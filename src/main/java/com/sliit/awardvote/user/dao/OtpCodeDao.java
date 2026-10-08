package com.sliit.awardvote.user.dao;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.user.model.OtpCode;
import com.sliit.awardvote.user.model.OtpPurpose;

import java.util.List;

/** Data access contract for {@link OtpCode}. */
public interface OtpCodeDao extends GenericDao<OtpCode, Long> {

    // Find all unused OTP codes for a specific user and purpose
    List<OtpCode> findByUserIdAndPurposeAndUsedFalse(Long userId, OtpPurpose purpose);
}