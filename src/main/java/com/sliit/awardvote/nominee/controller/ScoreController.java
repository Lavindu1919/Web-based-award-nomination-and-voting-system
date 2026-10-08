package com.sliit.awardvote.nominee.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.nominee.model.Nomination;
import com.sliit.awardvote.nominee.service.NominationService;
import com.sliit.awardvote.nominee.service.ScoreService;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/** Handles a judge's pick (score) for an approved nominee. */
@Controller
public class ScoreController {

    private final NominationService nominationService;
    private final ScoreService scoreService;

    public ScoreController(NominationService nominationService, ScoreService scoreService) {
        this.nominationService = nominationService;
        this.scoreService = scoreService;
    }

    @PostMapping("/nominations/{id}/score")
    public String score(@PathVariable Long id,
                         @RequestParam double value,
                         @RequestParam(required = false) String comments,
                         HttpSession session, Model model) {
        Nomination nomination = nominationService.findById(id).orElseThrow();
        User judge = SessionUtil.currentUser(session);
        boolean success = judge.hasPermission(Permission.JUDGE_NOMINATIONS)
                && scoreService.submitScore(nomination, judge, value, comments);
        if (success) {
            model.addAttribute("scoreMessage", "Your pick for this category has been recorded.");
        } else if (nomination.getCategory() != null
                && scoreService.findScoreInCategory(nomination.getCategory().getId(), judge.getId()).isPresent()) {
            model.addAttribute("scoreMessage", "You've already judged a nominee in this category — only one pick per category is allowed.");
        } else {
            model.addAttribute("scoreMessage", "Could not submit your pick (nomination not approved, or judging isn't enabled for this category).");
        }
        Nomination refreshed = nominationService.findById(id).orElseThrow();
        model.addAttribute("nomination", refreshed);
        if (refreshed.getCategory() != null) {
            scoreService.findScoreInCategory(refreshed.getCategory().getId(), judge.getId())
                    .ifPresent(s -> model.addAttribute("existingJudgmentNominee", s.getNomination()));
        }
        return "nominations/view";
    }
}
