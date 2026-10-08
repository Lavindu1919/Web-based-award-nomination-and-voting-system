package com.sliit.awardvote.user.model;


public enum Permission {

    // Permission to manage user accounts and assign roles
    MANAGE_USERS("Create, edit, deactivate and assign roles to user accounts"),

    // Permission to create and manage custom roles
    MANAGE_ROLES("Create and edit custom roles and their permissions"),

    // Permission to manage award programmes and categories
    MANAGE_AWARDS("Create and edit award programmes and categories"),

    // Permission to manage nominees within award categories
    MANAGE_NOMINEES("Add, edit and delete nominee entries within award categories"),

    // Permission to review and decide on submitted nominations
    REVIEW_NOMINATIONS("Approve or reject submitted nominations"),

    // Permission to submit evaluation scores for nominees
    JUDGE_NOMINATIONS("Submit judge evaluation scores for approved nominees"),

    // Permission to manage sponsor and partner information
    MANAGE_SPONSORS("Create and edit sponsor and partner profiles"),

    // Permission to manage announcements, banners and FAQs
    MANAGE_CONTENT("Publish announcements, banners and FAQs"),

    // Permission to manage notifications for all users
    MANAGE_NOTIFICATIONS("Create, edit, delete and view every notification sent to any user"),

    // Permission to manage and respond to public feedback
    HANDLE_FEEDBACK("Create, edit, delete and respond to public feedback and inquiries"),

    // Permission to allow users to cast votes
    VOTE("Cast a public vote for an approved nominee");

    private final String description;

    // Store a readable description for each permission
    Permission(String description) {
        this.description = description;
    }

    // Return the description of the permission
    public String getDescription() {
        return description;
    }
}