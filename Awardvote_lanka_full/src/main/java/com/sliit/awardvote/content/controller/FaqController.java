package com.sliit.awardvote.content.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.content.model.Faq;
import com.sliit.awardvote.content.service.FaqService;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/** Save / delete FAQ entries. */
@Controller
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    private boolean canManage(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_CONTENT);
    }

    @PostMapping("/content/faqs/save")
    public String saveFaq(@ModelAttribute Faq faq, HttpSession session) {
        if (!canManage(session)) return "redirect:/dashboard";
        faqService.save(faq);
        return "redirect:/content";
    }

    @GetMapping("/content/faqs/{id}/delete")
    public String deleteFaq(@PathVariable Long id, HttpSession session) {
        if (!canManage(session)) return "redirect:/dashboard";
        faqService.deleteById(id);
        return "redirect:/content";
    }
}
