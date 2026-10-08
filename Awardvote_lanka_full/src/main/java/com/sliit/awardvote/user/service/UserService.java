package com.sliit.awardvote.user.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.common.util.PasswordUtil;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;
import com.sliit.awardvote.user.util.EmailValidator;
import com.sliit.awardvote.user.dao.UserDao;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService extends AbstractCrudService<User, Long> {

    // DAO used to perform database operations for users
    private final UserDao userDao;

    // Constructor injection for UserDao
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Provides the UserDao to the parent CRUD service
    @Override
    protected GenericDao<User, Long> getDao() {
        return userDao;
    }

    // Hashes the password before saving the user
    @Override
    protected void beforeSave(User user) {

        // Avoid hashing an already hashed password
        if (user.getPassword() != null && user.getPassword().length() != 64) {

            // Convert the raw password into a SHA-256 hash
            user.setPassword(PasswordUtil.hash(user.getPassword()));
        }
    }

    // Finds a user by username
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    // Finds a user using either username or email
    public Optional<User> findByUsernameOrEmail(String identifier) {
        return userDao.findByUsername(identifier)
                .or(() -> userDao.findByEmail(identifier));
    }

    // Checks whether a username is already registered
    public boolean usernameTaken(String username) {
        return userDao.existsByUsername(username);
    }

    // Checks whether an email is already registered
    public boolean emailTaken(String email) {
        return userDao.existsByEmail(email);
    }

    // Validates the email format and checks whether it is already used
    public Optional<String> validateEmail(String email, Long ownId) {

        // Check whether the email has a valid format
        Optional<String> formatError = EmailValidator.validate(email);

        if (formatError.isPresent()) {
            return formatError;
        }

        // Normalize the email before checking the database
        String normalized = EmailValidator.normalize(email);

        // Check whether another user already uses this email
        boolean usedByOther = userDao.findByEmail(normalized)
                .map(existing -> ownId == null || !existing.getId().equals(ownId))
                .orElse(false);

        // Return an error if the email belongs to another account
        return usedByOther
                ? Optional.of("That email is already registered.")
                : Optional.empty();
    }

    // Finds all users with a specific role
    public List<User> findByRole(UserRole role) {
        return userDao.findByRole(role);
    }

    // Authenticates a user using username and password
    public Optional<User> authenticate(String username, String rawPassword) {

        // Find the user, make sure the account is active,
        // and verify the provided password
        return userDao.findByUsername(username)
                .filter(User::isActive)
                .filter(u -> PasswordUtil.matches(rawPassword, u.getPassword()));
    }

    // Switches the user's active status
    public void toggleActive(Long userId) {

        // Find the user and change active to inactive or inactive to active
        userDao.findById(userId).ifPresent(u -> {
            u.setActive(!u.isActive());
            userDao.save(u);
        });
    }

    // Deactivates a user account
    public void deactivate(Long userId) {

        // Find the user and set the account to inactive
        userDao.findById(userId).ifPresent(u -> {
            u.setActive(false);
            userDao.save(u);
        });
    }

    // Checks whether a user is allowed to delete another account
    public Optional<String> validateDeletion(User actor, User target) {

        // Apply special deletion rules for System Administrator accounts
        if (target.getRole() == UserRole.SYSTEM_ADMIN) {

            // Only another System Administrator can delete an administrator
            if (actor.getRole() != UserRole.SYSTEM_ADMIN) {
                return Optional.of(
                        "Only a system administrator can delete an administrator account."
                );
            }

            // Prevent deleting the last System Administrator
            if (userDao.countByRole(UserRole.SYSTEM_ADMIN) <= 1) {
                return Optional.of(
                        "This is the last system administrator account, so it can't be deleted."
                );
            }
        }

        // Empty means the deletion is allowed
        return Optional.empty();
    }

    // Permanently deletes the user account from the database
    public void deletePermanently(Long userId) {

        // Delete the user and related dependent records
        userDao.deletePermanently(userId);
    }
}