package com.sliit.awardvote.nominee.dao;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.nominee.model.Nomination;
import com.sliit.awardvote.nominee.model.NominationStatus;

import java.util.List;

/** Data access contract for {@link Nomination}. */
public interface NominationDao extends GenericDao<Nomination, Long> {
    List<Nomination> findByCategoryId(Long categoryId);

    List<Nomination> findByStatus(NominationStatus status);

    List<Nomination> findBySubmittedById(Long userId);

    long countByStatus(NominationStatus status);
}
