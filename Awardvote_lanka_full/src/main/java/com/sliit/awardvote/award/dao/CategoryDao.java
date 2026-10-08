package com.sliit.awardvote.award.dao;

import com.sliit.awardvote.award.model.Category;
import com.sliit.awardvote.common.dao.GenericDao;

import java.util.List;

/** Data access contract for {@link Category}. */
//An interface that defines the methods required to access category data from the database.
public interface CategoryDao extends GenericDao<Category, Long> {
    List<Category> findByAwardProgrammeId(Long programmeId);
}
