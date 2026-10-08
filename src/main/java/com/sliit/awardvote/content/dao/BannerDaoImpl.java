package com.sliit.awardvote.content.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.content.model.Banner;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class BannerDaoImpl extends AbstractJpaDao<Banner> implements BannerDao {

    public BannerDaoImpl() {
        super(Banner.class);
    }
}
