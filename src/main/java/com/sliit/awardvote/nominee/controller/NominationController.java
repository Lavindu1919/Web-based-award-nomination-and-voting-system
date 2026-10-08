package com.sliit.awardvote.nominee.controller;

import com.sliit.awardvote.award.model.Category;
import com.sliit.awardvote.award.service.CategoryService;
import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.nominee.model.Nomination;
import com.sliit.awardvote.nominee.model.NominationStatus;
import com.sliit.awardvote.nominee.service.NominationService;
import com.sliit.awardvote.nominee.service.ScoreService;
import com.sliit.awardvote.nominee.service.VoteService;
import com.sliit.awardvote.nominee.util.EvidenceLinkValidator;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class NominationController {

    private final NominationService nominationService;
    private final CategoryService categoryService;
    private final VoteService voteService;
    private final ScoreService scoreService;

    public NominationController(NominationService nominationService, CategoryService categoryService,
                                 VoteService voteService, ScoreService scoreService) {
        this.nominationService = nominationService;
        this.categoryService = categoryService;
        this.voteService = voteService;
        this.scoreService = scoreService;
    }

    @GetMapping("/nominations")
    public String list(Model model, HttpSession session) {
        User current = SessionUtil.currentUser(session);

        // Review queue vs. own submission history — independent of the sections below,
        // so a role holding several permissions (e.g. a custom role) gets all of them.
        if (current.hasPermission(Permission.REVIEW_NOMINATIONS)) {
            model.addAttribute("nominations", nominationService.findAll());
        } else {
            model.addAttribute("nominations", nominationService.findByUser(current.getId()));
        }

        // Voting section: every category with its approved, votable nominees front and centre.
        if (current.hasPermission(Permission.VOTE)) {
            List<Category> categories = categoryService.findAll();
            model.addAttribute("votingCategories", categories);

            Map<Long, Long> myVotes = new HashMap<>(); // categoryId -> nominationId this user already voted for
            Map<Long, List<Nomination>> approvedByCategory = new HashMap<>(); // categoryId -> its approved nominees
            for (Category category : categories) {
                voteService.findVoteInCategory(category.getId(), current.getId())
                        .ifPresent(v -> myVotes.put(category.getId(), v.getNomination().getId()));
                approvedByCategory.put(category.getId(), nominationService.findApprovedByCategory(category.getId()));
            }
            model.addAttribute("myVotes", myVotes);
            model.addAttribute("approvedByCategory", approvedByCategory);
        }

        // Judging section: every judging-enabled category with its approved nominees, one pick per category.
        if (current.hasPermission(Permission.JUDGE_NOMINATIONS)) {
            List<Category> categories = categoryService.findAll();
            model.addAttribute("judgingCategories", categories);

            Map<Long, Long> myJudgments = new HashMap<>(); // categoryId -> nominationId this judge already picked
            Map<Long, List<Nomination>> approvedByCategoryForJudging = new HashMap<>();
            for (Category category : categories) {
                scoreService.findScoreInCategory(category.getId(), current.getId())
                        .ifPresent(s -> myJudgments.put(category.getId(), s.getNomination().getId()));
                approvedByCategoryForJudging.put(category.getId(), nominationService.findApprovedByCategory(category.getId()));
            }
            model.addAttribute("myJudgments", myJudgments);
            model.addAttribute("approvedByCategoryForJudging", approvedByCategoryForJudging);
        }

        return "nominations/list";
    }

    @GetMapping("/nominations/new")
    public String newForm(Model model) {
        Nomination nomination = new Nomination();
        model.addAttribute("nomination", nomination);
        model.addAttribute("categories", categoryService.findAll());
        return "nominations/form";
    }

    @PostMapping("/nominations/save")
    public String save(@ModelAttribute Nomination nomination,
                        @RequestParam Long categoryId,
                        HttpSession session,
                        Model model) {
        Category category = categoryService.findById(categoryId).orElseThrow();
        nomination.setCategory(category);
        Optional<String> evidenceError = EvidenceLinkValidator.validate(nomination.getEvidenceUrl());
        if (evidenceError.isPresent()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("evidenceError", evidenceError.get());
            return "nominations/form";
        }
        nomination.setEvidenceUrl(EvidenceLinkValidator.normalize(nomination.getEvidenceUrl()));
        nomination.setSubmittedBy(SessionUtil.currentUser(session));
        nomination.setStatus(NominationStatus.SUBMITTED);
        nominationService.save(nomination);
        return "redirect:/nominations";
    }

    @GetMapping("/nominations/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        User current = SessionUtil.currentUser(session);
        if (!current.hasPermission(Permission.MANAGE_NOMINEES)) {
            return "redirect:/nominations/" + id;
        }
        Nomination nomination = nominationService.findById(id).orElseThrow();
        model.addAttribute("nomination", nomination);
        model.addAttribute("categories", categoryService.findAll());
        return "nominations/form";
    }

    @PostMapping("/nominations/{id}/update")
    public String update(@PathVariable Long id,
                          @ModelAttribute Nomination nomination,
                          @RequestParam Long categoryId,
                          HttpSession session,
                          Model model) {
        User current = SessionUtil.currentUser(session);
        if (!current.hasPermission(Permission.MANAGE_NOMINEES)) {
            return "redirect:/nominations/" + id;
        }
        Nomination existing = nominationService.findById(id).orElseThrow();
        Category category = categoryService.findById(categoryId).orElseThrow();
        Optional<String> evidenceError = EvidenceLinkValidator.validate(nomination.getEvidenceUrl());
        if (evidenceError.isPresent()) {
            // Re-show the edit form with what was typed (display only - nothing is saved).
            nomination.setId(id);
            nomination.setCategory(category);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("evidenceError", evidenceError.get());
            return "nominations/form";
        }
        existing.setCategory(category);
        existing.setNomineeName(nomination.getNomineeName());
        existing.setNomineeEmail(nomination.getNomineeEmail());
        existing.setDescription(nomination.getDescription());
        existing.setEvidenceUrl(EvidenceLinkValidator.normalize(nomination.getEvidenceUrl()));
        // Status, reviewComment, submittedBy, votes and scores are left untouched —
        // editing nominee details doesn't reopen or reset the review/voting state.
        nominationService.save(existing);
        return "redirect:/nominations/" + id;
    }

    @PostMapping("/nominations/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        User current = SessionUtil.currentUser(session);
        if (current.hasPermission(Permission.MANAGE_NOMINEES)) {
            nominationService.deleteById(id);
        }
        return "redirect:/nominations";
    }

    @GetMapping("/nominations/{id}")
    public String view(@PathVariable Long id, Model model, HttpSession session) {
        Nomination nomination = nominationService.findById(id).orElseThrow();
        model.addAttribute("nomination", nomination);
        User current = SessionUtil.currentUser(session);
        if (current != null && nomination.getCategory() != null) {
            if (current.hasPermission(Permission.VOTE)) {
                voteService.findVoteInCategory(nomination.getCategory().getId(), current.getId())
                        .ifPresent(v -> model.addAttribute("existingVoteNominee", v.getNomination()));
            }
            if (current.hasPermission(Permission.JUDGE_NOMINATIONS)) {
                scoreService.findScoreInCategory(nomination.getCategory().getId(), current.getId())
                        .ifPresent(s -> model.addAttribute("existingJudgmentNominee", s.getNomination()));
            }
        }
        return "nominations/view";
    }

    @PostMapping("/nominations/{id}/review")
    public String review(@PathVariable Long id,
                          @RequestParam NominationStatus decision,
                          @RequestParam(required = false) String comment,
                          HttpSession session) {
        User current = SessionUtil.currentUser(session);
        if (current.hasPermission(Permission.REVIEW_NOMINATIONS)) {
            nominationService.review(id, decision, comment);
        }
        return "redirect:/nominations/" + id;
    }
}
