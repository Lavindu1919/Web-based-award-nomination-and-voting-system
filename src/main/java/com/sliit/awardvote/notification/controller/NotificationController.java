package com.sliit.awardvote.notification.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.notification.model.Notification;
import com.sliit.awardvote.notification.model.NotificationStatus;
import com.sliit.awardvote.notification.model.NotificationType;
import com.sliit.awardvote.notification.service.NotificationService;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * MODULE 4: NOTIFICATION & FEEDBACK MANAGEMENT
 * Presented by: Aman A.A.M.H. (IT25100135)
 *
 * Handles notifications (system + admin-composed messages to users). Full CRUD
 * is gated behind {@code MANAGE_NOTIFICATIONS} — anyone holding that permission
 * (Marketing Officer by default, or a custom role granted it) can create, edit,
 * delete and view every message sent to every user, not just their own.
 * Feedback lives in {@link FeedbackController}.
 */
@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    private boolean canManageNotifications(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_NOTIFICATIONS);
    }

    // ---------- Notifications ----------

    @GetMapping("/notifications")
    public String myNotifications(Model model, HttpSession session) {
        User current = SessionUtil.currentUser(session);
        model.addAttribute("notifications", notificationService.findForUser(current.getId()));

        // Anyone with MANAGE_NOTIFICATIONS additionally sees every notification sent to every user.
        if (current.hasPermission(Permission.MANAGE_NOTIFICATIONS)) {
            model.addAttribute("allNotifications", notificationService.findAllOrderedByRecent());
        }
        return "notifications/list";
    }

    @GetMapping("/notifications/new")
    public String newNotificationForm(Model model, HttpSession session) {
        if (!canManageNotifications(session)) return "redirect:/notifications";
        model.addAttribute("notification", new Notification());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("types", NotificationType.values());
        return "notifications/form";
    }

    @PostMapping("/notifications/create")
    public String createNotification(@RequestParam Long recipientId,
                                      @RequestParam String message,
                                      @RequestParam NotificationType type,
                                      @RequestParam(required = false) String relatedEntity,
                                      HttpSession session) {
        if (!canManageNotifications(session)) return "redirect:/notifications";
        User recipient = userService.findById(recipientId).orElseThrow();
        notificationService.notify(recipient, message, type, relatedEntity); // dispatches for real
        return "redirect:/notifications";
    }

    @GetMapping("/notifications/{id}/edit")
    public String editNotificationForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!canManageNotifications(session)) return "redirect:/notifications";
        model.addAttribute("notification", notificationService.findById(id).orElseThrow());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("types", NotificationType.values());
        model.addAttribute("statuses", NotificationStatus.values());
        return "notifications/edit-form";
    }

    @PostMapping("/notifications/{id}/update")
    public String updateNotification(@PathVariable Long id,
                                      @RequestParam Long recipientId,
                                      @RequestParam String message,
                                      @RequestParam NotificationType type,
                                      @RequestParam NotificationStatus status,
                                      @RequestParam(required = false) String relatedEntity,
                                      HttpSession session) {
        if (!canManageNotifications(session)) return "redirect:/notifications";
        Notification notification = notificationService.findById(id).orElseThrow();
        notification.setRecipient(userService.findById(recipientId).orElseThrow());
        notification.setMessage(message);
        notification.setType(type);
        notification.setStatus(status);
        notification.setRelatedEntity(relatedEntity);
        notificationService.updateRecord(notification); // does NOT re-send
        return "redirect:/notifications";
    }

    @GetMapping("/notifications/{id}/delete")
    public String deleteNotification(@PathVariable Long id, HttpSession session) {
        if (!canManageNotifications(session)) return "redirect:/notifications";
        notificationService.deleteById(id);
        return "redirect:/notifications";
    }
}
