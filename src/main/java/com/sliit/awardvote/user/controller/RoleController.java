package com.sliit.awardvote.user.controller;

// Utility class used to get the currently logged-in user
import com.sliit.awardvote.common.util.SessionUtil;

// Models used for roles, permissions, and users
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.Role;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;

// DAO used to access user data
import com.sliit.awardvote.user.dao.UserDao;

// Service used to manage roles
import com.sliit.awardvote.user.service.RoleService;

// Provides default permissions for built-in roles
import com.sliit.awardvote.user.util.DefaultRolePermissions;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


 //Controller for managing roles and their permissions.
 //Only users with MANAGE_ROLES permission can access these functions.

@Controller
@RequestMapping("/roles")
public class RoleController {

    // Service for role-related operations
    private final RoleService roleService;

    // DAO for user-related database operations
    private final UserDao userDao;

    // Constructor injection for required dependencies
    public RoleController(RoleService roleService, UserDao userDao) {
        this.roleService = roleService;
        this.userDao = userDao;
    }

    // Checks whether the logged-in user has permission to manage roles
    private boolean canManageRoles(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_ROLES);
    }

    // Displays the list of all roles
    @GetMapping
    public String list(Model model, HttpSession session) {

        // Prevent unauthorized users from accessing role management
        if (!canManageRoles(session)) return "redirect:/dashboard";

        // Send all roles to the view
        model.addAttribute("roles", roleService.findAll());

        // Store default permissions for each built-in role
        Map<UserRole, Set<Permission>> builtInDefaults = new LinkedHashMap<>();

        // Get the default permissions for every built-in role
        for (UserRole ur : UserRole.values()) {
            builtInDefaults.put(ur, DefaultRolePermissions.forRole(ur));
        }

        // Send the default permissions to the view
        model.addAttribute("builtInDefaults", builtInDefaults);

        return "roles/list";
    }

    // Displays the form for creating a new role
    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {

        // Prevent unauthorized users from creating roles
        if (!canManageRoles(session)) return "redirect:/dashboard";

        // Create an empty Role object for the form
        model.addAttribute("role", new Role());

        // Send all available permissions to the form
        model.addAttribute("allPermissions", Permission.values());

        return "roles/form";
    }

    // Displays the form for editing an existing role
    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model,
            HttpSession session) {

        // Prevent unauthorized users from editing roles
        if (!canManageRoles(session)) return "redirect:/dashboard";

        // Find the role using its ID
        model.addAttribute(
                "role",
                roleService.findById(id).orElseThrow()
        );

        // Send all available permissions to the form
        model.addAttribute("allPermissions", Permission.values());

        return "roles/form";
    }

    // Saves a new role or updates an existing role
    @PostMapping("/save")
    public String save(@ModelAttribute Role role, HttpSession session) {

        // Prevent unauthorized users from saving roles
        if (!canManageRoles(session)) return "redirect:/dashboard";

        // Save the role and its selected permissions
        roleService.save(role);

        // Return to the role list after saving
        return "redirect:/roles";
    }

    // Deletes a role using its ID
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {

        // Prevent unauthorized users from deleting roles
        if (!canManageRoles(session)) return "redirect:/dashboard";

        // Find users currently assigned to this custom role
        List<User> holders = userDao.findByCustomRoleId(id);

        // Remove the role from every user before deleting it
        for (User u : holders) {
            u.setCustomRole(null);
            userDao.save(u);
        }

        // Delete the role after it has been unassigned from users
        roleService.deleteById(id);

        // Return to the role list
        return "redirect:/roles";
    }
}