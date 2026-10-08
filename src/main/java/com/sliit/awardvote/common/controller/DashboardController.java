package com.sliit.awardvote.common.controller;

import com.sliit.awardvote.common.service.DashboardService;
import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.content.service.BannerService;
import com.sliit.awardvote.notification.model.Notification;
import com.sliit.awardvote.notification.service.NotificationService;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalTime;
import java.util.List;

/** The logged-in user's dashboard. */
@Controller
public class DashboardController {

    private final BannerService bannerService;
    private final NotificationService notificationService;
    private final DashboardService dashboardService;

    public DashboardController(BannerService bannerService, NotificationService notificationService,
                                DashboardService dashboardService) {
        this.bannerService = bannerService;
        this.notificationService = notificationService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User current = SessionUtil.currentUser(session);
        model.addAttribute("user", current);
        model.addAttribute("greeting", timeBasedGreeting());
        model.addAttribute("stats", dashboardService.buildStatsFor(current));
        model.addAttribute("banners", bannerService.findActive());

        List<Notification> recent = notificationService.findForUser(current.getId());
        model.addAttribute("recentNotifications", recent.size() > 5 ? recent.subList(0, 5) : recent);

        return "dashboard";
    }

    private String timeBasedGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) return "Good morning";
        if (hour < 17) return "Good afternoon";
        return "Good evening";
    }
}
