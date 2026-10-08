package com.sliit.awardvote.award.model;

import com.sliit.awardvote.common.model.BaseEntity;
import com.sliit.awardvote.sponsor.model.Sponsor;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * MODULE 2: AWARD MANAGEMENT
 * Presented by: Weerasinghe A.S.T.W. (IT25100130)
 *
 * An AwardProgramme is the top-level "season" of the awards
 * (e.g. "Lanka Excellence Awards 2026"), made up of one or more Categories.
 *
 * Relationships:
 *  - ONE-TO-MANY with Category (one programme has many categories)
 *  - MANY-TO-MANY with Sponsor (a programme can have several sponsors,
 *    a sponsor can back several programmes)
 */
//This tells JPA that this Java class is an entity mapped to a database table.
@Entity
@Table(name = "award_programmes")  // Database table name
public class AwardProgramme extends BaseEntity {

    @Column(nullable = false) //The field that stores the program name.
    private String name;

    @Column(length = 2000)
    private String description;

    private int year;

    @Enumerated(EnumType.STRING) //enum-save the string type
    private AwardStatus status = AwardStatus.DRAFT; // deafult status is DRAFT


    @OneToMany(mappedBy = "awardProgramme", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "programme_sponsors",
            joinColumns = @JoinColumn(name = "programme_id"),
            inverseJoinColumns = @JoinColumn(name = "sponsor_id")
    )
    private Set<Sponsor> sponsors = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public AwardStatus getStatus() {
        return status;
    }

    public void setStatus(AwardStatus status) {
        this.status = status;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<Sponsor> getSponsors() {
        return sponsors;
    }

    public void setSponsors(Set<Sponsor> sponsors) {
        this.sponsors = sponsors;
    }
}
