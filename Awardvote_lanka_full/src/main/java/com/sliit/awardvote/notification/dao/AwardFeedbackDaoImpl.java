package com.sliit.awardvote.notification.dao;

import com.sliit.awardvote.notification.model.AwardFeedback;
import com.sliit.awardvote.common.dao.AbstractJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class AwardFeedbackDaoImpl extends AbstractJpaDao<AwardFeedback> implements AwardFeedbackDao {

    public AwardFeedbackDaoImpl() {
        super(AwardFeedback.class);
    }

    @Override
    public List<AwardFeedback> findByAwardProgrammeId(Long programmeId) {
        return em.createQuery(
                "select e from AwardFeedback e where e.awardProgramme.id = :programmeId order by e.createdAt desc",
                AwardFeedback.class)
                .setParameter("programmeId", programmeId)
                .getResultList();
    }
}
