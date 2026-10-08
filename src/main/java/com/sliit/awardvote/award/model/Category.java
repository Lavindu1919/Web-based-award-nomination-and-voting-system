package com.sliit.awardvote.award.model;

import com.sliit.awardvote.common.model.BaseEntity;
import com.sliit.awardvote.nominee.model.Nomination;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * MODULE 2: AWARD MANAGEMENT (continued)
 * A Category belongs to exactly one AwardProgramme and owns many Nominations.
 *
 * Relationships:
 *  - MANY-TO-ONE with AwardProgramme
 *  - ONE-TO-MANY with Nomination
 */
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(nullable = false) //Store the category name
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000) //The criteria defining who is eligible for this category.
    private String eligibilityCriteria;

    private LocalDateTime votingStart;
    private LocalDateTime votingEnd;

    private boolean judgingEnabled = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id")
    private AwardProgramme awardProgramme;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Nomination> nominations = new HashSet<>();

    public boolean isVotingOpen() {
        LocalDateTime now = LocalDateTime.now();
        return votingStart != null && votingEnd != null
                && now.isAfter(votingStart) && now.isBefore(votingEnd);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEligibilityCriteria() {
        return eligibilityCriteria;
    }

    public void setEligibilityCriteria(String eligibilityCriteria) {
        this.eligibilityCriteria = eligibilityCriteria;
    }

    public LocalDateTime getVotingStart() {
        return votingStart;
    }

    public void setVotingStart(LocalDateTime votingStart) {
        this.votingStart = votingStart;
    }

    public LocalDateTime getVotingEnd() {
        return votingEnd;
    }

    public void setVotingEnd(LocalDateTime votingEnd) {
        this.votingEnd = votingEnd;
    }

    public boolean isJudgingEnabled() {
        return judgingEnabled;
    }

    public void setJudgingEnabled(boolean judgingEnabled) {
        this.judgingEnabled = judgingEnabled;
    }

    public AwardProgramme getAwardProgramme() {
        return awardProgramme;
    }

    public void setAwardProgramme(AwardProgramme awardProgramme) {
        this.awardProgramme = awardProgramme;
    }

    public Set<Nomination> getNominations() {
        return nominations;
    }

    public void setNominations(Set<Nomination> nominations) {
        this.nominations = nominations;
    }
}
