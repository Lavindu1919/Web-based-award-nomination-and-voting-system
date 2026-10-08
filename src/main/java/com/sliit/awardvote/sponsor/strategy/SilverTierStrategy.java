//This file sets the rules for how Silver sponsors appear on the webpage.
package com.sliit.awardvote.sponsor.strategy;

import com.sliit.awardvote.sponsor.model.SponsorTier;

import org.springframework.stereotype.Component;

/** Silver: standard placement with a medium icon. */
// Defines SilverTierStrategy class, implementing SponsorTierStrategy interface
// Spring will detect this as a bean and inject it into the service layer
@Component
public class SilverTierStrategy implements SponsorTierStrategy {

    // Specifies that this strategy applies to the Silver tier
    @Override
    public SponsorTier tier() {
        return SponsorTier.SILVER;
    }

    // Silver sponsors have display priority 3 (standard placement, after Gold but before Bronze)
    @Override
    public int displayPriority() {
        return 3;
    }

    // CSS class for Silver sponsor icons (medium size)
    @Override
    public String iconClass() {
        return "fs-3";
    }

    // Text description of Silver sponsor benefits (used in UI templates)
    @Override
    public String benefits() {
        return "Standard placement, medium icon";
    }
}