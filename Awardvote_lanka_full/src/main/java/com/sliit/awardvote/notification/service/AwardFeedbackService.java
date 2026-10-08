package com.sliit.awardvote.notification.service;

import com.sliit.awardvote.notification.dao.AwardFeedbackDao;
import com.sliit.awardvote.notification.model.AwardFeedback;
import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AwardFeedbackService extends AbstractCrudService<AwardFeedback, Long> {

    private final AwardFeedbackDao awardFeedbackDao;

    public AwardFeedbackService(AwardFeedbackDao awardFeedbackDao) {
        this.awardFeedbackDao = awardFeedbackDao;
    }

    @Override
    protected GenericDao<AwardFeedback, Long> getDao() {
        return awardFeedbackDao;
    }

    public List<AwardFeedback> findByProgramme(Long programmeId) {
        return awardFeedbackDao.findByAwardProgrammeId(programmeId);
    }
}
