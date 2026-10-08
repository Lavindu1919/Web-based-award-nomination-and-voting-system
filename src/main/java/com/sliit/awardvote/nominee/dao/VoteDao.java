package com.sliit.awardvote.nominee.dao;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.nominee.model.Vote;

import java.util.Optional;

/** Data access contract for {@link Vote}. */
public interface VoteDao extends GenericDao<Vote, Long> {
    boolean existsByNominationIdAndVoterId(Long nominationId, Long voterId);

    /** Used to enforce "one vote per category" — checks across ALL nominees in a category. */
    boolean existsByCategoryIdAndVoterId(Long categoryId, Long voterId);

    /** Finds which nominee (if any) this voter already backed within a given category. */
    Optional<Vote> findByCategoryIdAndVoterId(Long categoryId, Long voterId);

    /** Total votes this user has cast across every category - used on the dashboard. */
    long countByVoterId(Long voterId);
}
