package com.sliit.awardvote.nominee.model;

import com.sliit.awardvote.award.model.Category;
import com.sliit.awardvote.common.model.BaseEntity;
import com.sliit.awardvote.user.model.User;

import jakarta.persistence.*;

/**
 * Score - one judge's evaluation "vote" for a Nomination.
 *
 * BUSINESS RULE: exactly like a public Vote, a judge may score exactly ONE
 * nominee per award CATEGORY (not one score per nominee) — a judge picks
 * their single top nominee in each category rather than rating every
 * nominee that was submitted. This mirrors the public voting rule
 * (see Vote) so both roles follow the same "one pick per category" logic.
 *
 * Deliberately has NO setters for nomination/judge/category/scoreValue: once
 * a judgement is recorded it cannot be reassigned or edited, only created or
 * removed by an administrator, protecting the audit trail described in the
 * proposal's "Data Integrity" non-functional requirement.
 */
@Entity
@Table(name = "scores", uniqueConstraints = {
        @UniqueConstraint(name = "uk_score_nomination_judge", columnNames = {"nomination_id", "judge_id"}),
        @UniqueConstraint(name = "uk_score_category_judge", columnNames = {"category_id", "judge_id"})
})
public class Score extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nomination_id", nullable = false)
    private Nomination nomination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id", nullable = false)
    private User judge;

    /** Denormalized copy of nomination.getCategory() so the DB can enforce one pick per category — see class javadoc. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private double scoreValue;

    @Column(length = 1000)
    private String comments;

    protected Score() {
        // required by JPA
    }

    public Score(Nomination nomination, User judge, double scoreValue, String comments) {
        this.nomination = nomination;
        this.judge = judge;
        this.category = nomination.getCategory();
        this.scoreValue = scoreValue;
        this.comments = comments;
    }

    public Nomination getNomination() {
        return nomination;
    }

    public User getJudge() {
        return judge;
    }

    public Category getCategory() {
        return category;
    }

    public double getScoreValue() {
        return scoreValue;
    }

    public String getComments() {
        return comments;
    }
}
