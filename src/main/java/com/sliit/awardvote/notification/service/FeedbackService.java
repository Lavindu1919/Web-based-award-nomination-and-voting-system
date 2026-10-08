package com.sliit.awardvote.notification.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.notification.model.Feedback;
import com.sliit.awardvote.notification.model.FeedbackStatus;
import com.sliit.awardvote.notification.dao.FeedbackDao;

import org.springframework.stereotype.Service;

@Service
public class FeedbackService extends AbstractCrudService<Feedback, Long> {

    private final FeedbackDao feedbackDao;

    public FeedbackService(FeedbackDao feedbackDao) {
        this.feedbackDao = feedbackDao;
    }

    @Override
    protected GenericDao<Feedback, Long> getDao() {
        return feedbackDao;
    }

    @Override
    protected void beforeSave(Feedback feedback) {
        if (feedback.getId() == null && feedback.getStatus() == null) {
            feedback.setStatus(FeedbackStatus.OPEN);
        }
    }

    public void respond(Long feedbackId, String response, com.sliit.awardvote.user.model.User handledBy) {
        Feedback feedback = feedbackDao.findById(feedbackId).orElseThrow();
        feedback.setResponse(response);
        feedback.setHandledBy(handledBy);
        feedback.setStatus(FeedbackStatus.RESOLVED);
        feedbackDao.save(feedback);
    }

    /** Open (unresolved) inquiries — used on the dashboard for whoever handles feedback. */
    public long countOpen() {
        return feedbackDao.countByStatus(FeedbackStatus.OPEN);
    }
}
