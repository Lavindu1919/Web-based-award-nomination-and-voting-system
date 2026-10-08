package com.sliit.awardvote.nominee.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.nominee.model.Vote;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class VoteDaoImpl extends AbstractJpaDao<Vote> implements VoteDao {

    public VoteDaoImpl() {
        super(Vote.class);
    }

    @Override
    public boolean existsByNominationIdAndVoterId(Long nominationId, Long voterId) {
        return em.createQuery("select count(e) from Vote e where e.nomination.id = :nominationId and e.voter.id = :voterId", Long.class)
                .setParameter("nominationId", nominationId)
                .setParameter("voterId", voterId)
                .getSingleResult() > 0;
    }

    @Override
    public boolean existsByCategoryIdAndVoterId(Long categoryId, Long voterId) {
        return em.createQuery("select count(e) from Vote e where e.category.id = :categoryId and e.voter.id = :voterId", Long.class)
                .setParameter("categoryId", categoryId)
                .setParameter("voterId", voterId)
                .getSingleResult() > 0;
    }

    @Override
    public Optional<Vote> findByCategoryIdAndVoterId(Long categoryId, Long voterId) {
        return em.createQuery("select e from Vote e where e.category.id = :categoryId and e.voter.id = :voterId", Vote.class)
                .setParameter("categoryId", categoryId)
                .setParameter("voterId", voterId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    @Override
    public long countByVoterId(Long voterId) {
        return em.createQuery("select count(e) from Vote e where e.voter.id = :voterId", Long.class)
                .setParameter("voterId", voterId)
                .getSingleResult();
    }
}
