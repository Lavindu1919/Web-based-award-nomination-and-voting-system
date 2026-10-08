package com.sliit.awardvote.nominee.event;

import com.sliit.awardvote.nominee.model.Nomination;

/** Event published when a nomination's review decision changes (Observer pattern). */
public record NominationReviewedEvent(Nomination nomination) {
}
