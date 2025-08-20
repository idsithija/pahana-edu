package com.pahanaedu.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.logging.Logger;

/**
 * Utility class for EntityManager creation when JPA injection fails
 * Used as fallback for development environments without full EJB container
 */
public class EntityManagerUtil {
    
    private static final Logger logger = Logger.getLogger(EntityManagerUtil.class.getName());
    private static EntityManagerFactory emf;
    
    static {
        try {
            emf = Persistence.createEntityManagerFactory("PahanaEduPU");
            logger.info("EntityManagerFactory created successfully for development mode");
        } catch (Exception e) {
            logger.severe("Failed to create EntityManagerFactory: " + e.getMessage());
            throw new RuntimeException("Could not initialize EntityManagerFactory", e);
        }
    }
    
    /**
     * Get EntityManager instance
     * @return EntityManager
     */
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    /**
     * Close EntityManagerFactory (for shutdown)
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}