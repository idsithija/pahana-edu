package com.pahanaedu.util;

import com.pahanaedu.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Entity Manager Utility Tests")
class EntityManagerUtilTest extends BaseTestCase {
    
    @Nested
    @DisplayName("Entity Manager Creation Tests")
    class EntityManagerCreationTests {
        
        @Test
        @DisplayName("Should create EntityManager successfully")
        void testGetEntityManager() {
            if (!isTestEnvironmentReady()) return;
            
            // Act
            EntityManager em = EntityManagerUtil.getEntityManager();
            
            // Assert
            assertNotNull(em);
            assertTrue(em.isOpen());
            
            // Clean up
            em.close();
        }
        
        @Test
        @DisplayName("Should create multiple EntityManager instances")
        void testGetMultipleEntityManagers() {
            if (!isTestEnvironmentReady()) return;
            
            // Act
            EntityManager em1 = EntityManagerUtil.getEntityManager();
            EntityManager em2 = EntityManagerUtil.getEntityManager();
            
            // Assert
            assertNotNull(em1);
            assertNotNull(em2);
            assertNotSame(em1, em2); // Should be different instances
            assertTrue(em1.isOpen());
            assertTrue(em2.isOpen());
            
            // Clean up
            em1.close();
            em2.close();
        }
        
        @Test
        @DisplayName("Should create EntityManager that can be used for transactions")
        void testEntityManagerTransactionCapability() {
            if (!isTestEnvironmentReady()) return;
            
            // Act
            EntityManager em = EntityManagerUtil.getEntityManager();
            
            // Assert
            assertNotNull(em);
            assertNotNull(em.getTransaction());
            assertFalse(em.getTransaction().isActive());
            
            // Test transaction begin/rollback
            assertDoesNotThrow(() -> {
                em.getTransaction().begin();
                assertTrue(em.getTransaction().isActive());
                em.getTransaction().rollback();
                assertFalse(em.getTransaction().isActive());
            });
            
            // Clean up
            em.close();
        }
        
        @Test
        @DisplayName("Should create EntityManager with correct persistence unit")
        void testEntityManagerPersistenceUnit() {
            if (!isTestEnvironmentReady()) return;
            
            // Act
            EntityManager em = EntityManagerUtil.getEntityManager();
            
            // Assert
            assertNotNull(em);
            assertNotNull(em.getMetamodel());
            
            // Verify it can access our entities (if they exist in the persistence unit)
            assertDoesNotThrow(() -> {
                em.getMetamodel().getEntities();
            });
            
            // Clean up
            em.close();
        }
    }
    
    @Nested
    @DisplayName("Resource Management Tests")
    class ResourceManagementTests {
        
        @Test
        @DisplayName("Should handle EntityManager closure correctly")
        void testEntityManagerClosure() {
            if (!isTestEnvironmentReady()) return;
            
            // Act
            EntityManager em = EntityManagerUtil.getEntityManager();
            assertTrue(em.isOpen());
            
            em.close();
            
            // Assert
            assertFalse(em.isOpen());
        }
        
        @Test
        @DisplayName("Should handle multiple close calls gracefully")
        void testMultipleCloseCalls() {
            if (!isTestEnvironmentReady()) return;
            
            // Act
            EntityManager em = EntityManagerUtil.getEntityManager();
            
            // Assert - Should not throw exception on multiple closes
            assertDoesNotThrow(() -> {
                em.close();
                em.close(); // Second close should not cause problems
            });
        }
        
        @Test
        @DisplayName("Should clean up EntityManagerFactory on close")
        void testEntityManagerFactoryClose() {
            // This test verifies that the close method exists and can be called
            // In a real application, this would be called during shutdown
            
            // Act & Assert
            assertDoesNotThrow(() -> {
                EntityManagerUtil.close();
            });
            
            // Note: After calling close(), getting a new EntityManager might fail
            // depending on the implementation, so we won't test that here
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle concurrent access to EntityManager creation")
        void testConcurrentAccess() {
            if (!isTestEnvironmentReady()) return;
            
            // Create multiple threads that try to get EntityManagers simultaneously
            Thread[] threads = new Thread[5];
            EntityManager[] entityManagers = new EntityManager[5];
            Exception[] exceptions = new Exception[5];
            
            for (int i = 0; i < threads.length; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    try {
                        entityManagers[index] = EntityManagerUtil.getEntityManager();
                    } catch (Exception e) {
                        exceptions[index] = e;
                    }
                });
            }
            
