package com.sliit.awardvote.user.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

// Login and logout. Registration and password reset have their own controllers
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Handles GET requests to /login
    // This method is called when the user opens the login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Handles POST requests to /login
    // This method processes the login form data

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        // Calls the authenticate method from UserService
        // It checks whether the username and password are valid
        Optional<User> authenticated = userService.authenticate(username, password);

        // Checks whether authentication failed
        // isEmpty() returns true if no authenticated user was found
        if (authenticated.isEmpty()) {
            model.addAttribute("error", "Invalid username or password, the account is deactivated, or it's still awaiting OTP verification.");
            return "login";
        }
        SessionUtil.login(session, authenticated.get());
        return "redirect:/dashboard";
    }
    // Handles GET requests to /logout
    // This method is called when the user logs out
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SessionUtil.logout(session);
        return "redirect:/";
    }
}
