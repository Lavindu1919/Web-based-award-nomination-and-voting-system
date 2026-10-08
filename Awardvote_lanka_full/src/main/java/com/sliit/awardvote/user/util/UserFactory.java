package com.sliit.awardvote.user.util;

import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;

import org.springframework.stereotype.Component;


 //UserFactory - Factory Method design pattern.

 //Creates User objects in one place so the account rules are not repeated
 //across the code: self-registered users are always PUBLIC_USER and start
 //inactive (until OTP verification), while system-created users start active.

@Component
public class UserFactory {

    // Create a self-registered user with the default PUBLIC_USER role
    public User createPublicUser(String fullName, String email, String username, String rawPassword) {
        User user = new User(fullName, email, username, rawPassword, UserRole.PUBLIC_USER);

        // Keep the account inactive until OTP verification is completed
        user.setActive(false);

        return user;
    }

    // Create a user account with the specified role and activate it immediately
    public User createUser(UserRole role, String fullName, String email, String username, String rawPassword) {
        User user = new User(fullName, email, username, rawPassword, role);

        // System-created accounts are active by default
        user.setActive(true);

        return user;
    }
}