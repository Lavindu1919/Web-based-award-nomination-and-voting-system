package com.sliit.awardvote.user.service;

import com.sliit.awardvote.notification.model.NotificationType;
import com.sliit.awardvote.notification.service.NotificationService;
import com.sliit.awardvote.user.model.OtpCode;
import com.sliit.awardvote.user.model.OtpPurpose;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.dao.OtpCodeDao;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    // OTP remains valid for 10 minutes
    private static final int OTP_VALID_MINUTES = 10;

    // Secure random generator used to create OTP codes
    private static final SecureRandom RANDOM = new SecureRandom();

    // DAO for storing and retrieving OTP records
    private final OtpCodeDao otpCodeDao;

    // Service used to send OTP through Email or SMS
    private final NotificationService notificationService;

    // Constructor injection for required services
    public OtpService(OtpCodeDao otpCodeDao, NotificationService notificationService) {
        this.otpCodeDao = otpCodeDao;
        this.notificationService = notificationService;
    }

    // Generates, stores, and sends a new OTP
    public void issueAndSend(User user, OtpPurpose purpose, NotificationType preferredChannel, String messageText) {

        // Invalidate any previous unused OTP for the same purpose
        otpCodeDao.findByUserIdAndPurposeAndUsedFalse(
                user.getId(), purpose
        ).forEach(otp -> {
            otp.markUsed();
            otpCodeDao.save(otp);
        });

        // Generate a new 6-digit OTP
        String code = generateSixDigitCode();
        // Save the OTP with a 10-minute expiration time
        otpCodeDao.save(new OtpCode(user, code, purpose, LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES)));

        // Use the channel selected by the user
        NotificationType channel = preferredChannel;

        // If SMS is selected but no phone exists, use email instead
        if (channel == NotificationType.SMS
                && (user.getPhone() == null || user.getPhone().isBlank())) {
            channel = NotificationType.EMAIL;
        }

        // Replace OTP placeholders and send the notification
        notificationService.notify(
                user,
                messageText
                        .replace("{code}", code)
                        .replace(
                                "{minutes}",
                                String.valueOf(OTP_VALID_MINUTES)
                        ),
                channel,
                purpose.name()
        );
    }

    // Verifies an OTP and marks it as used when valid
    public boolean verify(User user, String code, OtpPurpose purpose) {

        // Find unused OTPs for this user and purpose
        return otpCodeDao.findByUserIdAndPurposeAndUsedFalse(user.getId(), purpose).stream()

                // Check whether the OTP matches and has not expired
                .filter(otp -> otp.isValid(code))

                // Use the first valid OTP found
                .findFirst()

                // Mark the verified OTP as used
                .map(otp -> {
                    otp.markUsed();
                    otpCodeDao.save(otp);
                    return true;
                })

                // Return false if no valid OTP was found
                .orElse(false);
    }

    // Generates a random 6-digit OTP
    private String generateSixDigitCode() {

        // Generate a number from 000000 to 999999
        int number = RANDOM.nextInt(1_000_000);

        // Convert the number to a 6-digit string
        return String.format("%06d", number);
    }
}