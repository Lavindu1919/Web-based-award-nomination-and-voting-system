package com.sliit.awardvote.notification.dao;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.notification.model.Notification;

import java.util.List;

/** Data access contract for {@link Notification}. */
public interface NotificationDao extends GenericDao<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    List<Notification> findAllByOrderByCreatedAtDesc();
}
