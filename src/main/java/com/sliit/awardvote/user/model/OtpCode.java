package com.sliit.awardvote.user.model;

import com.sliit.awardvote.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;


 //Represents a single-use OTP code with a limited validity period.
 //Used for account verification and password reset.

@Entity
@Table(name = "otp_codes")
public class OtpCode extends BaseEntity {

    // User who requested the OTP
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Six-digit OTP code
    @Column(nullable = false, length = 6)
    private String code;

    // Purpose of the OTP, such as account verification or password reset
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpPurpose purpose;

    // Date and time when the OTP expires
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // Prevent the same OTP from being used more than once
    private boolean used = false;

    protected OtpCode() {
        // Required by JPA
    }

    // Create an OTP with its user, code, purpose and expiration time
    public OtpCode(User user, String code, OtpPurpose purpose, LocalDateTime expiresAt) {
        this.user = user;
        this.code = code;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
    }

    // Check whether the OTP is unused, not expired and matches the entered code
    public boolean isValid(String candidateCode) {
        return !used
                && LocalDateTime.now().isBefore(expiresAt)
                && code.equals(candidateCode);
    }

    // Mark the OTP as used after successful verification
    public void markUsed() {
        this.used = true;
    }

    public User getUser() {
        return user;
    }

    public String getCode() {
        return code;
    }

    public OtpPurpose getPurpose() {
        return purpose;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }
}