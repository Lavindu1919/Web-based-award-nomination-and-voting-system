package com.sliit.awardvote.notification.service;

import com.sliit.awardvote.notification.model.Notification;

import org.springframework.stereotype.Component;

/**
 * Concrete strategy for EMAIL notifications.
 * In production this would call an SMTP client / provider such as SendGrid;
 * for the coursework prototype it simulates the send and logs it.
 */
@Component
public class EmailNotificationStrategy implements NotificationStrategy {

    @Override
    public void send(Notification notification) {
        // Simulated dispatch - swap this for real SMTP integration later.
        System.out.println("[EMAIL] To: " + notification.getRecipient().getEmail()
                + " | " + notification.getMessage());
        notification.markSent();
    }
}