            // Start all threads
            for (Thread thread : threads) {
                thread.start();
            }
            
            // Wait for all threads to complete
            for (Thread thread : threads) {
                assertDoesNotThrow(() -> thread.join());
            }
            
            // Assert all EntityManagers were created successfully
            for (int i = 0; i < entityManagers.length; i++) {
                assertNull(exceptions[i], "Thread " + i + " should not have thrown an exception");
                assertNotNull(entityManagers[i], "Thread " + i + " should have created an EntityManager");
                assertTrue(entityManagers[i].isOpen(), "EntityManager " + i + " should be open");
            }
            
            // Clean up
            for (EntityManager em : entityManagers) {
                if (em != null) {
                    em.close();
                }
            }
        }
        
        @Test
        @DisplayName("Should create EntityManager even after previous ones are closed")
        void testCreateAfterClose() {
            if (!isTestEnvironmentReady()) return;
            
            // Act
            EntityManager em1 = EntityManagerUtil.getEntityManager();
            em1.close();
            
            EntityManager em2 = EntityManagerUtil.getEntityManager();
            
            // Assert
            assertNotNull(em2);
            assertTrue(em2.isOpen());
            
            // Clean up
            em2.close();
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should work with BaseDAOImpl fallback mechanism")
        void testDAOIntegration() {
            if (!isTestEnvironmentReady()) return;
            
            // This test verifies that EntityManagerUtil can be used
            // by DAO implementations when dependency injection fails
            
            // Act
            EntityManager em = EntityManagerUtil.getEntityManager();
            
            // Assert - Should be able to perform basic JPA operations
            assertDoesNotThrow(() -> {
                em.getTransaction().begin();
                
                // Test basic query functionality
                em.createQuery("SELECT 1", Integer.class);
                
                em.getTransaction().rollback();
            });
            
            // Clean up
            em.close();
        }
        
        @Test
        @DisplayName("Should provide consistent EntityManagerFactory")
        void testConsistentFactory() {
            if (!isTestEnvironmentReady()) return;
            
            // Act - Create multiple EntityManagers
            EntityManager em1 = EntityManagerUtil.getEntityManager();
            EntityManager em2 = EntityManagerUtil.getEntityManager();
            
            // Assert - Should come from the same factory but be different instances
            assertNotNull(em1);
            assertNotNull(em2);
            assertNotSame(em1, em2);
            
            // Both should have the same metamodel (indicating same persistence unit)
            assertEquals(em1.getMetamodel(), em2.getMetamodel());
            
            // Clean up
            em1.close();
            em2.close();
        }
    }
    
    @Nested
    @DisplayName("Performance and Reliability Tests")
    class PerformanceTests {
        
        @Test
        @DisplayName("Should create EntityManager quickly")
        void testCreationPerformance() {
            if (!isTestEnvironmentReady()) return;
            
            // Measure time to create EntityManager
            long startTime = System.currentTimeMillis();
            EntityManager em = EntityManagerUtil.getEntityManager();
            long endTime = System.currentTimeMillis();
            
            // Assert
            assertNotNull(em);
            assertTrue(em.isOpen());
            
            // EntityManager creation should be reasonably fast (less than 1 second)
            long creationTime = endTime - startTime;
            assertTrue(creationTime < 1000, 
                "EntityManager creation took too long: " + creationTime + "ms");
            
            // Clean up
            em.close();
        }
        
        @Test
        @DisplayName("Should handle multiple rapid creations")
        void testRapidCreation() {
            if (!isTestEnvironmentReady()) return;
            
            // Create and close multiple EntityManagers rapidly
            for (int i = 0; i < 10; i++) {
                EntityManager em = EntityManagerUtil.getEntityManager();
                assertNotNull(em);
                assertTrue(em.isOpen());
                em.close();
            }
            
            // Should complete without errors
            assertTrue(true, "Rapid creation and closure completed successfully");
        }
    }
}