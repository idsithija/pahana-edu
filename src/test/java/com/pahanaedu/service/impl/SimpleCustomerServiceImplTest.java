package com.pahanaedu.service.impl;

import com.pahanaedu.BaseTestCase;
import com.pahanaedu.dao.CustomerDAO;
import com.pahanaedu.model.Customer;
import com.pahanaedu.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Simple Customer Service Implementation Tests")
class SimpleCustomerServiceImplTest extends BaseTestCase {
    
    @Mock
    private CustomerDAO customerDAO;
    
    private CustomerService customerService;
    private Customer testCustomer1;
    private Customer testCustomer2;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        MockitoAnnotations.openMocks(this);
        
        // Create service with mocked DAO
        customerService = new SimpleCustomerServiceImpl() {
            private final CustomerDAO mockedDAO = customerDAO;
        };
        
        // Set up test data
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
    }
    
    @Nested
    @DisplayName("Customer Registration Tests")
    class CustomerRegistrationTests {
        
        @Test
        @DisplayName("Should register customer with auto-generated account number")
        void testRegisterCustomer() {
            // Arrange
            when(customerDAO.generateNextAccountNumber()).thenReturn("ACC001");
            when(customerDAO.save(any(Customer.class))).thenReturn(testCustomer1);
            
            // Act
            Customer result = customerService.registerCustomer("John Doe", "123 Main St", "555-0001");
            
            // Assert
            assertNotNull(result);
            assertEquals("John Doe", result.getName());
            assertEquals("123 Main St", result.getAddress());
            assertEquals("555-0001", result.getTelephoneNumber());
            
            verify(customerDAO).generateNextAccountNumber();
            verify(customerDAO).save(any(Customer.class));
        }
        
        @Test
        @DisplayName("Should register customer with custom account number")
        void testRegisterCustomerWithAccount() {
            // Arrange
            when(customerDAO.accountNumberExists("CUSTOM001")).thenReturn(false);
            when(customerDAO.save(any(Customer.class))).thenReturn(testCustomer1);
            
            // Act
            Customer result = customerService.registerCustomerWithAccount("CUSTOM001", 
                "John Doe", "123 Main St", "555-0001");
            
            // Assert
            assertNotNull(result);
            assertEquals("John Doe", result.getName());
            
            verify(customerDAO).accountNumberExists("CUSTOM001");
            verify(customerDAO).save(any(Customer.class));
            verify(customerDAO, never()).generateNextAccountNumber();
        }
        
        @Test
        @DisplayName("Should throw exception for duplicate account number")
        void testRegisterCustomerDuplicateAccountNumber() {
            // Arrange
            when(customerDAO.accountNumberExists("EXISTING")).thenReturn(true);
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                customerService.registerCustomerWithAccount("EXISTING", 
                    "John Doe", "123 Main St", "555-0001");
            });
            
            assertTrue(exception.getMessage().contains("Account number already exists"));
            verify(customerDAO, never()).save(any(Customer.class));
        }
        
        @Test
        @DisplayName("Should throw exception for invalid customer data")
        void testRegisterCustomerInvalidData() {
            // Act & Assert - Empty name
            IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
                customerService.registerCustomer("", "123 Main St", "555-0001");
            });
            assertTrue(exception1.getMessage().contains("Invalid customer data"));
            
            // Act & Assert - Null name
            IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
                customerService.registerCustomer(null, "123 Main St", "555-0001");
            });
            assertTrue(exception2.getMessage().contains("Invalid customer data"));
        }
        
        @Test
        @DisplayName("Should auto-generate account number when provided is empty")
        void testRegisterCustomerEmptyAccountNumber() {
            // Arrange
            when(customerDAO.generateNextAccountNumber()).thenReturn("ACC001");
            when(customerDAO.save(any(Customer.class))).thenReturn(testCustomer1);
            
            // Act
            Customer result = customerService.registerCustomerWithAccount("", 
                "John Doe", "123 Main St", "555-0001");
            
            // Assert
            assertNotNull(result);
            verify(customerDAO).generateNextAccountNumber();
            verify(customerDAO, never()).accountNumberExists(anyString());
        }
    }
    
    @Nested
    @DisplayName("Customer Update Tests")
    class CustomerUpdateTests {
        
        @Test
        @DisplayName("Should update customer successfully")
        void testUpdateCustomer() {
            // Arrange
            when(customerDAO.update(testCustomer1)).thenReturn(testCustomer1);
            
            // Act
            Customer result = customerService.updateCustomer(testCustomer1);
            
            // Assert
            assertNotNull(result);
            assertEquals(testCustomer1, result);
            verify(customerDAO).update(testCustomer1);
        }
        
        @Test
        @DisplayName("Should throw exception for null customer update")
        void testUpdateNullCustomer() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                customerService.updateCustomer(null);
            });
            
            assertTrue(exception.getMessage().contains("Customer cannot be null"));
            verify(customerDAO, never()).update(any(Customer.class));
        }
        
        @Test
        @DisplayName("Should throw exception for invalid customer data on update")
        void testUpdateCustomerInvalidData() {
            // Arrange
            Customer invalidCustomer = new Customer("ACC001", ""); // Empty name
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                customerService.updateCustomer(invalidCustomer);
            });
            
            assertTrue(exception.getMessage().contains("Invalid customer data"));
            verify(customerDAO, never()).update(any(Customer.class));
        }
    }
    
    @Nested
    @DisplayName("Customer Query Tests")
    class CustomerQueryTests {
        
        @Test
        @DisplayName("Should find customer by account number")
        void testFindByAccountNumber() {
            // Arrange
            when(customerDAO.findByAccountNumber("ACC001")).thenReturn(Optional.of(testCustomer1));
            
            // Act
            Optional<Customer> result = customerService.findByAccountNumber("ACC001");
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(testCustomer1, result.get());
            verify(customerDAO).findByAccountNumber("ACC001");
        }
        
        @Test
        @DisplayName("Should return empty optional for non-existent account number")
        void testFindByAccountNumberNotFound() {
            // Arrange
            when(customerDAO.findByAccountNumber("NONEXISTENT")).thenReturn(Optional.empty());
            
            // Act
            Optional<Customer> result = customerService.findByAccountNumber("NONEXISTENT");
            
            // Assert
            assertFalse(result.isPresent());
            verify(customerDAO).findByAccountNumber("NONEXISTENT");
        }
        
        @Test
        @DisplayName("Should return empty optional for null/empty account number")
        void testFindByAccountNumberNullOrEmpty() {
            // Act
            Optional<Customer> result1 = customerService.findByAccountNumber(null);
            Optional<Customer> result2 = customerService.findByAccountNumber("");
            Optional<Customer> result3 = customerService.findByAccountNumber("   ");
            
            // Assert
            assertFalse(result1.isPresent());
            assertFalse(result2.isPresent());
            assertFalse(result3.isPresent());
            
            verify(customerDAO, never()).findByAccountNumber(anyString());
        }
        
        @Test
        @DisplayName("Should search customers by name")
        void testSearchByName() {
            // Arrange
            List<Customer> customers = Arrays.asList(testCustomer1, testCustomer2);
            when(customerDAO.findByNameContaining("John")).thenReturn(Collections.singletonList(testCustomer1));
            
            // Act
            List<Customer> result = customerService.searchByName("John");
            
            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testCustomer1, result.get(0));
            verify(customerDAO).findByNameContaining("John");
        }
        
        @Test
        @DisplayName("Should return all customers when search name is empty")
        void testSearchByNameEmpty() {
            // Arrange
            List<Customer> allCustomers = Arrays.asList(testCustomer1, testCustomer2);
            when(customerDAO.findAll()).thenReturn(allCustomers);
            
            // Act
            List<Customer> result = customerService.searchByName("");
            
            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(customerDAO, never()).findByNameContaining(anyString());
        }
        
        @Test
        @DisplayName("Should find customers by telephone number")
        void testFindByTelephoneNumber() {
            // Arrange
            when(customerDAO.findByTelephoneNumber("555-0001"))
                .thenReturn(Collections.singletonList(testCustomer1));
            
            // Act
            List<Customer> result = customerService.findByTelephoneNumber("555-0001");
            
            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testCustomer1, result.get(0));
            verify(customerDAO).findByTelephoneNumber("555-0001");
        }
        
        @Test
        @DisplayName("Should throw exception for empty telephone number")
        void testFindByTelephoneNumberEmpty() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                customerService.findByTelephoneNumber("");
            });
            
            assertTrue(exception.getMessage().contains("Telephone number cannot be empty"));
            verify(customerDAO, never()).findByTelephoneNumber(anyString());
        }
        
        @Test
        @DisplayName("Should get active customers")
        void testGetActiveCustomers() {
            // Arrange
            List<Customer> activeCustomers = Arrays.asList(testCustomer1, testCustomer2);
            when(customerDAO.findActiveCustomers()).thenReturn(activeCustomers);
            
            // Act
            List<Customer> result = customerService.getActiveCustomers();
            
            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(customerDAO).findActiveCustomers();
        }
        
        @Test
        @DisplayName("Should get all customers")
        void testGetAllCustomers() {
            // Arrange
            List<Customer> allCustomers = Arrays.asList(testCustomer1, testCustomer2);
            when(customerDAO.findAll()).thenReturn(allCustomers);
            
            // Act
            List<Customer> result = customerService.getAllCustomers();
            
            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(customerDAO).findAll();
        }
    }
    
    @Nested
    @DisplayName("Customer Status Management Tests")
    class CustomerStatusManagementTests {
        
        @Test
        @DisplayName("Should update customer active status")
        void testUpdateActiveStatus() {
            // Arrange
            when(customerDAO.updateActiveStatus("ACC001", false)).thenReturn(testCustomer1);
            
            // Act
            Customer result = customerService.updateActiveStatus("ACC001", false);
            
            // Assert
            assertNotNull(result);
            assertEquals(testCustomer1, result);
            verify(customerDAO).updateActiveStatus("ACC001", false);
        }
        
        @Test
        @DisplayName("Should delete customer (soft delete)")
        void testDeleteCustomer() {
            // Arrange
            when(customerDAO.updateActiveStatus("ACC001", false)).thenReturn(testCustomer1);
            
            // Act
            assertDoesNotThrow(() -> customerService.deleteCustomer("ACC001"));
            
            // Assert
            verify(customerDAO).updateActiveStatus("ACC001", false);
        }
    }
    
    @Nested
    @DisplayName("Advanced Query Tests")
    class AdvancedQueryTests {
        
        @Test
        @DisplayName("Should get customers registered between dates")
        void testGetCustomersRegisteredBetween() {
            // Arrange
            LocalDate startDate = LocalDate.of(2023, 1, 1);
            LocalDate endDate = LocalDate.of(2023, 2, 28);
            when(customerDAO.findByRegistrationDateBetween(startDate, endDate))
                .thenReturn(Collections.singletonList(testCustomer1));
            
            // Act
            List<Customer> result = customerService.getCustomersRegisteredBetween(startDate, endDate);
            
            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(customerDAO).findByRegistrationDateBetween(startDate, endDate);
        }
        
        @Test
        @DisplayName("Should get customers with pending bills")
        void testGetCustomersWithPendingBills() {
            // Arrange
            when(customerDAO.findCustomersWithPendingBills())
                .thenReturn(Collections.singletonList(testCustomer1));
            
            // Act
            List<Customer> result = customerService.getCustomersWithPendingBills();
            
            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(customerDAO).findCustomersWithPendingBills();
        }
        
        @Test
        @DisplayName("Should search customers by multiple criteria")
        void testSearchCustomers() {
            // Arrange
            when(customerDAO.searchCustomers("John", "555-0001", true))
                .thenReturn(Collections.singletonList(testCustomer1));
            
            // Act
            List<Customer> result = customerService.searchCustomers("John", "555-0001", true);
            
            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(customerDAO).searchCustomers("John", "555-0001", true);
        }
        
        @Test
        @DisplayName("Should get customers with pagination")
        void testGetCustomersWithPagination() {
            // Arrange
            when(customerDAO.findWithPagination(0, 10))
                .thenReturn(Arrays.asList(testCustomer1, testCustomer2));
            
            // Act
            List<Customer> result = customerService.getCustomersWithPagination(0, 10);
            
            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(customerDAO).findWithPagination(0, 10);
        }
        
        @Test
        @DisplayName("Should get total customer count")
        void testGetTotalCustomerCount() {
            // Arrange
            when(customerDAO.count()).thenReturn(100L);
            
            // Act
            long result = customerService.getTotalCustomerCount();
            
            // Assert
            assertEquals(100L, result);
            verify(customerDAO).count();
        }
    }
    
    @Nested
    @DisplayName("Statistics and Validation Tests")
    class StatisticsAndValidationTests {
        
        @Test
        @DisplayName("Should get customer statistics")
        void testGetCustomerStatistics() {
            // Arrange
            long[] stats = {100L, 80L, 5L};
            when(customerDAO.getCustomerStatistics()).thenReturn(stats);
            
            // Act
            long[] result = customerService.getCustomerStatistics();
            
            // Assert
            assertNotNull(result);
            assertEquals(3, result.length);
            assertEquals(100L, result[0]);
            assertEquals(80L, result[1]);
            assertEquals(5L, result[2]);
            verify(customerDAO).getCustomerStatistics();
        }
        
        @Test
        @DisplayName("Should validate customer data correctly")
        void testValidateCustomerData() {
            // Act & Assert
            assertTrue(customerService.validateCustomerData("John Doe", "555-0001"));
            assertFalse(customerService.validateCustomerData("", "555-0001"));
            assertFalse(customerService.validateCustomerData(null, "555-0001"));
            assertFalse(customerService.validateCustomerData("John Doe", ""));
            assertFalse(customerService.validateCustomerData("John Doe", null));
        }
        
        @Test
        @DisplayName("Should check if account number exists")
        void testAccountNumberExists() {
            // Arrange
            when(customerDAO.accountNumberExists("ACC001")).thenReturn(true);
            when(customerDAO.accountNumberExists("NONEXISTENT")).thenReturn(false);
            
            // Act & Assert
            assertTrue(customerService.accountNumberExists("ACC001"));
            assertFalse(customerService.accountNumberExists("NONEXISTENT"));
            
            verify(customerDAO).accountNumberExists("ACC001");
            verify(customerDAO).accountNumberExists("NONEXISTENT");
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle DAO exceptions gracefully")
        void testDAOExceptionHandling() {
            // Arrange
            when(customerDAO.save(any(Customer.class))).thenThrow(new RuntimeException("Database error"));
            
            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                customerService.registerCustomer("John Doe", "123 Main St", "555-0001");
            });
        }
        
        @Test
        @DisplayName("Should handle null returns from DAO")
        void testNullDAOReturns() {
            // Arrange
            when(customerDAO.findByAccountNumber("ACC001")).thenReturn(Optional.empty());
            
            // Act
            Optional<Customer> result = customerService.findByAccountNumber("ACC001");
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should trim whitespace in inputs")
        void testInputTrimming() {
            // Arrange
            when(customerDAO.generateNextAccountNumber()).thenReturn("ACC001");
            when(customerDAO.save(any(Customer.class))).thenReturn(testCustomer1);
            
            // Act
            Customer result = customerService.registerCustomer("  John Doe  ", "  123 Main St  ", "  555-0001  ");
            
            // Assert
            verify(customerDAO).save(argThat(customer -> 
                customer.getName().equals("John Doe") &&
                customer.getAddress().equals("123 Main St") &&
                customer.getTelephoneNumber().equals("555-0001")
            ));
        }
    }
}