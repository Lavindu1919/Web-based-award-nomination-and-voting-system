//Provide a database access layer for Sponsor entities, using common methods from GenericDao
//defines the rules
package com.sliit.awardvote.sponsor.dao;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.sponsor.model.Sponsor;

// This is a Javadoc comment: explains that this interface is for Sponsor database access
/** Data access contract for {@link Sponsor}. */

// Defines SponsorDao as an interface (not a class)
// It extends GenericDao, meaning it inherits all basic database methods
// <Sponsor, Long> means this DAO works with Sponsor objects, and their primary key is of type Long
public interface SponsorDao extends GenericDao<Sponsor, Long> {
}
// This interface extends GenericDao, which already has CRUD methods:
// - save(Sponsor entity) → Create
// - findById(Long id) → Read
// - update(Sponsor entity) → Update
// - delete(Sponsor entity) → Delete