package com.pahanaedu.dao;

import java.util.List;
import java.util.Optional;

/**
 * Base DAO interface providing common CRUD operations
 * Generic interface to be extended by specific entity DAOs
 * 
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface BaseDAO<T, ID> {
    
    /**
     * Save a new entity
     * @param entity Entity to save
     * @return Saved entity
     */
    T save(T entity);
    
    /**
     * Update an existing entity
     * @param entity Entity to update
     * @return Updated entity
     */
    T update(T entity);
    
    /**
     * Delete an entity by ID
     * @param id Primary key of entity to delete
     */
    void deleteById(ID id);
    
    /**
     * Delete an entity
     * @param entity Entity to delete
     */
    void delete(T entity);
    
    /**
     * Find entity by ID
     * @param id Primary key
     * @return Optional containing entity if found
     */
    Optional<T> findById(ID id);
    
    /**
     * Find all entities
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Count total number of entities
     * @return Total count
     */
    long count();
    
    /**
     * Check if entity exists by ID
     * @param id Primary key
     * @return true if entity exists
     */
    boolean existsById(ID id);
    
    /**
     * Find entities with pagination
     * @param offset Starting position
     * @param limit Maximum number of results
     * @return List of entities
     */
    List<T> findWithPagination(int offset, int limit);
}

