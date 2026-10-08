package com.sliit.awardvote.notification.controller;

import com.sliit.awardvote.notification.model.AwardFeedback;
import com.sliit.awardvote.notification.service.AwardFeedbackService;
import com.sliit.awardvote.award.service.AwardService;
import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Public feedback/comments left on a specific award programme's page.
 * Any logged-in user can post; everyone (including guests) can read the
 * thread on the award page; the original author may edit or delete their
 * own entry, and staff holding {@code HANDLE_FEEDBACK} can moderate
 * (delete) any entry, from either the award page or the Feedback &amp;
 * Inquiries admin screen.
 */
@Controller
public class AwardFeedbackController {

    private final AwardFeedbackService awardFeedbackService;
    private final AwardService awardService;

    public AwardFeedbackController(AwardFeedbackService awardFeedbackService, AwardService awardService) {
        this.awardFeedbackService = awardFeedbackService;
        this.awardService = awardService;
    }

    private boolean isOwner(AwardFeedback feedback, HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && feedback.getUser() != null && feedback.getUser().getId().equals(u.getId());
    }

    private boolean canModerate(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.HANDLE_FEEDBACK);
    }

    @PostMapping("/awards/{programmeId}/feedback/submit")
    public String submit(@PathVariable Long programmeId, @RequestParam String message, HttpSession session) {
        User current = SessionUtil.currentUser(session);
        if (current == null || message == null || message.isBlank()) {
            return "redirect:/awards/" + programmeId;
        }
        AwardFeedback feedback = new AwardFeedback();
        feedback.setMessage(message.trim());
        feedback.setAwardProgramme(awardService.findById(programmeId).orElseThrow());
        feedback.setUser(current);
        awardFeedbackService.save(feedback);
        return "redirect:/awards/" + programmeId + "#award-feedback";
    }

    @PostMapping("/awards/feedback/{id}/update")
    public String update(@PathVariable Long id, @RequestParam String message, HttpSession session) {
        AwardFeedback feedback = awardFeedbackService.findById(id).orElseThrow();
        Long programmeId = feedback.getAwardProgramme().getId();
        if (!isOwner(feedback, session) || message == null || message.isBlank()) {
            return "redirect:/awards/" + programmeId + "#award-feedback";
        }
        feedback.setMessage(message.trim());
        awardFeedbackService.save(feedback);
        return "redirect:/awards/" + programmeId + "#award-feedback";
    }

    @GetMapping("/awards/feedback/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, @RequestParam(required = false) String from) {
        AwardFeedback feedback = awardFeedbackService.findById(id).orElseThrow();
        Long programmeId = feedback.getAwardProgramme().getId();
        if (!isOwner(feedback, session) && !canModerate(session)) {
            return "redirect:/awards/" + programmeId + "#award-feedback";
        }
        awardFeedbackService.deleteById(id);
        return "admin".equals(from) ? "redirect:/feedback" : "redirect:/awards/" + programmeId + "#award-feedback";
    }
}
