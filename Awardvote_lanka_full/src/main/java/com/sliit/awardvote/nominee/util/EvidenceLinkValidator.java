package com.sliit.awardvote.nominee.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Optional;


public final class EvidenceLinkValidator {

    public static final int MAX_LENGTH = 255;
    public static final String ACCEPTED_TYPES_MESSAGE =
            "Evidence must be a Google Document link (docs.google.com/document/...), a Google Drive file link (drive.google.com/file/...) or a PDF link (ending in .pdf).";

    private static final String TYPE_GOOGLE_DOC = "Google Document";
    private static final String TYPE_PDF = "PDF";
    private static final String TYPE_DRIVE = "Google Drive file";

    private EvidenceLinkValidator() {
    }

    /** Trims the link; blank becomes null so an empty optional field is stored as "no evidence". */
    public static String normalize(String link) {
        if (link == null || link.isBlank()) {
            return null;
        }
        return link.trim();
    }

    /** Returns an error message when the link is not acceptable, or empty when it is valid (or not provided). */
    public static Optional<String> validate(String link) {
        String value = normalize(link);
        if (value == null) {
            return Optional.empty();
        }
        if (value.length() > MAX_LENGTH) {
            return Optional.of("Evidence link is too long (maximum " + MAX_LENGTH + " characters).");
        }
        return detectType(value).isPresent()
                ? Optional.empty()
                : Optional.of(ACCEPTED_TYPES_MESSAGE);
    }

    /** Human-readable type of an acceptable link ("Google Document", "Google Drive file" or "PDF"), or empty. */
    public static Optional<String> detectType(String link) {
        String value = normalize(link);
        if (value == null || value.chars().anyMatch(Character::isWhitespace)) {
            return Optional.empty();
        }
        URI uri;
        try {
            uri = new URI(value);
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
        String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
        String path = uri.getPath() == null ? "" : uri.getPath();

        if (!(scheme.equals("http") || scheme.equals("https")) || host.isEmpty()) {
            return Optional.empty();
        }
        if (scheme.equals("https") && host.equals("docs.google.com") && path.startsWith("/document/")) {
            return Optional.of(TYPE_GOOGLE_DOC);
        }
        if (scheme.equals("https") && host.equals("drive.google.com") && path.startsWith("/file/d/")) {
            return Optional.of(TYPE_DRIVE);
        }
        if (path.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            return Optional.of(TYPE_PDF);
        }
        return Optional.empty();
    }

    /** Label for templates: the detected type, or an empty string. */
    public static String describe(String link) {
        return detectType(link).orElse("");
    }
}
