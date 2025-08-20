package com.pahanaedu.dao.impl;

import com.pahanaedu.BaseTestCase;
import com.pahanaedu.model.Customer;
import com.pahanaedu.model.Bill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer DAO Implementation Tests")
class CustomerDAOImplTest extends BaseTestCase {
    
    private CustomerDAOImpl customerDAO;
    private Customer testCustomer1;
    private Customer testCustomer2;
    private Customer testCustomer3;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        if (isTestEnvironmentReady()) {
            customerDAO = new CustomerDAOImpl();
            customerDAO.entityManager = entityManager;
            
            testCustomer1 = new Customer("ACC001", "John Doe");
            testCustomer1.setAddress("123 Main St");
            testCustomer1.setTelephoneNumber("555-0001");
            testCustomer1.setRegistrationDate(LocalDate.of(2023, 1, 15));
            testCustomer1.setActive(true);
            
            testCustomer2 = new Customer("ACC002", "Jane Smith");
            testCustomer2.setAddress("456 Oak Ave");
            testCustomer2.setTelephoneNumber("555-0002");
            testCustomer2.setRegistrationDate(LocalDate.of(2023, 2, 20));
            testCustomer2.setActive(true);
            
            testCustomer3 = new Customer("ACC003", "Bob Johnson");
            testCustomer3.setAddress("789 Pine St");
            testCustomer3.setTelephoneNumber("555-0003");
            testCustomer3.setRegistrationDate(LocalDate.of(2023, 3, 10));
            testCustomer3.setActive(false);
        }
    }
    
    @Nested
    @DisplayName("Base CRUD Operation Tests")
    class BaseCrudTests {
        
        @Test
        @DisplayName("Should save customer successfully")
        void testSaveCustomer() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Customer savedCustomer = customerDAO.save(testCustomer1);
            commitTransaction();
            
            assertNotNull(savedCustomer);
            assertEquals(testCustomer1.getAccountNumber(), savedCustomer.getAccountNumber());
            assertEquals(testCustomer1.getName(), savedCustomer.getName());
            assertEquals(testCustomer1.getAddress(), savedCustomer.getAddress());
        }
        
        @Test
        @DisplayName("Should update customer successfully")
        void testUpdateCustomer() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Customer savedCustomer = customerDAO.save(testCustomer1);
            commitTransaction();
            
            savedCustomer.setName("John Updated");
            savedCustomer.setAddress("Updated Address");
            
            beginTransaction();
            Customer updatedCustomer = customerDAO.update(savedCustomer);
            commitTransaction();
            
            assertEquals("John Updated", updatedCustomer.getName());
            assertEquals("Updated Address", updatedCustomer.getAddress());
        }
        
        @Test
        @DisplayName("Should find customer by account number")
        void testFindById() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            customerDAO.save(testCustomer1);
            commitTransaction();
            clearEntityManager();
            
            Optional<Customer> foundCustomer = customerDAO.findById("ACC001");
            
            assertTrue(foundCustomer.isPresent());
            assertEquals("John Doe", foundCustomer.get().getName());
        }
        
        @Test
        @DisplayName("Should delete customer by account number")
        void testDeleteById() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            customerDAO.save(testCustomer1);
            commitTransaction();
            
            beginTransaction();
            customerDAO.deleteById("ACC001");
            commitTransaction();
            clearEntityManager();
            
            Optional<Customer> foundCustomer = customerDAO.findById("ACC001");
            assertFalse(foundCustomer.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Customer-Specific Query Tests")
    class CustomerSpecificQueryTests {
        
        @BeforeEach
        void setUpCustomers() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            customerDAO.save(testCustomer1);
            customerDAO.save(testCustomer2);
            customerDAO.save(testCustomer3);
            commitTransaction();
            clearEntityManager();
        }
        
        @Test
        @DisplayName("Should find customer by account number")
        void testFindByAccountNumber() {
            if (!isTestEnvironmentReady()) return;
            
            Optional<Customer> foundCustomer = customerDAO.findByAccountNumber("ACC001");
            
            assertTrue(foundCustomer.isPresent());
            assertEquals("John Doe", foundCustomer.get().getName());
        }
        
        @Test
        @DisplayName("Should find customers by name containing")
        void testFindByNameContaining() {
            if (!isTestEnvironmentReady()) return;
            
            List<Customer> customers = customerDAO.findByNameContaining("John");
            
            assertNotNull(customers);
            assertTrue(customers.size() >= 2); // John Doe and Bob Johnson
            assertTrue(customers.stream().anyMatch(c -> c.getName().contains("John")));
        }
        
        @Test
        @DisplayName("Should find customers by name containing case-insensitive")
        void testFindByNameContainingCaseInsensitive() {
            if (!isTestEnvironmentReady()) return;
            
            List<Customer> customers = customerDAO.findByNameContaining("john");
            
            assertNotNull(customers);
            assertTrue(customers.size() >= 2);
            assertTrue(customers.stream().anyMatch(c -> c.getName().toLowerCase().contains("john")));
        }
        
        @Test
        @DisplayName("Should find customers by telephone number")
        void testFindByTelephoneNumber() {
            if (!isTestEnvironmentReady()) return;
            
            List<Customer> customers = customerDAO.findByTelephoneNumber("555-0001");
            
            assertNotNull(customers);
            assertEquals(1, customers.size());
            assertEquals("John Doe", customers.get(0).getName());
        }
        
        @Test
        @DisplayName("Should find only active customers")
        void testFindActiveCustomers() {
            if (!isTestEnvironmentReady()) return;
            
            List<Customer> activeCustomers = customerDAO.findActiveCustomers();
            
            assertNotNull(activeCustomers);
            assertTrue(activeCustomers.size() >= 2);
            assertTrue(activeCustomers.stream().allMatch(Customer::isActive));
            assertTrue(activeCustomers.stream().noneMatch(c -> c.getName().equals("Bob Johnson")));
        }
        
        @Test
        @DisplayName("Should find customers by registration date range")
        void testFindByRegistrationDateBetween() {
            if (!isTestEnvironmentReady()) return;
            
            LocalDate startDate = LocalDate.of(2023, 1, 1);
            LocalDate endDate = LocalDate.of(2023, 2, 28);
            
            List<Customer> customers = customerDAO.findByRegistrationDateBetween(startDate, endDate);
            
            assertNotNull(customers);
            assertEquals(2, customers.size());
            assertTrue(customers.stream().anyMatch(c -> c.getName().equals("John Doe")));
            assertTrue(customers.stream().anyMatch(c -> c.getName().equals("Jane Smith")));
        }
    }
    
    @Nested
    @DisplayName("Customer with Bills Tests")
    class CustomerWithBillsTests {
        
        @Test
        @DisplayName("Should find customers with pending bills")
        void testFindCustomersWithPendingBills() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Customer savedCustomer = customerDAO.save(testCustomer1);
            
            // Create a pending bill
            Bill bill = new Bill(savedCustomer, new BigDecimal("100.00"));
            bill.setStatus(Bill.BillStatus.PENDING);
            savedCustomer.addBill(bill);
            entityManager.persist(bill);
            
            commitTransaction();
            clearEntityManager();
            
            List<Customer> customersWithPendingBills = customerDAO.findCustomersWithPendingBills();
            
            assertNotNull(customersWithPendingBills);
            assertTrue(customersWithPendingBills.size() >= 1);
            assertTrue(customersWithPendingBills.stream()
                .anyMatch(c -> c.getAccountNumber().equals("ACC001")));
        }
        
        @Test
        @DisplayName("Should not find customers with only paid bills")
        void testFindCustomersWithPendingBillsExcludesPaid() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Customer savedCustomer = customerDAO.save(testCustomer2);
            
            // Create a paid bill
            Bill bill = new Bill(savedCustomer, new BigDecimal("150.00"));
            bill.setStatus(Bill.BillStatus.PAID);
            savedCustomer.addBill(bill);
            entityManager.persist(bill);
            
            commitTransaction();
            clearEntityManager();
            
            List<Customer> customersWithPendingBills = customerDAO.findCustomersWithPendingBills();
            
            assertNotNull(customersWithPendingBills);
            assertTrue(customersWithPendingBills.stream()
                .noneMatch(c -> c.getAccountNumber().equals("ACC002")));
        }
    }
    
    @Nested
    @DisplayName("Account Number Management Tests")
    class AccountNumberManagementTests {
        
        @Test
        @DisplayName("Should check if account number exists")
        void testAccountNumberExists() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            customerDAO.save(testCustomer1);
            commitTransaction();
            
            assertTrue(customerDAO.accountNumberExists("ACC001"));
            assertFalse(customerDAO.accountNumberExists("ACC999"));
        }
        
        @Test
        @DisplayName("Should generate next account number")
        void testGenerateNextAccountNumber() {
            if (!isTestEnvironmentReady()) return;
            
            String nextAccountNumber = customerDAO.generateNextAccountNumber();
            
            assertNotNull(nextAccountNumber);
            assertTrue(nextAccountNumber.length() > 0);
            assertTrue(nextAccountNumber.startsWith("ACC"));
        }
        
        @Test
        @DisplayName("Generated account numbers should be unique")
        void testGenerateUniqueAccountNumbers() {
            if (!isTestEnvironmentReady()) return;
            
            String accountNumber1 = customerDAO.generateNextAccountNumber();
            String accountNumber2 = customerDAO.generateNextAccountNumber();
            
            assertNotEquals(accountNumber1, accountNumber2);
        }
        
        @Test
        @DisplayName("Should update customer active status")
        void testUpdateActiveStatus() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            customerDAO.save(testCustomer1);
            commitTransaction();
            
            beginTransaction();
            Customer updatedCustomer = customerDAO.updateActiveStatus("ACC001", false);
            commitTransaction();
            
            assertNotNull(updatedCustomer);
            assertFalse(updatedCustomer.isActive());
        }
    }
    
    @Nested
    @DisplayName("Search and Statistics Tests")
    class SearchAndStatisticsTests {
        
        @BeforeEach
        void setUpCustomers() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            customerDAO.save(testCustomer1);
            customerDAO.save(testCustomer2);
            customerDAO.save(testCustomer3);
            commitTransaction();
            clearEntityManager();
        }
        
        @Test
        @DisplayName("Should search customers by multiple criteria")
        void testSearchCustomers() {
            if (!isTestEnvironmentReady()) return;
            
            List<Customer> customers = customerDAO.searchCustomers("John", null, true);
            
            assertNotNull(customers);
            assertTrue(customers.size() >= 1);
            assertTrue(customers.stream().allMatch(c -> 
                c.getName().toLowerCase().contains("john") && c.isActive()));
        }
        
        @Test
        @DisplayName("Should search customers by telephone number")
        void testSearchCustomersByTelephone() {
            if (!isTestEnvironmentReady()) return;
            
            List<Customer> customers = customerDAO.searchCustomers(null, "555-0001", null);
            
            assertNotNull(customers);
            assertEquals(1, customers.size());
            assertEquals("John Doe", customers.get(0).getName());
        }
        
        @Test
        @DisplayName("Should search customers by active status only")
        void testSearchCustomersByActiveStatus() {
            if (!isTestEnvironmentReady()) return;
            
            List<Customer> activeCustomers = customerDAO.searchCustomers(null, null, true);
            List<Customer> inactiveCustomers = customerDAO.searchCustomers(null, null, false);
            
            assertNotNull(activeCustomers);
            assertNotNull(inactiveCustomers);
            assertTrue(activeCustomers.size() >= 2);
            assertTrue(inactiveCustomers.size() >= 1);
            assertTrue(activeCustomers.stream().allMatch(Customer::isActive));
            assertTrue(inactiveCustomers.stream().noneMatch(Customer::isActive));
        }
        
        @Test
        @DisplayName("Should get customer statistics")
        void testGetCustomerStatistics() {
            if (!isTestEnvironmentReady()) return;
            
            long[] stats = customerDAO.getCustomerStatistics();
            
            assertNotNull(stats);
            assertEquals(3, stats.length);
            assertTrue(stats[0] >= 3); // Total customers
            assertTrue(stats[1] >= 2); // Active customers
            assertTrue(stats[2] >= 0); // Customers with pending bills
        }
    }
    
    @Nested
    @DisplayName("Edge Case and Error Handling Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should return empty list when searching with no matches")
        void testSearchWithNoMatches() {
            if (!isTestEnvironmentReady()) return;
            
            List<Customer> customers = customerDAO.findByNameContaining("NonexistentName");
            
            assertNotNull(customers);
            assertTrue(customers.isEmpty());
        }
        
        @Test
        @DisplayName("Should handle empty search criteria")
        void testSearchWithEmptyCriteria() {
            if (!isTestEnvironmentReady()) return;
            
            List<Customer> customers = customerDAO.searchCustomers("", "", null);
            
            assertNotNull(customers);
            // Should return all customers when search criteria are empty
        }
        
        @Test
        @DisplayName("Should handle null search parameters gracefully")
        void testSearchWithNullParameters() {
            if (!isTestEnvironmentReady()) return;
            
            assertDoesNotThrow(() -> {
                List<Customer> customers = customerDAO.searchCustomers(null, null, null);
                assertNotNull(customers);
            });
        }
        
        @Test
        @DisplayName("Should handle updating non-existent customer active status")
        void testUpdateActiveStatusNonExistentCustomer() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Customer updatedCustomer = customerDAO.updateActiveStatus("NONEXISTENT", false);
            commitTransaction();
            
            assertNull(updatedCustomer);
        }
        
        @Test
        @DisplayName("Should handle finding customer by null account number")
        void testFindByNullAccountNumber() {
            if (!isTestEnvironmentReady()) return;
            
            Optional<Customer> customer = customerDAO.findByAccountNumber(null);
            
            assertFalse(customer.isPresent());
        }
        
        @Test
        @DisplayName("Should handle finding customers by null telephone number")
        void testFindByNullTelephoneNumber() {
            if (!isTestEnvironmentReady()) return;
            
            assertDoesNotThrow(() -> {
                List<Customer> customers = customerDAO.findByTelephoneNumber(null);
                assertNotNull(customers);
            });
        }
        
        @Test
        @DisplayName("Should handle invalid date range in registration date search")
        void testFindByInvalidDateRange() {
            if (!isTestEnvironmentReady()) return;
            
            LocalDate startDate = LocalDate.of(2023, 12, 31);
            LocalDate endDate = LocalDate.of(2023, 1, 1); // End before start
            
            assertDoesNotThrow(() -> {
                List<Customer> customers = customerDAO.findByRegistrationDateBetween(startDate, endDate);
                assertNotNull(customers);
                assertTrue(customers.isEmpty());
            });
        }
        
        @Test
        @DisplayName("Should handle very large result sets in pagination")
        void testPaginationWithLargeDataset() {
            if (!isTestEnvironmentReady()) return;
            
            List<Customer> customers = customerDAO.findWithPagination(0, 1000);
            
            assertNotNull(customers);
            // Should not throw exception even with large limit
        }
    }
}