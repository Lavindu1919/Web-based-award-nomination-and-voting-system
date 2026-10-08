package com.sliit.awardvote.common.dao;

import java.util.List;
import java.util.Optional;

/**
 * GenericDao - the common Data Access Object contract every module DAO extends.
 *
 * Data Access Object pattern: the rest of the application (services,
 * controllers) talks only to these interfaces and never touches the
 * EntityManager or any JPQL directly, so persistence details stay in one
 * layer. Each module adds its own query methods on top of these basics.
 */
public interface GenericDao<T, ID> {

    /** Inserts a new entity (no id yet) or updates an existing one. Returns the managed instance. */
    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    /** Deletes the entity with this id; does nothing if it does not exist. */
    void deleteById(ID id);

    long count();
}
