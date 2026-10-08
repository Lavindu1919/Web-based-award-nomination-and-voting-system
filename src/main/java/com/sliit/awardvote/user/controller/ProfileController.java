package com.sliit.awardvote.user.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.service.ProfileService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

// Defines the common URL path for all methods in this controller.
// Therefore, all URLs in this class start with /profile.
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    // Creates a reference to ProfileService.
    // The service handles the actual profile-related business logic.
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }


    // Handles GET requests to /profile.
    // This method is used to display the user's profile page.
    @GetMapping
    public String view(Model model, HttpSession session) {
        model.addAttribute("user", SessionUtil.currentUser(session));
        return "users/profile";
    }

    // Handles POST requests to /profile/update.
    // This method is called when the user submits the profile update form.
    @PostMapping("/update")
    public String update(@RequestParam String fullName,
                          @RequestParam String email,
                          @RequestParam(required = false) String phone,
                          @RequestParam String username,
                          @RequestParam(required = false) String newPassword,
                          HttpSession session,
                          Model model) {
        User current = SessionUtil.currentUser(session);
        User user = profileService.getUser(current.getId());

        // Validates the username and email before updating the profile.
        // validateUpdate() returns an Optional<String>.
        // If there is an error, it contains an error message.
        // If there is no error, it is empty.

        Optional<String> error = profileService.validateUpdate(user, username, email);
        if (error.isPresent()) {
            model.addAttribute("user", user);
            model.addAttribute("error", error.get());
            return "users/profile";
        }

        // Updates the user's profile information.
        User saved = profileService.updateProfile(user, fullName, email, phone, username, newPassword);
        SessionUtil.login(session, saved);

        // Updates the user's login session with the newly saved user information.
        model.addAttribute("user", saved);
        model.addAttribute("success", "Your profile has been updated.");
        return "users/profile";
    }


    // Handles POST requests to /profile/delete.
    // This method is called when the user wants to delete their account.
    @PostMapping("/delete")
    public String delete(@RequestParam String currentPassword,
                          HttpSession session,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        User current = SessionUtil.currentUser(session);

        // Gets the complete user information from the database using the current user's ID.
        User user = profileService.getUser(current.getId());

        // Attempts to delete the user's account.
        // The service checks the current password before deleting.
        Optional<String> error = profileService.deleteAccount(user, currentPassword);
        if (error.isPresent()) {
            model.addAttribute("user", user);
            model.addAttribute("error", error.get());
            return "users/profile";
        }

        // Logs the user out of the current session.
        // This removes the user's login/session information after the account has been deleted.
        SessionUtil.logout(session);
        redirectAttributes.addFlashAttribute("accountDeleted", true);
        return "redirect:/login";
    }
}
