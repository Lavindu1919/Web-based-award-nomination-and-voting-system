package com.sliit.awardvote.nominee.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.nominee.event.NominationReviewedEvent;
import com.sliit.awardvote.nominee.model.Nomination;
import com.sliit.awardvote.nominee.model.NominationStatus;
import com.sliit.awardvote.nominee.dao.NominationDao;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NominationService extends AbstractCrudService<Nomination, Long> {

    private final NominationDao nominationDao;
    private final ApplicationEventPublisher eventPublisher;

    public NominationService(NominationDao nominationDao,
                              ApplicationEventPublisher eventPublisher) {
        this.nominationDao = nominationDao;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected GenericDao<Nomination, Long> getDao() {
        return nominationDao;
    }

    /** Hook override: every brand-new nomination starts life as SUBMITTED. */
    @Override
    protected void beforeSave(Nomination nomination) {
        if (nomination.getId() == null && nomination.getStatus() == null) {
            nomination.setStatus(NominationStatus.SUBMITTED);
        }
    }

    public List<Nomination> findByCategory(Long categoryId) {
        return nominationDao.findByCategoryId(categoryId);
    }

    /** Approved, votable nominees within a single category — used to build the public voting view. */
    public List<Nomination> findApprovedByCategory(Long categoryId) {
        return nominationDao.findByCategoryId(categoryId).stream()
                .filter(n -> n.getStatus() == NominationStatus.APPROVED)
                .toList();
    }

    public List<Nomination> findByStatus(NominationStatus status) {
        return nominationDao.findByStatus(status);
    }

    public List<Nomination> findByUser(Long userId) {
        return nominationDao.findBySubmittedById(userId);
    }

    public void review(Long nominationId, NominationStatus decision, String comment) {
        Nomination nomination = nominationDao.findById(nominationId).orElseThrow();
        nomination.setStatus(decision);
        nomination.setReviewComment(comment);
        nominationDao.save(nomination);
        // Observer pattern: publish the event; listeners (e.g. notifications) react to it.
        eventPublisher.publishEvent(new NominationReviewedEvent(nomination));
    }

    // ---------- Dashboard aggregation ----------

    /** Nominations awaiting a review decision (SUBMITTED or UNDER_REVIEW) — used on the dashboard for staff/admin. */
    public long countPendingReview() {
        return nominationDao.countByStatus(NominationStatus.SUBMITTED)
                + nominationDao.countByStatus(NominationStatus.UNDER_REVIEW);
    }
}
