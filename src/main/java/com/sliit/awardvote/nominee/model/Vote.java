package com.sliit.awardvote.nominee.model;

import com.sliit.awardvote.award.model.Category;
import com.sliit.awardvote.common.model.BaseEntity;
import com.sliit.awardvote.user.model.User;

import jakarta.persistence.*;

/**
 * Vote - a single public vote cast for a Nomination.
 *
 * Deliberately has NO setters for nomination/voter/category: once a vote is
 * cast it cannot be reassigned or tampered with, only created or deleted by
 * an administrator. This protects the integrity of the tally described in
 * the proposal's "Data Integrity" non-functional requirement.
 *
 * BUSINESS RULE: a public user may cast exactly ONE vote per award CATEGORY
 * (not per nominee). The category is stored directly on the Vote row (in
 * addition to being reachable via nomination.category) purely so the
 * database itself can enforce "one vote per category per voter" with a
 * unique constraint — application logic in VoteService enforces the
 * same rule, but this is defense-in-depth against a bug or a direct SQL
 * insert bypassing the service layer.
 */
@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_vote_nomination_voter", columnNames = {"nomination_id", "voter_id"}),
        @UniqueConstraint(name = "uk_vote_category_voter", columnNames = {"category_id", "voter_id"})
})
public class Vote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nomination_id", nullable = false)
    private Nomination nomination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    private User voter;

    /** Denormalized copy of nomination.getCategory() — see class javadoc. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    protected Vote() {
        // required by JPA
    }

    public Vote(Nomination nomination, User voter) {
        this.nomination = nomination;
        this.voter = voter;
        this.category = nomination.getCategory();
    }

    public Nomination getNomination() {
        return nomination;
    }

    public User getVoter() {
        return voter;
    }

    public Category getCategory() {
        return category;
    }
}
