package com.sliit.awardvote.notification.service;

import com.sliit.awardvote.notification.model.Notification;

/**
 * NotificationStrategy - STRATEGY design pattern.
 *
 * OOP concept: POLYMORPHISM
 * NotificationService holds a reference of type NotificationStrategy and,
 * depending on the notification's type (EMAIL or SMS), calls send() on a
 * different concrete implementation without knowing or caring which one it is.
 */
public interface NotificationStrategy {
    void send(Notification notification);
}
