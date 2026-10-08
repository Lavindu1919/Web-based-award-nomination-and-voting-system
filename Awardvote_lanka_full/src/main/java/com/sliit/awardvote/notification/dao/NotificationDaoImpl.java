package com.sliit.awardvote.notification.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.notification.model.Notification;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class NotificationDaoImpl extends AbstractJpaDao<Notification> implements NotificationDao {

    public NotificationDaoImpl() {
        super(Notification.class);
    }

    @Override
    public List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId) {
        return em.createQuery("select e from Notification e where e.recipient.id = :recipientId order by e.createdAt desc", Notification.class)
                .setParameter("recipientId", recipientId)
                .getResultList();
    }

    @Override
    public List<Notification> findAllByOrderByCreatedAtDesc() {
        return em.createQuery("select e from Notification e order by e.createdAt desc", Notification.class)
                .getResultList();
    }
}
