package com.sliit.awardvote.common.service;

import com.sliit.awardvote.award.service.AwardService;
import com.sliit.awardvote.common.dto.DashboardStats;
import com.sliit.awardvote.content.service.AnnouncementService;
import com.sliit.awardvote.nominee.service.NominationService;
import com.sliit.awardvote.nominee.service.ScoreService;
import com.sliit.awardvote.nominee.service.VoteService;
import com.sliit.awardvote.notification.service.FeedbackService;
import com.sliit.awardvote.notification.service.NotificationService;
import com.sliit.awardvote.sponsor.service.SponsorService;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.service.UserService;

import org.springframework.stereotype.Service;

/**
 * Gathers the numbers the dashboard shows, pulling one or two cheap COUNT
 * queries from each of the six modules. Kept as its own service rather than
 * bloating HomeController's constructor with seven module services just for
 * one page — a small example of the Single Responsibility principle applied
 * across an otherwise controller-thin app.
 */
@Service
public class DashboardService {

    private final UserService userService;
    private final AwardService awardService;
    private final SponsorService sponsorService;
    private final AnnouncementService announcementService;
    private final NotificationService notificationService;
    private final FeedbackService feedbackService;
    private final NominationService nominationService;
    private final VoteService voteService;
    private final ScoreService scoreService;

    public DashboardService(UserService userService, AwardService awardService, SponsorService sponsorService,
                             AnnouncementService announcementService, NotificationService notificationService,
                             FeedbackService feedbackService, NominationService nominationService,
                             VoteService voteService, ScoreService scoreService) {
        this.userService = userService;
        this.awardService = awardService;
        this.sponsorService = sponsorService;
        this.announcementService = announcementService;
        this.notificationService = notificationService;
        this.feedbackService = feedbackService;
        this.nominationService = nominationService;
        this.voteService = voteService;
        this.scoreService = scoreService;
    }

    public DashboardStats buildStatsFor(User user) {
        return new DashboardStats(
                notificationService.findForUser(user.getId()).size(),
                nominationService.findByUser(user.getId()).size(),
                voteService.countVotesByUser(user.getId()),
                scoreService.countJudgedByUser(user.getId()),
                nominationService.countPendingReview(),
                userService.count(),
                awardService.count(),
                sponsorService.count(),
                announcementService.count(),
                notificationService.count(),
                feedbackService.countOpen()
        );
    }
}
