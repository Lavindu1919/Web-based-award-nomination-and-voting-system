package com.sliit.awardvote.user.service;

import com.sliit.awardvote.notification.model.NotificationType;
import com.sliit.awardvote.user.model.OtpPurpose;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.util.UserFactory;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegistrationService {

    // Services and factory used during user registration
    private final UserService userService;
    private final OtpService otpService;
    private final UserFactory userFactory;

    // Constructor injection for required dependencies
    public RegistrationService(UserService userService, OtpService otpService, UserFactory userFactory) {
        this.userService = userService;
        this.otpService = otpService;
        this.userFactory = userFactory;
    }

    // Creates a new inactive public user account and sends a verification code
    public User register(User form, NotificationType channel) {

        // Create a public user with default role and inactive status
        User newUser = userFactory.createPublicUser(form.getFullName(), form.getEmail(), form.getUsername(), form.getPassword());

        // Add the user's phone number
        newUser.setPhone(form.getPhone());

        // Save the new account to the database
        User saved = userService.save(newUser);

        // Send an OTP for account verification
        sendVerificationCode(saved, channel);

        return saved;
    }

    // Resends a verification code to an inactive user
    public void resendCode(String identifier, NotificationType channel) {

        // Find the user using either username or email
        userService.findByUsernameOrEmail(identifier)

                // Only inactive accounts can request verification again
                .filter(u -> !u.isActive())

                // Send a new verification code
                .ifPresent(u -> sendVerificationCode(u, channel));
    }

    // Generates and sends the account verification OTP
    private void sendVerificationCode(User user, NotificationType channel) {

        // Message template used when sending the verification code
        String message = "Welcome to Award Vote Lanka! Your account verification code is {code}. "
                + "It expires in {minutes} minutes.";

        // Generate the OTP and send it through the selected channel
        otpService.issueAndSend(user, OtpPurpose.ACCOUNT_VERIFICATION, channel, message);
    }

    // Verifies the OTP and activates the user's account
    public Optional<User> verifyAndActivate(String identifier, String code) {

        // Find the user using username or email
        Optional<User> userOpt = userService.findByUsernameOrEmail(identifier);

        // Stop if the user does not exist
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        // If already active, no further verification is required
        if (user.isActive()) {
            return Optional.of(user);
        }

        // Verify the OTP before activating the account
        if (!otpService.verify(user, code, OtpPurpose.ACCOUNT_VERIFICATION)) {
            return Optional.empty();
        }

        // Activate the account after successful verification
        user.setActive(true);

        // Save the updated account status
        userService.save(user);

        return Optional.of(user);
    }
}