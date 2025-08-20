package com.pahanaedu.dao.impl;

import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.model.User;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserDAO interface
 * Provides user-specific database operations using JPA
 */
@Stateless
public class UserDAOImpl extends BaseDAOImpl<User, String> implements UserDAO {
    
    @Override
    public Optional<User> findByUsernameAndPassword(String username, String passwordHash) {
        try {
            TypedQuery<User> query = createQuery(
                "SELECT u FROM User u WHERE u.username = :username AND u.passwordHash = :passwordHash AND u.active = true"
            );
            query.setParameter("username", username);
            query.setParameter("passwordHash", passwordHash);
            
            User user = query.getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        try {
            TypedQuery<User> query = createQuery(
                "SELECT u FROM User u WHERE u.username = :username"
            );
            query.setParameter("username", username);
            
            User user = query.getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<User> findActiveUsers() {
        TypedQuery<User> query = createQuery(
            "SELECT u FROM User u WHERE u.active = true ORDER BY u.username"
        );
        return query.getResultList();
    }
    
    @Override
    public List<User> findByRole(User.UserRole role) {
        TypedQuery<User> query = createQuery(
            "SELECT u FROM User u WHERE u.role = :role ORDER BY u.username"
        );
        query.setParameter("role", role);
        return query.getResultList();
    }
    
    @Override
    public boolean usernameExists(String username) {
        TypedQuery<Long> query = getEntityManager().createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class
        );
        query.setParameter("username", username);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public User updateActiveStatus(String username, boolean active) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(active);
            return update(user);
        }
        throw new IllegalArgumentException("User not found: " + username);
    }
    
    @Override
    public User updatePassword(String username, String newPasswordHash) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPasswordHash(newPasswordHash);
            return update(user);
        }
        throw new IllegalArgumentException("User not found: " + username);
    }
    
    @Override
    public void delete(String username) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            delete(userOpt.get());
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }
}

