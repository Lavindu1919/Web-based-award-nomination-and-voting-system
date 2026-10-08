package com.sliit.awardvote.nominee.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.nominee.model.Nomination;
import com.sliit.awardvote.nominee.model.NominationStatus;
import com.sliit.awardvote.nominee.model.Score;
import com.sliit.awardvote.nominee.dao.ScoreDao;
import com.sliit.awardvote.user.model.User;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScoreService extends AbstractCrudService<Score, Long> {

    private final ScoreDao scoreDao;

    public ScoreService(ScoreDao scoreDao) {
        this.scoreDao = scoreDao;
    }

    @Override
    protected GenericDao<Score, Long> getDao() {
        return scoreDao;
    }

    /**
     * One judged pick per JUDGE per CATEGORY (not one score per nominee): a judge
     * selects their single top nominee within a given award category — exactly
     * the same "one pick per category" rule that governs public Votes — but may
     * still judge once in every other category. Only allowed while the
     * category's judging is enabled and the nomination has been approved.
     */
    public boolean submitScore(Nomination nomination, User judge, double value, String comments) {
        if (nomination.getStatus() != NominationStatus.APPROVED) return false;
        if (nomination.getCategory() == null) return false;
        if (!nomination.getCategory().isJudgingEnabled()) return false;
        if (scoreDao.existsByCategoryIdAndJudgeId(nomination.getCategory().getId(), judge.getId())) {
            return false; // already judged a nominee in this category
        }
        scoreDao.save(new Score(nomination, judge, value, comments));
        return true;
    }

    /** The nominee (if any) this judge already picked within the given category, for UI display. */
    public Optional<Score> findScoreInCategory(Long categoryId, Long judgeId) {
        return scoreDao.findByCategoryIdAndJudgeId(categoryId, judgeId);
    }

    /** How many categories this judge has picked a nominee in — used on the dashboard. */
    public long countJudgedByUser(Long userId) {
        return scoreDao.countByJudgeId(userId);
    }
}
