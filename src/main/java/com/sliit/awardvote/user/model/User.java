package com.sliit.awardvote.user.model;

import com.sliit.awardvote.common.model.Person;
import com.sliit.awardvote.user.util.DefaultRolePermissions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends Person {

    // Unique username used to identify the user
    @Column(unique = true, nullable = false)
    private String username;

    // Password is stored as a SHA-256 hash, not plain text
    @Column(nullable = false)
    private String password;

    // Store the user's built-in role
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // Controls whether the user account is active
    private boolean active = true;

    // Optional custom role assigned by an administrator
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "custom_role_id")
    private Role customRole;

    public User() {
    }

    // Create a user with the main account details and assigned role
    public User(String fullName, String email, String username, String password, UserRole role) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Return a description based on the user's built-in role
    @Override
    public String getRoleDescription() {
        return switch (role) {
            case SYSTEM_ADMIN -> "Manages user accounts, award categories, voting periods and system settings.";
            case AWARDS_STAFF -> "Reviews and approves nominations and runs the day-to-day award process.";
            case JUDGE -> "Evaluates approved nominees against defined judging criteria.";
            case MARKETING_OFFICER -> "Manages notifications, announcements and website content.";
            case SPONSOR_COORDINATOR -> "Manages sponsor and partner profiles and communications.";
            case NOMINEE -> "Views their own nomination details and tracks its status.";
            case PUBLIC_USER -> "Views award categories, submits nominations and votes where permitted.";
        };
    }

    // Check whether the user has permission to perform an action
    public boolean hasPermission(Permission permission) {

        // System administrators have all permissions
        if (role == UserRole.SYSTEM_ADMIN) {
            return true;
        }

        // Custom role permissions take priority over the default role permissions
        if (customRole != null) {
            return customRole.hasPermission(permission);
        }

        // Use the default permissions assigned to the built-in role
        return DefaultRolePermissions.forRole(role).contains(permission);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Role getCustomRole() {
        return customRole;
    }

    public void setCustomRole(Role customRole) {
        this.customRole = customRole;
    }
}