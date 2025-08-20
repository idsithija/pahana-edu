package com.pahanaedu.dao.impl;

import com.pahanaedu.dao.BaseDAO;
import com.pahanaedu.util.EntityManagerUtil;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Base DAO implementation providing common CRUD operations
 * Abstract class to be extended by specific entity DAO implementations
 * 
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public abstract class BaseDAOImpl<T, ID> implements BaseDAO<T, ID> {
    
    private static final Logger logger = Logger.getLogger(BaseDAOImpl.class.getName());
    
    @PersistenceContext(unitName = "PahanaEduPU")
    protected EntityManager entityManager;
    
    private final Class<T> entityClass;
    
    @SuppressWarnings("unchecked")
    public BaseDAOImpl() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    @Override
    public T save(T entity) {
        EntityManager em = getEntityManager();
        boolean isOwnTransaction = !em.getTransaction().isActive();
        
        try {
            if (isOwnTransaction) {
                em.getTransaction().begin();
            }
            
            em.persist(entity);
            em.flush();
            
            if (isOwnTransaction) {
                em.getTransaction().commit();
            }
            
            return entity;
        } catch (Exception e) {
            if (isOwnTransaction && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error saving entity", e);
        } finally {
            if (isOwnTransaction && entityManager == null) {
                // Only close if we created it (fallback mode)
                em.close();
            }
        }
    }
    
    @Override
    public T update(T entity) {
        EntityManager em = getEntityManager();
        boolean isOwnTransaction = !em.getTransaction().isActive();
        
        try {
            if (isOwnTransaction) {
                em.getTransaction().begin();
            }
            
            T updatedEntity = em.merge(entity);
            em.flush();
            
            if (isOwnTransaction) {
                em.getTransaction().commit();
            }
            
            return updatedEntity;
        } catch (Exception e) {
            if (isOwnTransaction && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error updating entity", e);
        } finally {
            if (isOwnTransaction && entityManager == null) {
                // Only close if we created it (fallback mode)
                em.close();
            }
        }
    }
    
    @Override
    public void deleteById(ID id) {
        EntityManager em = getEntityManager();
        boolean isOwnTransaction = !em.getTransaction().isActive();
        
        try {
            if (isOwnTransaction) {
                em.getTransaction().begin();
            }
            
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
                em.flush();
            }
            
            if (isOwnTransaction) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (isOwnTransaction && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error deleting entity with id: " + id, e);
        } finally {
            if (isOwnTransaction && entityManager == null) {
                // Only close if we created it (fallback mode)
                em.close();
            }
        }
    }
    
    @Override
    public void delete(T entity) {
        EntityManager em = getEntityManager();
        boolean isOwnTransaction = !em.getTransaction().isActive();
        
        try {
            if (isOwnTransaction) {
                em.getTransaction().begin();
            }
            
            if (em.contains(entity)) {
                em.remove(entity);
            } else {
                em.remove(em.merge(entity));
            }
            em.flush();
            
            if (isOwnTransaction) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (isOwnTransaction && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error deleting entity", e);
        } finally {
            if (isOwnTransaction && entityManager == null) {
                // Only close if we created it (fallback mode)
                em.close();
            }
        }
    }
    
    @Override
    public Optional<T> findById(ID id) {
        EntityManager em = getEntityManager();
        try {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        } finally {
            if (entityManager == null) {
                // Only close if we created it (fallback mode)
                em.close();
            }
        }
    }
    
    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root);
            
            TypedQuery<T> query = em.createQuery(cq);
            return query.getResultList();
        } finally {
            if (entityManager == null) {
                // Only close if we created it (fallback mode)
                em.close();
            }
        }
    }
    
    @Override
    public long count() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> root = cq.from(entityClass);
            cq.select(cb.count(root));
            
            TypedQuery<Long> query = em.createQuery(cq);
            return query.getSingleResult();
        } finally {
            if (entityManager == null) {
                // Only close if we created it (fallback mode)
                em.close();
            }
        }
    }
    
    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }
    
    @Override
    public List<T> findWithPagination(int offset, int limit) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root);
            
            TypedQuery<T> query = em.createQuery(cq);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            
            return query.getResultList();
        } finally {
            if (entityManager == null) {
                // Only close if we created it (fallback mode)
                em.close();
            }
        }
    }
    
    /**
     * Get the entity class
     * @return Entity class
     */
    protected Class<T> getEntityClass() {
        return entityClass;
    }
    
    /**
     * Get the entity manager with fallback for development environments
     * @return Entity manager
     */
    protected EntityManager getEntityManager() {
        if (entityManager == null) {
            // Fallback for development environments without full EJB container
            logger.warning("PersistenceContext injection failed, using EntityManagerUtil for development");
            return EntityManagerUtil.getEntityManager();
        }
        return entityManager;
    }
    
    /**
     * Execute a named query
     * @param queryName Named query name
     * @return TypedQuery for further configuration
     */
    protected TypedQuery<T> createNamedQuery(String queryName) {
        return getEntityManager().createNamedQuery(queryName, entityClass);
    }
    
    /**
     * Execute a JPQL query
     * @param jpql JPQL query string
     * @return TypedQuery for further configuration
     */
    protected TypedQuery<T> createQuery(String jpql) {
        return getEntityManager().createQuery(jpql, entityClass);
    }
    
    /**
     * Execute a native SQL query
     * @param sql Native SQL query string
     * @return Query for further configuration
     */
    protected javax.persistence.Query createNativeQuery(String sql) {
        return getEntityManager().createNativeQuery(sql, entityClass);
    }
}

