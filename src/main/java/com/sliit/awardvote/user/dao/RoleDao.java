package com.sliit.awardvote.user.dao;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.user.model.Role;

import java.util.Optional;

/** Data access contract for {@link Role}. */
public interface RoleDao extends GenericDao<Role, Long> {

    // Find a role using its name
    Optional<Role> findByName(String name);

    // Check whether a role with the given name already exists
    boolean existsByName(String name);
}