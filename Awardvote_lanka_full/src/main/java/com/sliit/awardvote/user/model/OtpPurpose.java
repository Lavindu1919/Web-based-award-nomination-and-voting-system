package com.sliit.awardvote.user.model;

/**
 * What a given {@link OtpCode} was issued for. Kept separate from the code
 * itself so a code issued to verify a new account can never accidentally be
 * used to reset a password, or vice versa.
 */
public enum OtpPurpose {
    ACCOUNT_VERIFICATION,
    PASSWORD_RESET
}
