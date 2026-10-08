package com.sliit.awardvote.content.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.content.model.Announcement;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class AnnouncementDaoImpl extends AbstractJpaDao<Announcement> implements AnnouncementDao {

    public AnnouncementDaoImpl() {
        super(Announcement.class);
    }
}
