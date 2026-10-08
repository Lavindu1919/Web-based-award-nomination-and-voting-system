package com.sliit.awardvote.content.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.content.model.Faq;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class FaqDaoImpl extends AbstractJpaDao<Faq> implements FaqDao {

    public FaqDaoImpl() {
        super(Faq.class);
    }
}
