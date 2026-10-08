//This class is used in the service layer and templates to apply tier‑specific display rules and present sponsor information neatly on the webpage
package com.sliit.awardvote.sponsor.service;

// Import the Sponsor entity (we will wrap it inside this class)
import com.sliit.awardvote.sponsor.model.Sponsor;

/** A sponsor together with the display rules chosen by its tier strategy (used by the page templates). */
// Defines the SponsorDisplay class (not an entity, just a helper for UI)
public class SponsorDisplay {

    private final Sponsor sponsor;
    private final String iconClass;
    private final String benefits;

    // Constructor: when creating a SponsorDisplay, you must provide sponsor, iconClass, and benefits
    public SponsorDisplay(Sponsor sponsor, String iconClass, String benefits) {
        this.sponsor = sponsor;
        this.iconClass = iconClass;
        this.benefits = benefits;
    }

    public Sponsor getSponsor() {
        return sponsor;
    }

    public String getIconClass() {
        return iconClass;
    }

    public String getBenefits() {
        return benefits;
    }
}