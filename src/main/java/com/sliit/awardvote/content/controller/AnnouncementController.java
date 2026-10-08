package com.sliit.awardvote.content.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.content.model.Announcement;
import com.sliit.awardvote.content.service.AnnouncementService;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/** Save / delete announcements (shown on the /content management page). */
@Controller
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    private boolean canManage(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_CONTENT);
    }

    @PostMapping("/content/announcements/save")
    public String saveAnnouncement(@ModelAttribute Announcement announcement, HttpSession session) {
        if (!canManage(session)) return "redirect:/dashboard";
        announcement.setPublishedBy(SessionUtil.currentUser(session));
        announcementService.save(announcement);
        return "redirect:/content";
    }

    @GetMapping("/content/announcements/{id}/delete")
    public String deleteAnnouncement(@PathVariable Long id, HttpSession session) {
        if (!canManage(session)) return "redirect:/dashboard";
        announcementService.deleteById(id);
        return "redirect:/content";
    }
}
