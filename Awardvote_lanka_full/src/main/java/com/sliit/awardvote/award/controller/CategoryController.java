package com.sliit.awardvote.award.controller;

import com.sliit.awardvote.award.model.Category;
import com.sliit.awardvote.award.service.AwardService;
import com.sliit.awardvote.award.service.CategoryService;
import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/** Handles the Category screens (create / edit / delete) that belong to an award programme. */
@Controller
public class CategoryController {
    // Dependencies
    private final AwardService awardService;
    private final CategoryService categoryService;
    // Constructor Injection
    public CategoryController(AwardService awardService, CategoryService categoryService) {
        this.awardService = awardService;
        this.categoryService = categoryService;
    }
    //currently logged-in user have 'Award Management' permission
    private boolean canManage(HttpSession session) {
        // Retrieves the currently logged-in user.
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_AWARDS);
    }
    // Open the new category form and create
    @GetMapping("/awards/{programmeId}/categories/new")
    public String newCategoryForm(@PathVariable Long programmeId, Model model, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards"; // permission check
        Category category = new Category(); // Create new object
        // specify which Award Programme the category is being created for.
        category.setAwardProgramme(awardService.findById(programmeId).orElseThrow());//not find the program Throw the exception
        model.addAttribute("category", category); // pass the category object View
        return "categories/form"; //Display categories form
    }
    //Edit category
    @GetMapping("/categories/{id}/edit") //get the edit form
    public String editCategoryForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards"; // Check user permission
        //The category is retrieved from the database/service layer.
        model.addAttribute("category", categoryService.findById(id).orElseThrow());
        return "categories/form";
    }
    //Submit the form execute method
    @PostMapping("/categories/save")
    // fields category object bind
    public String saveCategory(@ModelAttribute Category category,
                               @RequestParam Long programmeId,
                               HttpSession session) {
        if (!canManage(session)) return "redirect:/awards";
        //assign the category correct award program
        category.setAwardProgramme(awardService.findById(programmeId).orElseThrow());
        categoryService.save(category);//The actual save operation is delegated to the service layer
        return "redirect:/awards/" + programmeId;
    }
    // Delete category
    @GetMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards"; // permission check
        Category category = categoryService.findById(id).orElseThrow();
        Long programmeId = category.getAwardProgramme().getId(); //save the programme id
        categoryService.deleteById(id);
        return "redirect:/awards/" + programmeId;
    }
}