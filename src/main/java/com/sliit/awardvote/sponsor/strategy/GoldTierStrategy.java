//This file sets the rules for how Gold sponsors appear on the webpage
package com.sliit.awardvote.sponsor.strategy;

import com.sliit.awardvote.sponsor.model.SponsorTier;

import org.springframework.stereotype.Component;

/** Gold: shown after Platinum with a large icon. */
// Defines GoldTierStrategy class, implementing SponsorTierStrategy interface
// Spring will detect this as a bean and inject it into the service layer
@Component
public class GoldTierStrategy implements SponsorTierStrategy {

    // Specifies that this strategy applies to the Gold tier
    @Override
    public SponsorTier tier() {
        return SponsorTier.GOLD;
    }

    // Gold sponsors have display priority 2 (shown after Platinum)
    @Override
    public int displayPriority() {
        return 2;
    }

    // CSS class for Gold sponsor icons (large size)
    @Override
    public String iconClass() {
        return "fs-2";
    }

    // Text description of Gold sponsor benefits (used in UI templates)
    @Override
    public String benefits() {
        return "High placement, large icon";
    }
}