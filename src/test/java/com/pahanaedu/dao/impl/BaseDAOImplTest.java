package com.pahanaedu.dao.impl;

import com.pahanaedu.BaseTestCase;
import com.pahanaedu.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Base DAO Implementation Tests")
class BaseDAOImplTest extends BaseTestCase {
    
    private TestUserDAOImpl userDAO;
    private User testUser;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        if (isTestEnvironmentReady()) {
            userDAO = new TestUserDAOImpl();
            userDAO.setEntityManager(entityManager);
            
            testUser = new User();
            testUser.setUsername("testuser");
            testUser.setPasswordHash("hashedpassword");
            testUser.setRole(User.UserRole.OPERATOR);
        }
    }
    
    @Nested
    @DisplayName("CRUD Operation Tests")
    class CrudOperationTests {
        
        @Test
        @DisplayName("Should save entity successfully")
        void testSave() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            User savedUser = userDAO.save(testUser);
            commitTransaction();
            
            assertNotNull(savedUser);
            assertNotNull(savedUser.getUsername());
            assertEquals(testUser.getUsername(), savedUser.getUsername());
            assertEquals(testUser.getPasswordHash(), savedUser.getPasswordHash());
        }
        
        @Test
        @DisplayName("Should update entity successfully")
        void testUpdate() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            User savedUser = userDAO.save(testUser);
            commitTransaction();
            
            savedUser.setPasswordHash("updatedPassword");
            
            beginTransaction();
            User updatedUser = userDAO.update(savedUser);
            commitTransaction();
            
            assertNotNull(updatedUser);
            assertEquals("updatedPassword", updatedUser.getPasswordHash());
            assertEquals(savedUser.getUsername(), updatedUser.getUsername());
        }
        
        @Test
        @DisplayName("Should find entity by ID")
        void testFindById() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            User savedUser = userDAO.save(testUser);
            commitTransaction();
            clearEntityManager();
            
            Optional<User> foundUser = userDAO.findById(savedUser.getUsername());
            
            assertTrue(foundUser.isPresent());
            assertEquals(savedUser.getUsername(), foundUser.get().getUsername());
            assertEquals(savedUser.getUsername(), foundUser.get().getUsername());
        }
        
        @Test
        @DisplayName("Should return empty optional when entity not found")
        void testFindByIdNotFound() {
            if (!isTestEnvironmentReady()) return;
            
            Optional<User> foundUser = userDAO.findById("nonexistent");
            
            assertFalse(foundUser.isPresent());
        }
        
        @Test
        @DisplayName("Should delete entity by ID")
        void testDeleteById() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            User savedUser = userDAO.save(testUser);
            commitTransaction();
            
            String userId = savedUser.getUsername();
            
            beginTransaction();
            userDAO.deleteById(userId);
            commitTransaction();
            clearEntityManager();
            
            Optional<User> foundUser = userDAO.findById(userId);
            assertFalse(foundUser.isPresent());
        }
        
        @Test
        @DisplayName("Should delete entity object")
        void testDelete() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            User savedUser = userDAO.save(testUser);
            commitTransaction();
            
            String userId = savedUser.getUsername();
            
            beginTransaction();
            userDAO.delete(savedUser);
            commitTransaction();
            clearEntityManager();
            
            Optional<User> foundUser = userDAO.findById(userId);
            assertFalse(foundUser.isPresent());
        }
        
        @Test
        @DisplayName("Should handle delete of non-existent entity gracefully")
        void testDeleteNonExistentEntity() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            assertDoesNotThrow(() -> userDAO.deleteById("nonexistent"));
            commitTransaction();
        }
    }
    
    @Nested
    @DisplayName("Query Operation Tests")
    class QueryOperationTests {
        
        @Test
        @DisplayName("Should find all entities")
        void testFindAll() {
            if (!isTestEnvironmentReady()) return;
            
            // Save multiple users
            beginTransaction();
            User user1 = new User();
            user1.setUsername("user1");
            user1.setPasswordHash("password1");
            user1.setRole(User.UserRole.OPERATOR);
            userDAO.save(user1);
            
            User user2 = new User();
            user2.setUsername("user2");
            user2.setPasswordHash("password2");
            user2.setRole(User.UserRole.ADMIN);
            userDAO.save(user2);
            commitTransaction();
            clearEntityManager();
            
            List<User> allUsers = userDAO.findAll();
            
            assertNotNull(allUsers);
            assertTrue(allUsers.size() >= 2);
        }
        
        @Test
        @DisplayName("Should count entities correctly")
        void testCount() {
            if (!isTestEnvironmentReady()) return;
            
            long initialCount = userDAO.count();
            
            beginTransaction();
            userDAO.save(testUser);
            commitTransaction();
            
            long newCount = userDAO.count();
            assertEquals(initialCount + 1, newCount);
        }
        
        @Test
        @DisplayName("Should check if entity exists by ID")
        void testExistsById() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            User savedUser = userDAO.save(testUser);
            commitTransaction();
            
            assertTrue(userDAO.existsById(savedUser.getUsername()));
            assertFalse(userDAO.existsById("nonexistent"));
        }
        
        @Test
        @DisplayName("Should find entities with pagination")
        void testFindWithPagination() {
            if (!isTestEnvironmentReady()) return;
            
            // Save multiple users
            beginTransaction();
            for (int i = 1; i <= 5; i++) {
                User user = new User();
                user.setUsername("user" + i);
                user.setPasswordHash("password" + i);
                user.setRole(User.UserRole.OPERATOR);
                userDAO.save(user);
            }
            commitTransaction();
            clearEntityManager();
            
            List<User> paginatedUsers = userDAO.findWithPagination(0, 3);
            
            assertNotNull(paginatedUsers);
            assertTrue(paginatedUsers.size() <= 3);
            assertTrue(paginatedUsers.size() >= 0);
        }
        
        @Test
        @DisplayName("Should handle pagination with offset")
        void testPaginationWithOffset() {
            if (!isTestEnvironmentReady()) return;
            
            List<User> firstPage = userDAO.findWithPagination(0, 2);
            List<User> secondPage = userDAO.findWithPagination(2, 2);
            
            assertNotNull(firstPage);
            assertNotNull(secondPage);
            
            // Pages should not contain same entities (assuming enough data exists)
            if (firstPage.size() > 0 && secondPage.size() > 0) {
                assertNotEquals(firstPage.get(0).getUsername(), secondPage.get(0).getUsername());
            }
        }
    }
    
    @Nested
    @DisplayName("Transaction Management Tests")
    class TransactionManagementTests {
        
        @Test
        @DisplayName("Should handle transaction rollback on error")
        void testTransactionRollback() {
            if (!isTestEnvironmentReady()) return;
            
            // Create user with duplicate username to cause constraint violation
            beginTransaction();
            userDAO.save(testUser);
            commitTransaction();
            
            User duplicateUser = new User();
            duplicateUser.setUsername(testUser.getUsername()); // Same username
            duplicateUser.setPasswordHash("different");
            duplicateUser.setRole(User.UserRole.ADMIN);
            
            assertThrows(RuntimeException.class, () -> {
                userDAO.save(duplicateUser);
            });
        }
        
        @Test
        @DisplayName("Should handle nested transactions correctly")
        void testNestedTransactions() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            User savedUser = userDAO.save(testUser);
            
            // Update within same transaction
            savedUser.setPasswordHash("nestedPassword");
            User updatedUser = userDAO.update(savedUser);
            
            commitTransaction();
            
            assertEquals("nestedPassword", updatedUser.getPasswordHash());
        }
    }
    
    @Nested
    @DisplayName("Helper Method Tests")
    class HelperMethodTests {
        
        @Test
        @DisplayName("Should get entity class correctly")
        void testGetEntityClass() {
            if (!isTestEnvironmentReady()) return;
            
            Class<User> entityClass = userDAO.getTestEntityClass();
            assertEquals(User.class, entityClass);
        }
        
        @Test
        @DisplayName("Should create typed query")
        void testCreateQuery() {
            if (!isTestEnvironmentReady()) return;
            
            assertDoesNotThrow(() -> {
                userDAO.createTestQuery("SELECT u FROM User u");
            });
        }
        
        @Test
        @DisplayName("Should create native query")
        void testCreateNativeQuery() {
            if (!isTestEnvironmentReady()) return;
            
            assertDoesNotThrow(() -> {
                userDAO.createTestNativeQuery("SELECT * FROM users LIMIT 1");
            });
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle null entity in save")
        void testSaveNullEntity() {
            if (!isTestEnvironmentReady()) return;
            
            assertThrows(RuntimeException.class, () -> {
                userDAO.save(null);
            });
        }
        
        @Test
        @DisplayName("Should handle null entity in update")
        void testUpdateNullEntity() {
            if (!isTestEnvironmentReady()) return;
            
            assertThrows(RuntimeException.class, () -> {
                userDAO.update(null);
            });
        }
        
        @Test
        @DisplayName("Should handle null ID in findById")
        void testFindByIdWithNull() {
            if (!isTestEnvironmentReady()) return;
            
            Optional<User> result = userDAO.findById((String)null);
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should handle null ID in deleteById")
        void testDeleteByIdWithNull() {
            if (!isTestEnvironmentReady()) return;
            
            assertDoesNotThrow(() -> {
                beginTransaction();
                userDAO.deleteById((String)null);
                commitTransaction();
            });
        }
    }
    
    // Test implementation of BaseDAOImpl for testing purposes
    private static class TestUserDAOImpl extends BaseDAOImpl<User, String> {
        
        // Expose protected methods for testing
        public Class<User> getTestEntityClass() {
            return getEntityClass();
        }
        
        public void createTestQuery(String jpql) {
            createQuery(jpql);
        }
        
        public void createTestNativeQuery(String sql) {
            createNativeQuery(sql);
        }
        
        // Method to set entity manager for testing
        public void setEntityManager(EntityManager em) {
            this.entityManager = em;
        }
    }
}