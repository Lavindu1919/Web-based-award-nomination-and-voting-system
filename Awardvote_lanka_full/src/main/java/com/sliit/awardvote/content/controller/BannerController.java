package com.sliit.awardvote.content.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.content.model.Banner;
import com.sliit.awardvote.content.service.BannerService;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/** Save / delete homepage banners. */
@Controller
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    private boolean canManage(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_CONTENT);
    }

    @PostMapping("/content/banners/save")
    public String saveBanner(@ModelAttribute Banner banner, HttpSession session) {
        if (!canManage(session)) return "redirect:/dashboard";
        bannerService.save(banner);
        return "redirect:/content";
    }

    @GetMapping("/content/banners/{id}/delete")
    public String deleteBanner(@PathVariable Long id, HttpSession session) {
        if (!canManage(session)) return "redirect:/dashboard";
        bannerService.deleteById(id);
        return "redirect:/content";
    }
}
