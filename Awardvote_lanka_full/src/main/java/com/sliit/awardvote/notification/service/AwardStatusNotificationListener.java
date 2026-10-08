package com.sliit.awardvote.notification.service;

import com.sliit.awardvote.award.event.AwardStatusChangedEvent;
import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.notification.model.NotificationType;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.service.UserService;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Observer design pattern: a second listener for AwardStatusChangedEvent.
 * AwardService only publishes the event; this listener turns it into a
 * Notification for every active user, so it shows up in their
 * "My Notifications" panel. AwardService needs no changes.
 */
@Component
public class AwardStatusNotificationListener {

    private final NotificationService notificationService;
    private final UserService userService;

    public AwardStatusNotificationListener(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @EventListener
    public void onAwardStatusChanged(AwardStatusChangedEvent event) {
        AwardProgramme programme = event.programme();
        String name = programme.getName();

        String message;
        switch (event.newStatus()) {
            case OPEN:
                message = "Nominations are now open for " + name + ".";
                break;
            case CLOSED:
                message = "Voting for " + name + " has closed.";
                break;
            case COMPLETED:
                message = name + " is complete. Thank you to everyone who took part.";
                break;
            default:
                return; // no notification for DRAFT
        }

        String related = "AwardProgramme#" + programme.getId();
        for (User user : userService.findAll()) {
            if (user.isActive()) {
                notificationService.notify(user, message, NotificationType.EMAIL, related);
            }
        }
    }
}
