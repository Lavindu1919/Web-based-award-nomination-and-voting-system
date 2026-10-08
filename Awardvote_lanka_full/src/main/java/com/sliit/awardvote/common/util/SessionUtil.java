package com.sliit.awardvote.common.util;

import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;

/**
 * SessionUtil - centralises how the logged-in user is stored in/read from
 * the HTTP session, used by the login interceptor and every controller that
 * needs to know "who is currently logged in".
 */
public final class SessionUtil {

    public static final String SESSION_KEY = "loggedInUser";

    private SessionUtil() {
    }

    public static void login(HttpSession session, User user) {
        session.setAttribute(SESSION_KEY, user);
    }

    public static void logout(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
        session.invalidate();
    }

    public static User currentUser(HttpSession session) {
        Object obj = session.getAttribute(SESSION_KEY);
        return obj instanceof User ? (User) obj : null;
    }

    public static boolean isLoggedIn(HttpSession session) {
        return currentUser(session) != null;
    }
}
