package com.sliit.awardvote.content.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.content.model.Announcement;
import com.sliit.awardvote.content.model.Banner;
import com.sliit.awardvote.content.model.Faq;
import com.sliit.awardvote.content.service.AnnouncementService;
import com.sliit.awardvote.content.service.BannerService;
import com.sliit.awardvote.content.service.FaqService;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContentController {

    private final AnnouncementService announcementService;
    private final BannerService bannerService;
    private final FaqService faqService;

    public ContentController(AnnouncementService announcementService, BannerService bannerService, FaqService faqService) {
        this.announcementService = announcementService;
        this.bannerService = bannerService;
        this.faqService = faqService;
    }

    private boolean canManage(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_CONTENT);
    }

    @GetMapping("/content")
    public String manage(Model model, HttpSession session) {
        if (!canManage(session)) return "redirect:/dashboard";
        model.addAttribute("announcements", announcementService.findAll());
        model.addAttribute("banners", bannerService.findAll());
        model.addAttribute("faqs", faqService.findAll());
        model.addAttribute("newAnnouncement", new Announcement());
        model.addAttribute("newBanner", new Banner());
        model.addAttribute("newFaq", new Faq());
        return "content/manage";
    }
}
