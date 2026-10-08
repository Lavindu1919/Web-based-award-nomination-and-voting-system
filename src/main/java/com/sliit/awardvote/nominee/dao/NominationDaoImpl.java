package com.sliit.awardvote.nominee.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.nominee.model.Nomination;
import com.sliit.awardvote.nominee.model.NominationStatus;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class NominationDaoImpl extends AbstractJpaDao<Nomination> implements NominationDao {

    public NominationDaoImpl() {
        super(Nomination.class);
    }

    @Override
    public List<Nomination> findByCategoryId(Long categoryId) {
        return em.createQuery("select e from Nomination e where e.category.id = :categoryId", Nomination.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public List<Nomination> findByStatus(NominationStatus status) {
        return em.createQuery("select e from Nomination e where e.status = :status", Nomination.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Nomination> findBySubmittedById(Long userId) {
        return em.createQuery("select e from Nomination e where e.submittedBy.id = :userId", Nomination.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public long countByStatus(NominationStatus status) {
        return em.createQuery("select count(e) from Nomination e where e.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }
}
