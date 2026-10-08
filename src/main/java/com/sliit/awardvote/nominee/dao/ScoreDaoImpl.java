package com.sliit.awardvote.nominee.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.nominee.model.Score;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class ScoreDaoImpl extends AbstractJpaDao<Score> implements ScoreDao {

    public ScoreDaoImpl() {
        super(Score.class);
    }

    @Override
    public boolean existsByNominationIdAndJudgeId(Long nominationId, Long judgeId) {
        return em.createQuery("select count(e) from Score e where e.nomination.id = :nominationId and e.judge.id = :judgeId", Long.class)
                .setParameter("nominationId", nominationId)
                .setParameter("judgeId", judgeId)
                .getSingleResult() > 0;
    }

    @Override
    public boolean existsByCategoryIdAndJudgeId(Long categoryId, Long judgeId) {
        return em.createQuery("select count(e) from Score e where e.category.id = :categoryId and e.judge.id = :judgeId", Long.class)
                .setParameter("categoryId", categoryId)
                .setParameter("judgeId", judgeId)
                .getSingleResult() > 0;
    }

    @Override
    public Optional<Score> findByCategoryIdAndJudgeId(Long categoryId, Long judgeId) {
        return em.createQuery("select e from Score e where e.category.id = :categoryId and e.judge.id = :judgeId", Score.class)
                .setParameter("categoryId", categoryId)
                .setParameter("judgeId", judgeId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    @Override
    public long countByJudgeId(Long judgeId) {
        return em.createQuery("select count(e) from Score e where e.judge.id = :judgeId", Long.class)
                .setParameter("judgeId", judgeId)
                .getSingleResult();
    }
}
