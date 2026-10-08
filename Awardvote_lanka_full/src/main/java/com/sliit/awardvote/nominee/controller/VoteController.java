package com.sliit.awardvote.nominee.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.nominee.model.Nomination;
import com.sliit.awardvote.nominee.service.NominationService;
import com.sliit.awardvote.nominee.service.VoteService;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/** Handles casting a public vote for an approved nominee. */
@Controller
public class VoteController {

    private final NominationService nominationService;
    private final VoteService voteService;

    public VoteController(NominationService nominationService, VoteService voteService) {
        this.nominationService = nominationService;
        this.voteService = voteService;
    }

    @PostMapping("/nominations/{id}/vote")
    public String vote(@PathVariable Long id, HttpSession session, Model model) {
        Nomination nomination = nominationService.findById(id).orElseThrow();
        User voter = SessionUtil.currentUser(session);
        boolean success = voter.hasPermission(Permission.VOTE) && voteService.castVote(nomination, voter);
        if (success) {
            model.addAttribute("voteMessage", "Thanks for your vote!");
        } else if (nomination.getCategory() != null
                && voteService.findVoteInCategory(nomination.getCategory().getId(), voter.getId()).isPresent()) {
            model.addAttribute("voteMessage", "You've already voted for a nominee in this category — only one vote per category is allowed.");
        } else {
            model.addAttribute("voteMessage", "You could not vote (nomination not approved, or voting is closed).");
        }
        Nomination refreshed = nominationService.findById(id).orElseThrow();
        model.addAttribute("nomination", refreshed);
        if (refreshed.getCategory() != null) {
            voteService.findVoteInCategory(refreshed.getCategory().getId(), voter.getId())
                    .ifPresent(v -> model.addAttribute("existingVoteNominee", v.getNomination()));
        }
        return "nominations/view";
    }
}
