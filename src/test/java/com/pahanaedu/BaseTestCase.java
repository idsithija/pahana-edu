package com.pahanaedu;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.logging.Logger;

/**
 * Base test case for all JUnit tests
 * Provides common test infrastructure and utilities
 * Academic-compliant testing framework setup
 */
public abstract class BaseTestCase {
    
    protected static final Logger logger = Logger.getLogger(BaseTestCase.class.getName());
    
    protected static EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;
    
    @BeforeAll
    static void setUpClass() {
        try {
            // Initialize test persistence unit
            entityManagerFactory = Persistence.createEntityManagerFactory("pahanaedu-test-pu");
            logger.info("Test EntityManagerFactory initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize test EntityManagerFactory: " + e.getMessage());
            // Fall back to in-memory H2 for CI/CD environments
            try {
                System.setProperty("javax.persistence.jdbc.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
                System.setProperty("javax.persistence.jdbc.driver", "org.h2.Driver");
                System.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
                entityManagerFactory = Persistence.createEntityManagerFactory("pahanaedu-test-pu");
                logger.info("Fallback H2 EntityManagerFactory initialized");
            } catch (Exception fallbackException) {
                logger.severe("Failed to initialize fallback EntityManagerFactory: " + fallbackException.getMessage());
                throw new RuntimeException("Cannot initialize test database", fallbackException);
            }
        }
    }
    
    @AfterAll
    static void tearDownClass() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            logger.info("Test EntityManagerFactory closed");
        }
    }
    
    @BeforeEach
    protected void setUp() {
        if (entityManagerFactory != null) {
            entityManager = entityManagerFactory.createEntityManager();
            logger.fine("Test EntityManager created for test case");
        }
    }
    
    @AfterEach
    protected void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
            logger.fine("Test EntityManager closed");
        }
    }
    
    /**
     * Helper method to begin transaction
     */
    protected void beginTransaction() {
        if (entityManager != null && !entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
    }
    
    /**
     * Helper method to commit transaction
     */
    protected void commitTransaction() {
        if (entityManager != null && entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        }
    }
    
    /**
     * Helper method to rollback transaction
     */
    protected void rollbackTransaction() {
        if (entityManager != null && entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
    }
    
    /**
     * Helper method to clear entity manager cache
     */
    protected void clearEntityManager() {
        if (entityManager != null) {
            entityManager.clear();
        }
    }
    
    /**
     * Check if test environment is properly set up
     */
    protected boolean isTestEnvironmentReady() {
        return entityManagerFactory != null && entityManager != null;
    }
}