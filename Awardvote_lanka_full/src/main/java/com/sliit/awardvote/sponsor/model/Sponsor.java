//the blueprint for Sponsor records in the database
package com.sliit.awardvote.sponsor.model;

import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.common.model.BaseEntity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * MODULE 5: SPONSOR & PARTNER MANAGEMENT
 * Presented by: Chandrasooriya J.T. (IT25100146)

 * Relationships: MANY-TO-MANY with AwardProgramme (inverse side).
 * Deliberately has NO access to Nomination/Vote/Score data - sponsors never
 * see confidential voting results, per the proposal's Restrictions (8.3).
 */
// Marks this class as a JPA entity (maps to a database table)
@Entity
// Specifies the table name in the database: "sponsors"
@Table(name = "sponsors")
// Sponsor class extends BaseEntity (likely provides ID, timestamps, etc.)
public class Sponsor extends BaseEntity {

    // Sponsor name (required field, cannot be null)
    @Column(nullable = false)
    private String name;

    private String contactPerson;
    private String email;
    private String phone;
    private String logoUrl;

    // Sponsor tier (Bronze, Silver, Gold, etc.) stored as text in DB
    // Default value is BRONZE
    @Enumerated(EnumType.STRING)
    private SponsorTier tier = SponsorTier.BRONZE;

    // Description of sponsor (up to 1000 characters)
    @Column(length = 1000)
    private String description;

    // Many-to-many relationship with AwardProgramme
    // "mappedBy" means AwardProgramme owns the relationship
    // A sponsor can support multiple programs, and a program can have multiple sponsors
    @ManyToMany(mappedBy = "sponsors")
    private Set<AwardProgramme> programmes = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public SponsorTier getTier() {
        return tier;
    }

    public void setTier(SponsorTier tier) {
        this.tier = tier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<AwardProgramme> getProgrammes() {
        return programmes;
    }

    public void setProgrammes(Set<AwardProgramme> programmes) {
        this.programmes = programmes;
    }
}