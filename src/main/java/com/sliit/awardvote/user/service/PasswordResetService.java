package com.sliit.awardvote.user.service;

import com.sliit.awardvote.notification.model.NotificationType;
import com.sliit.awardvote.user.model.OtpPurpose;
import com.sliit.awardvote.user.model.User;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling the forgot-password and password-reset process.
 */
@Service
public class PasswordResetService {

    // Services used for user lookup and OTP operations
    private final UserService userService;
    private final OtpService otpService;

    // Constructor injection for required services
    public PasswordResetService(UserService userService, OtpService otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    // Starts the password reset process using username or email
    public void requestReset(String identifier, NotificationType channel) {

        // Find the user using their username or email
        Optional<User> userOpt = userService.findByUsernameOrEmail(identifier);

        // Do nothing if the account does not exist
        // This prevents revealing whether an account is registered
        if (userOpt.isEmpty()) {
            return;
        }

        // Message template for the password reset OTP
        String message = "Your Award Vote Lanka password reset code is {code}. "
                        + "It expires in {minutes} minutes. "
                        + "If you didn't request this, you can safely ignore it.";

        // Generate and send the password reset OTP
        otpService.issueAndSend(
                userOpt.get(),
                OtpPurpose.PASSWORD_RESET,
                channel,
                message
        );
    }

    // Verifies the OTP and changes the user's password
    public boolean verifyAndReset(String identifier, String code, String newPassword) {

        // Find the user using username or email
        Optional<User> userOpt = userService.findByUsernameOrEmail(identifier);

        // Return false if the account does not exist
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        // Verify the password-reset OTP
        if (!otpService.verify(user, code, OtpPurpose.PASSWORD_RESET)) {
            return false;
        }

        // Set the new raw password
        // UserService hashes it automatically before saving
        user.setPassword(newPassword);

        // Save the updated password
        userService.save(user);

        // Password reset completed successfully
        return true;
    }
}