package com.sliit.awardvote.user.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.user.model.Role;
import com.sliit.awardvote.user.dao.RoleDao;

import org.springframework.stereotype.Service;

@Service
public class RoleService extends AbstractCrudService<Role, Long> {

    // DAO used for role-related database operations
    private final RoleDao roleDao;

    // Constructor injection for RoleDao
    public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    // Provides RoleDao to the parent CRUD service
    @Override
    protected GenericDao<Role, Long> getDao() {
        return roleDao;
    }

    // Checks whether a role name already exists
    public boolean nameTaken(String name) {
        return roleDao.existsByName(name);
    }
}