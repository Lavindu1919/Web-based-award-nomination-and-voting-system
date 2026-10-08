package com.sliit.awardvote.notification.service;

import com.sliit.awardvote.notification.model.Notification;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Concrete strategy for SMS notifications (Strategy pattern).
 *
 * Sends real text messages through Twilio when credentials are configured in
 * application.properties (twilio.account-sid / twilio.auth-token /
 * twilio.from-number). If any of those are left blank - the default, out of
 * the box - it falls back to logging the message to the console instead of
 * failing, so the app works immediately with zero setup and only needs
 * configuring once someone actually wants real texts delivered. See the
 * README ("Real SMS delivery via Twilio") for the five-minute setup.
 */
@Component
public class SmsNotificationStrategy implements NotificationStrategy {

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.from-number:}")
    private String fromNumber;

    /** Default country code used when a phone number is stored without one (e.g. "0771234567"). */
    @Value("${twilio.default-country-code:+94}")
    private String defaultCountryCode;

    private boolean twilioConfigured;

    @PostConstruct
    void init() {
        twilioConfigured = isPresent(accountSid) && isPresent(authToken) && isPresent(fromNumber);
        if (twilioConfigured) {
            Twilio.init(accountSid, authToken);
        }
    }

    @Override
    public void send(Notification notification) {
        String phone = notification.getRecipient().getPhone();

        if (!twilioConfigured) {
            System.out.println("[SMS - SIMULATED, Twilio not configured] To: "
                    + (phone != null ? phone : "(no phone on file)") + " | " + notification.getMessage());
            notification.markSent();
            return;
        }

        if (phone == null || phone.isBlank()) {
            System.out.println("[SMS] Cannot send - no phone number on file for " + notification.getRecipient().getEmail());
            notification.markFailed();
            return;
        }

        try {
            String toNumber = toE164(phone);
            Message.creator(new PhoneNumber(toNumber), new PhoneNumber(fromNumber), notification.getMessage()).create();
            System.out.println("[SMS] Sent via Twilio to " + toNumber);
            notification.markSent();
        } catch (Exception e) {
            System.out.println("[SMS] Twilio send failed (" + e.getMessage() + ") - message was: " + notification.getMessage());
            notification.markFailed();
        }
    }

    /** Normalizes a locally-entered number (e.g. "0771234567") to E.164 (e.g. "+94771234567") for Twilio. */
    private String toE164(String phone) {
        String trimmed = phone.trim().replace(" ", "");
        if (trimmed.startsWith("+")) {
            return trimmed;
        }
        if (trimmed.startsWith("0")) {
            return defaultCountryCode + trimmed.substring(1);
        }
        return defaultCountryCode + trimmed;
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }
}
