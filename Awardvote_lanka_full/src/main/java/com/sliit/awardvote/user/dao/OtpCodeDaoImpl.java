package com.sliit.awardvote.user.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.user.model.OtpCode;
import com.sliit.awardvote.user.model.OtpPurpose;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class OtpCodeDaoImpl extends AbstractJpaDao<OtpCode> implements OtpCodeDao {

    // Initialize the parent DAO with the OtpCode entity
    public OtpCodeDaoImpl() {
        super(OtpCode.class);
    }

    @Override
    public List<OtpCode> findByUserIdAndPurposeAndUsedFalse(Long userId, OtpPurpose purpose) {

        // Find unused OTP codes for a specific user and purpose
        return em.createQuery(
                        "select e from OtpCode e " +
                                "where e.user.id = :userId " +
                                "and e.purpose = :purpose " +
                                "and e.used = false",
                        OtpCode.class
                )
                // Set the user ID parameter
                .setParameter("userId", userId)

                // Set the OTP purpose parameter
                .setParameter("purpose", purpose)

                // Return all matching OTP records
                .getResultList();
    }
}