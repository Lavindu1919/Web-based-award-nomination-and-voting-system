//This file sets the rules for how Bronze sponsors appear on the webpage
package com.sliit.awardvote.sponsor.strategy;

import com.sliit.awardvote.sponsor.model.SponsorTier;

import org.springframework.stereotype.Component;

/** Bronze: listed last with a small icon. */
// Defines BronzeTierStrategy class, implementing SponsorTierStrategy interface
// Spring will detect this as a bean and inject it into the service layer
@Component
public class BronzeTierStrategy implements SponsorTierStrategy {

    // Specifies that this strategy applies to the Bronze tier
    @Override
    public SponsorTier tier() {
        return SponsorTier.BRONZE;
    }

    // Bronze sponsors have the lowest priority (shown last in the list)
    @Override
    public int displayPriority() {
        return 4;
    }

    // CSS class for Bronze sponsor icons (small size)
    @Override
    public String iconClass() {
        return "fs-5";
    }

    // Text description of Bronze sponsor benefits (used in UI templates)
    @Override
    public String benefits() {
        return "Listed after higher tiers, small icon";
    }
}