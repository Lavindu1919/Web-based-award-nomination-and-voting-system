//the blueprint for tier‑specific display rules
package com.sliit.awardvote.sponsor.strategy;

import com.sliit.awardvote.sponsor.model.SponsorTier;

/**
 * SponsorTierStrategy - Behavioral-Strategy design pattern.
 *
 * Each sponsor tier (Platinum, Gold, Silver, Bronze) has its own display
 * rules: where it is placed, how big its icon is and what it gets.
 * SponsorService picks the matching strategy at runtime from the sponsor's tier.
 */
// Defines the interface (blueprint) for all sponsor tier strategies
public interface SponsorTierStrategy {

    /** The tier this strategy handles. */
    // Method: returns which tier this strategy applies to (e.g., PLATINUM)
    SponsorTier tier();

    /** Placement order on the page (1 = shown first). */
    // Method: returns the display priority (lower number = higher placement)
    int displayPriority();

    /** Bootstrap font-size class for the sponsor icon (fs-1 is the largest). */
    // Method: returns the CSS class for icon size (e.g., "fs-1", "fs-2")
    String iconClass();

    /** Short text describing the display benefit of the tier. */
    // Method: returns a short description of the tier’s benefits (used in UI)
    String benefits();
}