package com.sliit.awardvote.award.dao;

import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.common.dao.AbstractJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class AwardDaoImpl extends AbstractJpaDao<AwardProgramme> implements AwardDao {

    public AwardDaoImpl() {
        super(AwardProgramme.class);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name, Long excludeId) {
        String jpql = "select count(e) from AwardProgramme e where lower(e.name) = :name"
                + (excludeId != null ? " and e.id <> :excludeId" : "");
        var query = em.createQuery(jpql, Long.class).setParameter("name", name.trim().toLowerCase());
        if (excludeId != null) {
            query.setParameter("excludeId", excludeId);
        }
        return query.getSingleResult() > 0;
    }
}
