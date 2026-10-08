package com.sliit.awardvote.nominee.model;

import com.sliit.awardvote.award.model.Category;
import com.sliit.awardvote.common.model.BaseEntity;
import com.sliit.awardvote.user.model.User;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * MODULE 3: NOMINEE MANAGEMENT
 * Presented by: Jayalath S.V. (IT25100138)
 *
 * Handles submission, review and tracking of nominations before they become
 * eligible for voting or judging.
 *
 * Relationships:
 *  - MANY-TO-ONE with Category
 *  - MANY-TO-ONE with User (the person who submitted the nomination)
 *  - ONE-TO-MANY with Vote (public votes cast for this nominee)
 *  - ONE-TO-MANY with Score (judge evaluations for this nominee)
 */
@Entity
@Table(name = "nominations")
public class Nomination extends BaseEntity {

    @Column(nullable = false)
    private String nomineeName;

    @Column(nullable = false)
    private String nomineeEmail;

    @Column(length = 2000)
    private String description;

    private String evidenceUrl;

    @Enumerated(EnumType.STRING)
    private NominationStatus status = NominationStatus.SUBMITTED;

    @Column(length = 1000)
    private String reviewComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedBy;

    @OneToMany(mappedBy = "nomination", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes = new HashSet<>();

    @OneToMany(mappedBy = "nomination", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Score> scores = new HashSet<>();

    /** Derived read-only stat, useful directly in Thymeleaf views. */
    public int getVoteCount() {
        return votes.size();
    }

    public double getAverageScore() {
        return scores.stream().mapToDouble(Score::getScoreValue).average().orElse(0.0);
    }

    /**
     * This nominee's vote count as a percentage of the highest vote count among
     * approved nominees in the same category — purely a UI convenience used to
     * render the animated vote-share micro-bar (0-100).
     */
    public int getVoteSharePercent() {
        if (category == null) return 0;
        int max = category.getNominations().stream()
                .filter(n -> n.getStatus() == NominationStatus.APPROVED)
                .mapToInt(Nomination::getVoteCount)
                .max()
                .orElse(0);
        if (max == 0) return 0;
        return (int) Math.round((getVoteCount() * 100.0) / max);
    }

    public String getNomineeName() {
        return nomineeName;
    }

    public void setNomineeName(String nomineeName) {
        this.nomineeName = nomineeName;
    }

    public String getNomineeEmail() {
        return nomineeEmail;
    }

    public void setNomineeEmail(String nomineeEmail) {
        this.nomineeEmail = nomineeEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEvidenceUrl() {
        return evidenceUrl;
    }

    public void setEvidenceUrl(String evidenceUrl) {
        this.evidenceUrl = evidenceUrl;
    }

    public NominationStatus getStatus() {
        return status;
    }

    public void setStatus(NominationStatus status) {
        this.status = status;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(User submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }
}
