package com.sliit.awardvote.notification.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.notification.model.Feedback;
import com.sliit.awardvote.notification.model.FeedbackStatus;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class FeedbackDaoImpl extends AbstractJpaDao<Feedback> implements FeedbackDao {

    public FeedbackDaoImpl() {
        super(Feedback.class);
    }

    @Override
    public long countByStatus(FeedbackStatus status) {
        return em.createQuery("select count(e) from Feedback e where e.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }
}
