package com.sliit.awardvote.user.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.notification.model.NotificationType;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.service.RegistrationService;
import com.sliit.awardvote.user.service.UserService;
import com.sliit.awardvote.user.util.EmailValidator;

import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

//Controller for user registration and account verification.

@Controller
public class RegistrationController {

    // Minimum number of characters required for a password
    private static final int MIN_PASSWORD_LENGTH = 5;

    // Services used for user and registration operations
    private final UserService userService;
    private final RegistrationService registrationService;

    // Constructor injection for required services
    public RegistrationController(UserService userService, RegistrationService registrationService) {
        this.userService = userService;
        this.registrationService = registrationService;
    }

    // Displays the registration page
    @GetMapping("/register")
    public String registerPage(Model model) {

        // Create an empty User object for the registration form
        model.addAttribute("user", new User());

        return "register";
    }

    // Handles the registration form submission
    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           @RequestParam NotificationType channel,
                           Model model) {

        // Check whether the password meets the minimum length requirement
        if (user.getPassword() == null || user.getPassword().length() < MIN_PASSWORD_LENGTH) {
            model.addAttribute(
                    "error",
                    "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long."
            );
            return "register";
        }

        // Check whether the username is already registered
        if (userService.usernameTaken(user.getUsername())) {
            model.addAttribute("error", "That username is already taken.");
            return "register";
        }

        // Validate the user's email address
        Optional<String> emailError = userService.validateEmail(user.getEmail(), null);

        // Stop registration if the email is invalid or already used
        if (emailError.isPresent()) {
            model.addAttribute("error", emailError.get());
            return "register";
        }

        // Normalize the email before saving it
        user.setEmail(EmailValidator.normalize(user.getEmail()));

        try {
            // Register the user and send the verification code
            registrationService.register(user, channel);

        } catch (DataIntegrityViolationException ex) {

            // Handle cases where another user registered the same
            // username or email at the same time
            model.addAttribute(
                    "error",
                    "That username or email was just taken by someone else. Please try a different one."
            );
            return "register";
        }

        // Store registration information for the verification page
        model.addAttribute("identifier", user.getUsername());
        model.addAttribute("channel", channel);

        // Redirect the user to the OTP verification page
        return "verify-account";
    }

    // Verifies the OTP code and activates the account
    @PostMapping("/verify-account")
    public String verifyAccount(@RequestParam String identifier,
                                @RequestParam String code,
                                HttpSession session,
                                Model model) {

        // Verify the submitted code and activate the account
        Optional<User> verified = registrationService.verifyAndActivate(identifier, code);

        // Show an error if the OTP is invalid or expired
        if (verified.isEmpty()) {
            model.addAttribute("identifier", identifier);
            model.addAttribute(
                    "error",
                    "That code is invalid or has expired. Please request a new one."
            );
            return "verify-account";
        }

        // Log the verified user into the session automatically
        SessionUtil.login(session, verified.get());

        // Redirect the verified user to the dashboard
        return "redirect:/dashboard?verified";
    }

    // Sends a new verification code to the user
    @PostMapping("/verify-account/resend")
    public String resendVerificationCode(@RequestParam String identifier,
                                         @RequestParam NotificationType channel,
                                         Model model) {

        // Generate and send a new OTP through the selected channel
        registrationService.resendCode(identifier, channel);

        // Keep the verification information for the verification page
        model.addAttribute("identifier", identifier);
        model.addAttribute("channel", channel);

        // Tell the view that the code was successfully resent
        model.addAttribute("resent", true);

        return "verify-account";
    }
}