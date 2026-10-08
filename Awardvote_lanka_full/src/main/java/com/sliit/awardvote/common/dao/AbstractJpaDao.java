package com.sliit.awardvote.common.dao;

import com.sliit.awardvote.common.model.BaseEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


// AbstractJpaDao - reusable JPA implementation of {@link GenericDao}.

// OOP concept: ABSTRACTION + INHERITANCE
// Provides common CRUD operations for all entities.
// Concrete DAOs inherit these methods and add their own queries when needed.

public abstract class AbstractJpaDao<T extends BaseEntity> implements GenericDao<T, Long> {

    // JPA component used to perform database operations
    @PersistenceContext
    protected EntityManager em;

    // Stores the entity class used by this DAO
    private final Class<T> entityClass;

    // Receive the entity type when creating a concrete DAO
    protected AbstractJpaDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    @Transactional
    public T save(T entity) {

        // Insert a new entity when it does not have an ID
        if (entity.getId() == null) {
            em.persist(entity);
            return entity;
        }

        // Update an existing entity
        return em.merge(entity);
    }

    @Override
    public Optional<T> findById(Long id) {

        // Find an entity by its primary key
        return Optional.ofNullable(em.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {

        // Retrieve all records for the current entity type
        return em.createQuery(
                "select e from " + entityClass.getSimpleName() + " e",
                entityClass
        ).getResultList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        // Find the entity and remove it if it exists
        findById(id).ifPresent(em::remove);
    }

    @Override
    public long count() {

        // Count the total number of records for the current entity
        return em.createQuery(
                "select count(e) from " + entityClass.getSimpleName() + " e",
                Long.class
        ).getSingleResult();
    }
}