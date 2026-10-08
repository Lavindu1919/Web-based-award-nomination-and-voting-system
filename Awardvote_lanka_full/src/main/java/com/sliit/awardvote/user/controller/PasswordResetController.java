package com.sliit.awardvote.user.controller;

import com.sliit.awardvote.notification.model.NotificationType;
import com.sliit.awardvote.user.service.PasswordResetService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Forgot password (OTP via Email or SMS). */
@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // Handles GET requests to /forgot-password.
    // This method is normally called when the user opens the "Forgot Password" page.
    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot-password";
    }

    // Handles POST requests sent to /forgot-password.
    // This happens when the user submits the forgot-password form.
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String identifier, @RequestParam NotificationType channel, Model model) {
        passwordResetService.requestReset(identifier, channel);
        // Always show the same confirmation screen, whether or not the identifier
        // matched an account, so the form can't be used to probe who is registered.
        model.addAttribute("identifier", identifier);
        model.addAttribute("channel", channel);
        return "reset-password";
    }

    // Handles POST requests sent to /reset-password.
    // This method is called when the user submits the OTP and their new password.
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String identifier,
                                 @RequestParam String code,
                                 @RequestParam String newPassword,
                                 Model model) {

        // Calls the service to:
        // 1. Check whether the OTP is correct.
        // 2. Check whether the OTP has expired.
        // 3. Reset the user's password if the OTP is valid.
        // The method returns true if the password was successfully reset.
        boolean success = passwordResetService.verifyAndReset(identifier, code, newPassword);
        if (success) {
            return "redirect:/login?resetSuccess";
        }

        // Adds an error message to the model.
        // The reset-password page can display this message to the user.
        model.addAttribute("identifier", identifier);
        model.addAttribute("error", "That code is invalid or has expired. Please request a new one.");
        return "reset-password";
    }
}
