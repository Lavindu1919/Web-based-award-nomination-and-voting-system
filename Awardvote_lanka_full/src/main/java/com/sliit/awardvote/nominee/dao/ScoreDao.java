package com.sliit.awardvote.nominee.dao;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.nominee.model.Score;

import java.util.Optional;

/** Data access contract for {@link Score}. */
public interface ScoreDao extends GenericDao<Score, Long> {
    boolean existsByNominationIdAndJudgeId(Long nominationId, Long judgeId);

    /** Used to enforce "one judged pick per category" — checks across ALL nominees in a category. */
    boolean existsByCategoryIdAndJudgeId(Long categoryId, Long judgeId);

    /** Finds which nominee (if any) this judge already picked within a given category. */
    Optional<Score> findByCategoryIdAndJudgeId(Long categoryId, Long judgeId);

    /** Total picks this judge has made across every category - used on the dashboard. */
    long countByJudgeId(Long judgeId);
}
