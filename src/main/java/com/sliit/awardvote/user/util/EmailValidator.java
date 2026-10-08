package com.sliit.awardvote.user.util;

import java.util.Optional;
import java.util.regex.Pattern;


public final class EmailValidator {

    public static final int MAX_LENGTH = 254;
    private static final int MAX_LOCAL_LENGTH = 64;

    private static final String LOCAL_CHAR = "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]";
    private static final String DOMAIN_LABEL = "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?";

    private static final Pattern LOCAL_PART =
            Pattern.compile(LOCAL_CHAR + "+(?:\\." + LOCAL_CHAR + "+)*");
    private static final Pattern DOMAIN =
            Pattern.compile("(?:" + DOMAIN_LABEL + "\\.)+[A-Za-z]{2,63}");

    private EmailValidator() {
    }

    /** Trims and lower-cases an email; returns null for null input. */
    public static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    /** Returns an error message if the email is not acceptable, or empty when it is valid. */
    public static Optional<String> validate(String email) {
        if (email == null || email.isBlank()) {
            return Optional.of("Email address is required.");
        }
        String value = email.trim();
        if (value.length() > MAX_LENGTH) {
            return Optional.of("Email address is too long (maximum " + MAX_LENGTH + " characters).");
        }
        int at = value.indexOf('@');
        if (at < 0 || at != value.lastIndexOf('@')) {
            return Optional.of("Email address must contain exactly one '@' (e.g. name@example.com).");
        }
        String local = value.substring(0, at);
        String domain = value.substring(at + 1);
        if (local.isEmpty() || local.length() > MAX_LOCAL_LENGTH || !LOCAL_PART.matcher(local).matches()) {
            return Optional.of("The part of the email before '@' is not valid.");
        }
        if (!DOMAIN.matcher(domain).matches()) {
            return Optional.of("The domain part of the email is not valid (e.g. example.com).");
        }
        return Optional.empty();
    }

    public static boolean isValid(String email) {
        return validate(email).isEmpty();
    }
}
