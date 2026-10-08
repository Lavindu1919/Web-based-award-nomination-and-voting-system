package com.sliit.awardvote.notification.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.nominee.model.Nomination;
import com.sliit.awardvote.notification.model.Notification;
import com.sliit.awardvote.notification.model.NotificationType;
import com.sliit.awardvote.notification.dao.NotificationDao;
import com.sliit.awardvote.user.model.User;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService extends AbstractCrudService<Notification, Long> {

    private final NotificationDao notificationDao;
    private final EmailNotificationStrategy emailStrategy;
    private final SmsNotificationStrategy smsStrategy;

    public NotificationService(NotificationDao notificationDao,
                                EmailNotificationStrategy emailStrategy,
                                SmsNotificationStrategy smsStrategy) {
        this.notificationDao = notificationDao;
        this.emailStrategy = emailStrategy;
        this.smsStrategy = smsStrategy;
    }

    @Override
    protected GenericDao<Notification, Long> getDao() {
        return notificationDao;
    }

    /** Hook override: dispatch the notification the moment it is saved (Template Method + Strategy working together). */
    @Override
    protected void afterSave(Notification notification) {
        dispatch(notification);
    }

    /** Picks the right strategy at runtime based on the notification's type - polymorphism in action. */
    private void dispatch(Notification notification) {
        NotificationStrategy strategy = notification.getType() == NotificationType.SMS ? smsStrategy : emailStrategy;
        strategy.send(notification);
        notificationDao.save(notification);
    }

    public List<Notification> findForUser(Long userId) {
        return notificationDao.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    /** All notifications across every user, most recent first — used by the MANAGE_NOTIFICATIONS admin view. */
    public List<Notification> findAllOrderedByRecent() {
        return notificationDao.findAllByOrderByCreatedAtDesc();
    }

    public void notify(User recipient, String message, NotificationType type, String relatedEntity) {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setMessage(message);
        n.setType(type);
        n.setRelatedEntity(relatedEntity);
        save(n); // goes through afterSave() -> dispatch(), i.e. this actually sends it
    }

    /**
     * Persists changes to an existing notification WITHOUT re-dispatching it.
     * Used when an admin corrects/edits an already-sent notification's record —
     * editing a typo shouldn't re-send the message to the recipient. Only
     * {@link #notify} (new notifications) goes through the dispatch hook.
     */
    public Notification updateRecord(Notification notification) {
        return notificationDao.save(notification);
    }

    /** Called by NominationService whenever a nomination's review decision changes. */
    public void notifyReviewDecision(Nomination nomination) {
        if (nomination.getSubmittedBy() == null) return;
        String msg = "Your nomination for " + nomination.getNomineeName() + " is now " + nomination.getStatus() + ".";
        notify(nomination.getSubmittedBy(), msg, NotificationType.EMAIL, "Nomination#" + nomination.getId());
    }
}
