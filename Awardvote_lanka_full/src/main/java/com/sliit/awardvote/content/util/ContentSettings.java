package com.sliit.awardvote.content.util;

/**
 * ContentSettings - Singleton design pattern.
 *
 * Exactly one shared instance holds the site-wide content settings, so every
 * part of the application reads the same values. The constructor is private,
 * and the only way to get the object is getInstance().
 */
public final class ContentSettings {

    /** The single instance, created once when the class is loaded (thread-safe). */
    private static final ContentSettings INSTANCE = new ContentSettings();

    /** How many announcements the public home page shows. */
    private volatile int maxHomeAnnouncements = 5;

    private ContentSettings() {
        // private: nobody else can create another instance
    }

    public static ContentSettings getInstance() {
        return INSTANCE;
    }

    public int getMaxHomeAnnouncements() {
        return maxHomeAnnouncements;
    }

    public void setMaxHomeAnnouncements(int maxHomeAnnouncements) {
        if (maxHomeAnnouncements < 1) {
            throw new IllegalArgumentException("Must show at least 1 announcement");
        }
        this.maxHomeAnnouncements = maxHomeAnnouncements;
    }
}
