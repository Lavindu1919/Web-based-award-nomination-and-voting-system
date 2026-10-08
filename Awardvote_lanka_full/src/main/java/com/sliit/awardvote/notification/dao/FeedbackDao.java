package com.sliit.awardvote.notification.dao;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.notification.model.Feedback;
import com.sliit.awardvote.notification.model.FeedbackStatus;

/** Data access contract for {@link Feedback}. */
public interface FeedbackDao extends GenericDao<Feedback, Long> {
    long countByStatus(FeedbackStatus status);
}
