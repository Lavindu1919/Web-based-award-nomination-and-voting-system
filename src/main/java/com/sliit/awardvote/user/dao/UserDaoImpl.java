package com.sliit.awardvote.user.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class UserDaoImpl extends AbstractJpaDao<User> implements UserDao {

    public UserDaoImpl() {
        super(User.class);
    }

    // Find a user by email
    @Override
    public Optional<User> findByUsername(String username) {
        return em.createQuery("select e from User e where e.username = :username", User.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    // Find a user by email
    @Override
    public Optional<User> findByEmail(String email) {
        return em.createQuery("select e from User e where e.email = :email", User.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    // Check whether a username already exists
    @Override
    public boolean existsByUsername(String username) {
        return em.createQuery("select count(e) from User e where e.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult() > 0;
    }

    // Check whether an email already exists
    @Override
    public boolean existsByEmail(String email) {
        return em.createQuery("select count(e) from User e where e.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult() > 0;
    }

    // Find all users with the specified role
    @Override
    public List<User> findByRole(UserRole role) {
        return em.createQuery("select e from User e where e.role = :role", User.class)
                .setParameter("role", role)
                .getResultList();
    }

    // Find users assigned to a specific custom role
    @Override
    public List<User> findByCustomRoleId(Long roleId) {
        return em.createQuery("select e from User e where e.customRole.id = :roleId", User.class)
                .setParameter("roleId", roleId)
                .getResultList();
    }

    @Override
    public long countByRole(UserRole role) {
        return em.createQuery("select count(e) from User e where e.role = :role", Long.class)
                .setParameter("role", role)
                .getSingleResult();
    }

    // Count users with a specific role
    @Override
    @Transactional
    public void deletePermanently(Long userId) {
        // 1. Rows that cannot exist without this user - delete them.
        deleteWhere("delete from Vote v where v.voter.id = :id", userId);
        deleteWhere("delete from Score s where s.judge.id = :id", userId);
        deleteWhere("delete from Notification n where n.recipient.id = :id", userId);
        deleteWhere("delete from OtpCode o where o.user.id = :id", userId);
        deleteWhere("delete from AwardFeedback f where f.user.id = :id", userId);

        // 2. Records that belong to the organisation / other people - keep them, drop the link.
        deleteWhere("update Nomination n set n.submittedBy = null where n.submittedBy.id = :id", userId);
        deleteWhere("update Feedback f set f.submittedBy = null where f.submittedBy.id = :id", userId);
        deleteWhere("update Feedback f set f.handledBy = null where f.handledBy.id = :id", userId);
        deleteWhere("update Announcement a set a.publishedBy = null where a.publishedBy.id = :id", userId);

        // 3. Finally remove the account itself.
        deleteWhere("delete from User u where u.id = :id", userId);
    }

    private void deleteWhere(String jpql, Long userId) {
        em.createQuery(jpql).setParameter("id", userId).executeUpdate();
    }
}
