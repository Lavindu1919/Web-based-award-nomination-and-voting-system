package com.sliit.awardvote.notification.model;

import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.common.model.BaseEntity;
import com.sliit.awardvote.user.model.User;

import jakarta.persistence.*;

/**
 * A public comment/feedback entry left by a logged-in user on a specific
 * {@link AwardProgramme}. Every visitor can read the thread; only the
 * author (tracked via {@link #user}) may edit or delete their own entry.
 */
@Entity
@Table(name = "award_feedback")
public class AwardFeedback extends BaseEntity {

    @Column(length = 1000, nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "award_programme_id", nullable = false)
    private AwardProgramme awardProgramme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AwardProgramme getAwardProgramme() {
        return awardProgramme;
    }

    public void setAwardProgramme(AwardProgramme awardProgramme) {
        this.awardProgramme = awardProgramme;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
