package com.sliit.awardvote.award.event;

import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.award.model.AwardStatus;

/** Event published when an existing award programme moves to a new status . */
public record AwardStatusChangedEvent(AwardProgramme programme, AwardStatus oldStatus) {

    public AwardStatus newStatus() {
        return programme.getStatus();
    }
}
