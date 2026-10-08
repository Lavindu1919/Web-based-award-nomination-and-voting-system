package com.sliit.awardvote.notification.service;

import com.sliit.awardvote.nominee.event.NominationReviewedEvent;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Observer design pattern: listens for nomination review events and notifies
 * the submitter. NominationService only publishes the event and does not need
 * to know who is listening, so more observers can be added without changing it.
 */
@Component
public class NominationReviewListener {

    private final NotificationService notificationService;

    public NominationReviewListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void onNominationReviewed(NominationReviewedEvent event) {
        notificationService.notifyReviewDecision(event.nomination());
    }
}
