package com.sliit.awardvote.user.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.user.model.Role;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class RoleDaoImpl extends AbstractJpaDao<Role> implements RoleDao {

    // Initialize the parent DAO with the Role entity
    public RoleDaoImpl() {
        super(Role.class);
    }

    // Find a role by its name
    @Override
    public Optional<Role> findByName(String name) {
        return em.createQuery("select e from Role e where e.name = :name", Role.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    // Check whether a role with the given name already exists
    @Override
    public boolean existsByName(String name) {
        return em.createQuery(
                        "select count(e) from Role e where e.name = :name",
                        Long.class
                )
                .setParameter("name", name)
                .getSingleResult() > 0;
    }
}