package com.sliit.awardvote.nominee.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.nominee.model.Nomination;
import com.sliit.awardvote.nominee.model.NominationStatus;
import com.sliit.awardvote.nominee.model.Vote;
import com.sliit.awardvote.nominee.dao.VoteDao;
import com.sliit.awardvote.user.model.User;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService extends AbstractCrudService<Vote, Long> {

    private final VoteDao voteDao;

    public VoteService(VoteDao voteDao) {
        this.voteDao = voteDao;
    }

    @Override
    protected GenericDao<Vote, Long> getDao() {
        return voteDao;
    }

    /**
     * One vote per user PER CATEGORY (not per nominee): a public user may back
     * exactly one nominee within a given award category, but may still vote
     * once in every other category. Only allowed while the category's voting
     * window is open and the nomination has been approved.
     */
    public boolean castVote(Nomination nomination, User voter) {
        if (nomination.getStatus() != NominationStatus.APPROVED) return false;
        if (nomination.getCategory() == null) return false;
        if (!nomination.getCategory().isVotingOpen()) return false;
        if (voteDao.existsByCategoryIdAndVoterId(nomination.getCategory().getId(), voter.getId())) {
            return false; // already voted for a nominee in this category
        }
        voteDao.save(new Vote(nomination, voter));
        return true;
    }

    /** The nominee (if any) this voter already backed within the given category, for UI display. */
    public Optional<Vote> findVoteInCategory(Long categoryId, Long voterId) {
        return voteDao.findByCategoryIdAndVoterId(categoryId, voterId);
    }

    /** How many votes this user has cast across every category — used on the dashboard. */
    public long countVotesByUser(Long userId) {
        return voteDao.countByVoterId(userId);
    }
}
