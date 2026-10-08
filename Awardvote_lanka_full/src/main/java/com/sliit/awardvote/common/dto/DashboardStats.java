package com.sliit.awardvote.common.dto;

/**
 * Read-only bundle of every number the dashboard might show. Every field is
 * always computed (these are cheap COUNT queries) — which of them actually
 * render is decided in the template via the same {@code hasPermission(...)}
 * checks used everywhere else in the app, not by this class deciding what to
 * include. Keeps DashboardService focused purely on gathering the numbers.
 */
public class DashboardStats {

    private final long myNotifications;
    private final long mySubmissions;
    private final long myVotes;
    private final long myJudgedPicks;
    private final long pendingReviews;
    private final long totalUsers;
    private final long totalProgrammes;
    private final long totalSponsors;
    private final long totalContentItems;
    private final long totalNotificationsSent;
    private final long openFeedback;

    public DashboardStats(long myNotifications, long mySubmissions, long myVotes, long myJudgedPicks,
                           long pendingReviews, long totalUsers, long totalProgrammes, long totalSponsors,
                           long totalContentItems, long totalNotificationsSent, long openFeedback) {
        this.myNotifications = myNotifications;
        this.mySubmissions = mySubmissions;
        this.myVotes = myVotes;
        this.myJudgedPicks = myJudgedPicks;
        this.pendingReviews = pendingReviews;
        this.totalUsers = totalUsers;
        this.totalProgrammes = totalProgrammes;
        this.totalSponsors = totalSponsors;
        this.totalContentItems = totalContentItems;
        this.totalNotificationsSent = totalNotificationsSent;
        this.openFeedback = openFeedback;
    }

    public long getMyNotifications() {
        return myNotifications;
    }

    public long getMySubmissions() {
        return mySubmissions;
    }

    public long getMyVotes() {
        return myVotes;
    }

    public long getMyJudgedPicks() {
        return myJudgedPicks;
    }

    public long getPendingReviews() {
        return pendingReviews;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalProgrammes() {
        return totalProgrammes;
    }

    public long getTotalSponsors() {
        return totalSponsors;
    }

    public long getTotalContentItems() {
        return totalContentItems;
    }

    public long getTotalNotificationsSent() {
        return totalNotificationsSent;
    }

    public long getOpenFeedback() {
        return openFeedback;
    }
}
