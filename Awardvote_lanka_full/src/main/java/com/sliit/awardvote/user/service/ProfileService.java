package com.sliit.awardvote.user.service;

import com.sliit.awardvote.common.util.PasswordUtil;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.util.EmailValidator;

import org.springframework.stereotype.Service;

import java.util.Optional;

/** Service for users to manage their own profile and account. */
@Service
public class ProfileService {

    // Service used for user-related database operations
    private final UserService userService;

    // Constructor injection for UserService
    public ProfileService(UserService userService) {
        this.userService = userService;
    }

    // Retrieves a user by their ID
    public User getUser(Long userId) {
        return userService.findById(userId).orElseThrow();
    }

    // Checks whether the new username or email is already used by another account
    public Optional<String> validateUpdate(User user, String username, String email) {

        // Check if the new username is different and already taken
        if (!username.equals(user.getUsername())
                && userService.usernameTaken(username)) {
            return Optional.of("That username is already taken.");
        }

        // Validate the email and exclude the current user's own email
        return userService.validateEmail(email, user.getId());
    }

    // Updates the user's profile information
    public User updateProfile(User user, String fullName, String email, String phone, String username, String newPassword) {

        // Update the user's basic profile information
        user.setFullName(fullName);
        user.setEmail(EmailValidator.normalize(email));
        user.setPhone(phone);
        user.setUsername(username);

        // Update the password only when a new password was provided
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(newPassword);
        }

        // Save and return the updated user
        return userService.save(user);
    }

    // Deletes the user's own account after verifying their current password
    public Optional<String> deleteAccount(User user, String currentPassword) {

        // Verify the current password before allowing account deletion
        if (!PasswordUtil.matches(currentPassword, user.getPassword())) {
            return Optional.of(
                    "That password is incorrect - your account was not deleted."
            );
        }

        // Check whether any system rules prevent the account from being deleted
        Optional<String> refusal = userService.validateDeletion(user, user);

        // Stop deletion if a business rule is violated
        if (refusal.isPresent()) {
            return refusal;
        }

        // Permanently remove the user's account
        userService.deletePermanently(user.getId());

        // Empty means the deletion was successful
        return Optional.empty();
    }
}