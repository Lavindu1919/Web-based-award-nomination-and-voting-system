//This file sets the rules for how Platinum sponsors appear on the webpage
package com.sliit.awardvote.sponsor.strategy;

import com.sliit.awardvote.sponsor.model.SponsorTier;

import org.springframework.stereotype.Component;

/** Platinum: shown first with the largest icon. */
// Defines PlatinumTierStrategy class, implementing SponsorTierStrategy interface
// Spring will detect this as a bean and inject it into the service layer
@Component
public class PlatinumTierStrategy implements SponsorTierStrategy {

    // Specifies that this strategy applies to the Platinum tier
    @Override
    public SponsorTier tier() {
        return SponsorTier.PLATINUM;
    }

    // Platinum sponsors have the highest priority (shown first in the list)
    @Override
    public int displayPriority() {
        return 1;
    }

    // CSS class for Platinum sponsor icons (largest size)
    @Override
    public String iconClass() {
        return "fs-1";
    }

    // Text description of Platinum sponsor benefits (used in UI templates)
    @Override
    public String benefits() {
        return "Top placement, largest icon";
    }
}