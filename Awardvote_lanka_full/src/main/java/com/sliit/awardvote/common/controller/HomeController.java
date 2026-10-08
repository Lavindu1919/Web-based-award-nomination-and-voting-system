package com.sliit.awardvote.common.controller;

import com.sliit.awardvote.award.service.AwardService;
import com.sliit.awardvote.content.service.AnnouncementService;
import com.sliit.awardvote.content.service.BannerService;
import com.sliit.awardvote.content.service.FaqService;
import com.sliit.awardvote.sponsor.service.SponsorService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** The public landing page. */
@Controller
public class HomeController {

    private final AwardService awardService;
    private final SponsorService sponsorService;
    private final AnnouncementService announcementService;
    private final BannerService bannerService;
    private final FaqService faqService;

    public HomeController(AwardService awardService, SponsorService sponsorService,
                           AnnouncementService announcementService, BannerService bannerService,
                           FaqService faqService) {
        this.awardService = awardService;
        this.sponsorService = sponsorService;
        this.announcementService = announcementService;
        this.bannerService = bannerService;
        this.faqService = faqService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("programmes", awardService.findAll());
        model.addAttribute("sponsors", sponsorService.findAllForDisplay());
        model.addAttribute("announcements", announcementService.findLatestActive());
        model.addAttribute("banners", bannerService.findActive());
        model.addAttribute("faqs", faqService.findAll());
        return "index";
    }
}
