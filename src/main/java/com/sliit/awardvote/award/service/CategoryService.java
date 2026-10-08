package com.sliit.awardvote.award.service;

import com.sliit.awardvote.award.model.Category;
import com.sliit.awardvote.award.dao.CategoryDao;
import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService extends AbstractCrudService<Category, Long> {

    private final CategoryDao categoryDao;

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    protected GenericDao<Category, Long> getDao() {
        return categoryDao;
    }

    public List<Category> findByProgramme(Long programmeId) {
        return categoryDao.findByAwardProgrammeId(programmeId);
    }
}
