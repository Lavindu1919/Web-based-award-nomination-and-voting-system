package com.sliit.awardvote.notification.controller;

import com.sliit.awardvote.notification.service.AwardFeedbackService;
import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.notification.model.Feedback;
import com.sliit.awardvote.notification.model.FeedbackStatus;
import com.sliit.awardvote.notification.service.FeedbackService;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Public feedback / inquiries. Anyone can submit; staff holding
 * {@code HANDLE_FEEDBACK} can create, edit, respond to and delete any item,
 * while a plain user can only edit/delete their own submissions.
 */
@Controller
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final AwardFeedbackService awardFeedbackService;

    public FeedbackController(FeedbackService feedbackService, AwardFeedbackService awardFeedbackService) {
        this.feedbackService = feedbackService;
        this.awardFeedbackService = awardFeedbackService;
    }

    private boolean canHandleFeedback(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.HANDLE_FEEDBACK);
    }

    /** True if the logged-in user is the one who submitted this feedback. */
    private boolean isOwner(Feedback feedback, HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && feedback.getSubmittedBy() != null
                && feedback.getSubmittedBy().getId().equals(u.getId());
    }

    @GetMapping("/feedback")
    public String feedbackList(Model model, HttpSession session) {
        model.addAttribute("feedbackItems", feedbackService.findAll());
        model.addAttribute("newFeedback", new Feedback());
        if (canHandleFeedback(session)) {
            model.addAttribute("awardFeedbackItems", awardFeedbackService.findAll());
        }
        return "feedback/list";
    }

    @PostMapping("/feedback/submit")
    public String submitFeedback(@ModelAttribute Feedback feedback, HttpSession session) {
        feedback.setSubmittedBy(SessionUtil.currentUser(session));
        feedbackService.save(feedback);
        return "redirect:/feedback";
    }

    @GetMapping("/feedback/new")
    public String newFeedbackForm(Model model, HttpSession session) {
        if (!canHandleFeedback(session)) return "redirect:/feedback";
        model.addAttribute("feedback", new Feedback());
        model.addAttribute("statuses", FeedbackStatus.values());
        model.addAttribute("canManageStatus", true);
        return "feedback/form";
    }

    @PostMapping("/feedback/create")
    public String createFeedback(@ModelAttribute Feedback feedback, HttpSession session) {
        if (!canHandleFeedback(session)) return "redirect:/feedback";
        // Staff logging feedback received by phone/in person on someone else's behalf.
        feedbackService.save(feedback);
        return "redirect:/feedback";
    }

    @GetMapping("/feedback/{id}/edit")
    public String editFeedbackForm(@PathVariable Long id, Model model, HttpSession session) {
        Feedback feedback = feedbackService.findById(id).orElseThrow();
        if (!canHandleFeedback(session) && !isOwner(feedback, session)) return "redirect:/feedback";
        model.addAttribute("feedback", feedback);
        model.addAttribute("statuses", FeedbackStatus.values());
        model.addAttribute("canManageStatus", canHandleFeedback(session));
        return "feedback/form";
    }

    @PostMapping("/feedback/{id}/update")
    public String updateFeedback(@PathVariable Long id,
                                  @RequestParam String name,
                                  @RequestParam String email,
                                  @RequestParam(required = false) String subject,
                                  @RequestParam String message,
                                  @RequestParam(required = false) FeedbackStatus status,
                                  HttpSession session) {
        // Load the existing record and only touch the editable fields, so
        // submittedBy/handledBy/response (not present on this form) are preserved
        // instead of being wiped out by a freshly-bound, mostly-empty object.
        Feedback feedback = feedbackService.findById(id).orElseThrow();
        boolean staff = canHandleFeedback(session);
        if (!staff && !isOwner(feedback, session)) return "redirect:/feedback";

        feedback.setName(name);
        feedback.setEmail(email);
        feedback.setSubject(subject);
        feedback.setMessage(message);
        // Only staff may change status; a plain owner editing their own
        // feedback leaves it as-is (status isn't shown on their form).
        if (staff && status != null) {
            feedback.setStatus(status);
        }
        feedbackService.save(feedback);
        return "redirect:/feedback";
    }

    @PostMapping("/feedback/{id}/respond")
    public String respond(@PathVariable Long id, @RequestParam String response, HttpSession session) {
        User current = SessionUtil.currentUser(session);
        if (current.hasPermission(Permission.HANDLE_FEEDBACK)) {
            feedbackService.respond(id, response, current);
        }
        return "redirect:/feedback";
    }

    @GetMapping("/feedback/{id}/delete")
    public String deleteFeedback(@PathVariable Long id, HttpSession session) {
        Feedback feedback = feedbackService.findById(id).orElseThrow();
        if (!canHandleFeedback(session) && !isOwner(feedback, session)) return "redirect:/feedback";
        feedbackService.deleteById(id);
        return "redirect:/feedback";
    }
}
