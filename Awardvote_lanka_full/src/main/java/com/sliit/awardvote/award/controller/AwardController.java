// Handle the http reqst and send the service layer
package com.sliit.awardvote.award.controller;

//Model class
import com.sliit.awardvote.notification.model.AwardFeedback;
import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.award.model.AwardStatus;
import com.sliit.awardvote.award.model.Category;
import com.sliit.awardvote.notification.service.AwardFeedbackService;
import com.sliit.awardvote.award.service.AwardService;
import com.sliit.awardvote.award.service.CategoryService;
import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.nominee.model.Nomination;
import com.sliit.awardvote.nominee.service.NominationService;
import com.sliit.awardvote.nominee.service.ScoreService;
import com.sliit.awardvote.nominee.service.VoteService;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AwardController {

    /** Awards can only be created for this year or later. */
    private static final int MIN_YEAR = 2026;

    //"The controller uses different service classes to separate the controller layer
    // from the business logic and database operations."
    private final AwardService awardService;
    private final CategoryService categoryService;
    private final NominationService nominationService;
    private final VoteService voteService;
    private final ScoreService scoreService;
    private final AwardFeedbackService awardFeedbackService;

    //Constructor Injection (Dependency Injection)
    public AwardController(AwardService awardService, CategoryService categoryService, NominationService nominationService,
                           VoteService voteService, ScoreService scoreService, AwardFeedbackService awardFeedbackService) {
        this.awardService = awardService;
        this.categoryService = categoryService;
        this.nominationService = nominationService;
        this.voteService = voteService;
        this.scoreService = scoreService;
        this.awardFeedbackService = awardFeedbackService;
    }
    //Authorization check
    private boolean canManage(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_AWARDS);
    }

    // ---------- Award programmes ----------

    @GetMapping("/awards") // Handle the get reqst
    public String list(Model model) {
        //Get the all Award programmes and this data send the View
        model.addAttribute("programmes", awardService.findAll());
        return "awards/list";// Display the award view
    }
    //Create New award form
    @GetMapping("/awards/new")
    public String newForm(Model model, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards"; // Check the permission user,if the user doesn't have permission redirect the awards
        AwardProgramme programme = new AwardProgramme();
        programme.setYear(MIN_YEAR);
        model.addAttribute("programme", programme);
        model.addAttribute("statuses", AwardStatus.values());
        return "awards/form";//open the award page
    }
    //Edit award program
    @GetMapping("/awards/{id}/edit") // Id-dynamic valueid
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards";
        model.addAttribute("programme", awardService.findById(id).orElseThrow());//Find the data base award
        model.addAttribute("statuses", AwardStatus.values());
        return "awards/form";
    }
    //Submit the form before the code runs
    @PostMapping("/awards/save")
    //@ bind the award programe  received the form data
    public String save(@ModelAttribute AwardProgramme programme, HttpSession session, Model model) {
        if (!canManage(session)) return "redirect:/awards";
        if (programme.getYear() < MIN_YEAR) { // Check the year 2026 min
            model.addAttribute("statuses", AwardStatus.values());
            model.addAttribute("yearError", "Year must be " + MIN_YEAR + " or later.");
            return "awards/form";
        }
        awardService.save(programme); //save the form service true
        return "redirect:/awards";
    }

    // Delete Award
    @GetMapping("/awards/{id}/delete") // get the award ID
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards";
        awardService.deleteById(id);
        return "redirect:/awards"; //Send the award list
    }
    // Display the award details page
    @GetMapping("/awards/{id}")
    public String view(@PathVariable Long id, Model model, HttpSession session) {
        AwardProgramme programme = awardService.findById(id).orElseThrow();
        List<Category> categories = categoryService.findByProgramme(id);//get the award programmes category
        model.addAttribute("programme", programme);
        model.addAttribute("categories", categories);

        //Store the Approved nomination list
        Map<Long, List<Nomination>> approvedByCategory = new HashMap<>();
        //Get the approved nomination each category
        for (Category category : categories) {
            approvedByCategory.put(category.getId(), nominationService.findApprovedByCategory(category.getId()));
        }
        model.addAttribute("approvedByCategory", approvedByCategory);//View

        // Identifies the currently logged-in user.
        User current = SessionUtil.currentUser(session);
        //It checks whether the user has VOTE permission.
        if (current != null && current.hasPermission(Permission.VOTE)) {
            Map<Long, Long> myVotes = new HashMap<>(); // Store user vote
            for (Category category : categories) {
                voteService.findVoteInCategory(category.getId(), current.getId())
                        .ifPresent(v -> myVotes.put(category.getId(), v.getNomination().getId())); //check the current user vote
            }
            model.addAttribute("myVotes", myVotes);
        }
        //Judges judgment
        if (current != null && current.hasPermission(Permission.JUDGE_NOMINATIONS)) {
            Map<Long, Long> myJudgments = new HashMap<>();
            for (Category category : categories) { // The judge checks whether a score has already been assigned to the nomination.
                scoreService.findScoreInCategory(category.getId(), current.getId())
                        .ifPresent(s -> myJudgments.put(category.getId(), s.getNomination().getId()));
            }
            model.addAttribute("myJudgments", myJudgments); // Send the data
        }

        List<AwardFeedback> awardFeedback = awardFeedbackService.findByProgramme(id);
        model.addAttribute("awardFeedback", awardFeedback); // Send the view
        model.addAttribute("currentUserId", current != null ? current.getId() : null);
        //The current user's ID is passed to the view.

        return "awards/view"; //Display
    }
}