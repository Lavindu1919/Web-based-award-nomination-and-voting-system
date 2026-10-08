package com.sliit.awardvote.common.service;

import com.sliit.awardvote.common.dao.GenericDao;

import java.util.List;
import java.util.Optional;

/**
 * AbstractCrudService - Template Method design pattern.
 *
 * OOP concept: ABSTRACTION + POLYMORPHISM
 * Defines the fixed skeleton of a save operation (beforeSave -> persist -> afterSave)
 * while letting each of the six module services plug in their own DAO and
 * override the hooks with module-specific behaviour (e.g. NotificationService
 * dispatches an email/SMS in afterSave; NominationService stamps a default status
 * in beforeSave). The calling code never changes - only the plugged-in behaviour does.
 */
public abstract class AbstractCrudService<T, ID> {

    /** Every subclass supplies its own Data Access Object. */
    protected abstract GenericDao<T, ID> getDao();

    /** Hook run before persistence - override to validate / default fields. */
    protected void beforeSave(T entity) {
        // no-op by default
    }

    /** Hook run after persistence - override to trigger side effects (e.g. notifications). */
    protected void afterSave(T entity) {
        // no-op by default
    }

    public T save(T entity) {
        beforeSave(entity);
        T saved = getDao().save(entity);
        afterSave(saved);
        return saved;
    }

    public List<T> findAll() {
        return getDao().findAll();
    }

    public Optional<T> findById(ID id) {
        return getDao().findById(id);
    }

    public void deleteById(ID id) {
        getDao().deleteById(id);
    }

    public long count() {
        return getDao().count();
    }
}
