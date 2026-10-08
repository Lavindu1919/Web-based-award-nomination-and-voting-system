package com.sliit.awardvote.user.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;
import com.sliit.awardvote.user.service.RoleService;
import com.sliit.awardvote.user.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    // Constructor to inject UserService and RoleService
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Check whether the logged-in user has permission to manage users

    private boolean canManageUsers(HttpSession session) {
        User u = SessionUtil.currentUser(session);

        // Return true if user exists and has MANAGE_USERS permission
        return u != null && u.hasPermission(Permission.MANAGE_USERS);
    }

    // Find and display all users
    @GetMapping
    public String list(Model model, HttpSession session) {
        // Check user permission
        if (!canManageUsers(session))

            return "redirect:/dashboard";

        // Find all users from the database
        model.addAttribute("users", userService.findAll());

        // Open the users list page
        return "users/list";
    }


    // Open the form to create a new user
    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {

        // Check user permission
        if (!canManageUsers(session)) return "redirect:/dashboard";

        // Create an empty User object for the form
        model.addAttribute("user", new User());

        // Find all available system roles
        model.addAttribute("roles", UserRole.values());

        // Find all custom roles from the database
        model.addAttribute("customRoles", roleService.findAll());

        // Open the user form page
        return "users/form";
    }

    // Find a user by ID and open the edit form
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {

        // Check user permission
        if (!canManageUsers(session)) return "redirect:/dashboard";
        // Find the user using the given ID

        model.addAttribute("user", userService.findById(id).orElseThrow());

        // Find all available system roles
        model.addAttribute("roles", UserRole.values());

        // Find all custom roles from the database
        model.addAttribute("customRoles", roleService.findAll());

        // Open the user form page
        return "users/form";
    }

    // Save a new user or update an existing user
    @PostMapping("/save")
    public String save(@ModelAttribute User user,
                       @RequestParam(required = false) Long customRoleId,
                       HttpSession session) {

        // Check user permission
        if (!canManageUsers(session))
            return "redirect:/dashboard";

        // Find the selected custom role by ID
        user.setCustomRole(
                customRoleId != null
                        ? roleService.findById(customRoleId).orElse(null)
                        : null
        );

        // Save the user in the database
        userService.save(user);

        // Return to the users list
        return "redirect:/users";
    }

    // Find a user by ID and change their active status
    @GetMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id,
                         HttpSession session) {

        // Check user permission
        if (!canManageUsers(session))
            return "redirect:/dashboard";

        // Find the user and change active/inactive status
        userService.toggleActive(id);

        // Return to the users list
        return "redirect:/users";
    }

    // Permanently delete a user
    // POST is used so that a normal link or browser prefetch cannot delete a user
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        // Check user permission
        if (!canManageUsers(session))
            return "redirect:/dashboard";

        // Find the currently logged-in user
        User actor = SessionUtil.currentUser(session);

        // Prevent an admin from deleting their own account here
        if (actor.getId().equals(id)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "To delete your own account, use My Profile > Delete My Account (it asks for your password)."
            );

            return "redirect:/users";
        }

        // Find the user that should be deleted
        User target = userService.findById(id).orElse(null);

        // Check whether the user exists
        if (target == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "That account no longer exists."
            );

            return "redirect:/users";
        }

        // Check whether the current user is allowed to delete the target user
        Optional<String> refusal =
                userService.validateDeletion(actor, target);

        // If deletion is not allowed, show the reason
        if (refusal.isPresent()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    refusal.get()
            );

            return "redirect:/users";
        }

        // Permanently delete the user from the database
        userService.deletePermanently(id);

        // Show a success message
        redirectAttributes.addFlashAttribute(
                "success",
                "Account \"" + target.getUsername() + "\" was permanently deleted."
        );

        // Return to the users list
        return "redirect:/users";
    }
}
