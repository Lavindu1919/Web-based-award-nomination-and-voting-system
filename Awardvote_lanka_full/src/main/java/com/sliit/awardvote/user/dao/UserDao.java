package com.sliit.awardvote.user.dao;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;

import java.util.List;
import java.util.Optional;

// Data access interface for User database operations.
public interface UserDao extends GenericDao<User, Long> {

    // Find a user by username
    Optional<User> findByUsername(String username);

    // Find a user by email
    Optional<User> findByEmail(String email);

    // Check whether a username already exists
    boolean existsByUsername(String username);

    // Check whether an email already exists
    boolean existsByEmail(String email);

    // Find all users with a specific role
    List<User> findByRole(UserRole role);

    // Find users assigned to a specific custom role
    List<User> findByCustomRoleId(Long roleId);

    // Count the number of users with a specific role
    long countByRole(UserRole role);

    // Permanently delete a user and handle related database records
    void deletePermanently(Long userId);
}