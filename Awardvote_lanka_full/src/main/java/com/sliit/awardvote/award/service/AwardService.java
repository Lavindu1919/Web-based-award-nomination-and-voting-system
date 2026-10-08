package com.sliit.awardvote.award.service;

import com.sliit.awardvote.award.event.AwardStatusChangedEvent;
import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.award.model.AwardStatus;
import com.sliit.awardvote.award.dao.AwardDao;
import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service //Spring service layer class
public class AwardService extends AbstractCrudService<AwardProgramme, Long> {
    //Dependencies
    private final AwardDao awardDao;
    private final ApplicationEventPublisher eventPublisher;

    public AwardService(AwardDao awardDao, ApplicationEventPublisher eventPublisher) {
        this.awardDao = awardDao;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected GenericDao<AwardProgramme, Long> getDao() {
        return awardDao;
    }

    /**
     * Checks a programme name: required, and not already used by another
     * programme (case-insensitive). Pass the programme's own id when editing
     * so it doesn't clash with itself. Returns an error message, or empty when fine.
     */
    public Optional<String> validateName(String name, Long ownId) {
        if (name == null || name.isBlank()) {
            return Optional.of("Programme name is required.");
        }
        if (awardDao.existsByNameIgnoreCase(name, ownId)) {
            return Optional.of("An award programme named \"" + name.trim() + "\" already exists. Please choose a different name.");
        }
        return Optional.empty();
    }

    /** Hook override: brand-new programmes always start as DRAFT. */
    @Override
    protected void beforeSave(AwardProgramme programme) {
        if (programme.getId() == null && programme.getStatus() == null) {
            programme.setStatus(AwardStatus.DRAFT);
        }
    }

    /**
     * Observer pattern: when an existing programme's status changes, publish an
     * event. Listeners (for example the content module) react to it, and this
     * service does not need to know who they are.
     */
    @Override
    public AwardProgramme save(AwardProgramme programme) { //Save -Status change check
        AwardStatus oldStatus = programme.getId() == null //Check the programme new or old
                ? null
                : awardDao.findById(programme.getId()).map(AwardProgramme::getStatus).orElse(null);
        AwardProgramme saved = super.save(programme);
        if (oldStatus != null && saved.getStatus() != oldStatus) { //checking if the status has changed
            eventPublisher.publishEvent(new AwardStatusChangedEvent(saved, oldStatus));
        }
        return saved;
    }
}
