//Provide the actual database access logic for Sponsor entities, using JPA
//provides the real implementation
package com.sliit.awardvote.sponsor.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.sponsor.model.Sponsor;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
// Marks this class as a Spring Repository (database access component)
@Transactional(readOnly = true)
// Makes all methods run inside a transaction (read-only by default for safety)

// This class extends AbstractJpaDao (generic JPA operations) and implements SponsorDao (our custom contract)
public class SponsorDaoImpl extends AbstractJpaDao<Sponsor> implements SponsorDao {

    // Constructor: tells AbstractJpaDao that this DAO works with the Sponsor entity
    public SponsorDaoImpl() {
        super(Sponsor.class);
    }
}