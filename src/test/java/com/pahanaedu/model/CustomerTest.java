package com.pahanaedu.model;

import com.pahanaedu.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Entity Tests")
class CustomerTest extends BaseTestCase {
    
    private Customer customer;
    private String accountNumber;
    private String name;
    private String address;
    private String telephone;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        accountNumber = "ACC001";
        name = "John Doe";
        address = "123 Main St, City";
        telephone = "555-1234";
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create customer with default constructor")
        void testDefaultConstructor() {
            customer = new Customer();
            
            assertNotNull(customer);
            assertNull(customer.getAccountNumber());
            assertNull(customer.getName());
            assertEquals(LocalDate.now(), customer.getRegistrationDate());
            assertTrue(customer.getActive());
            assertNotNull(customer.getBills());
            assertTrue(customer.getBills().isEmpty());
        }
        
        @Test
        @DisplayName("Should create customer with account number and name")
        void testConstructorWithAccountAndName() {
            customer = new Customer(accountNumber, name);
            
            assertEquals(accountNumber, customer.getAccountNumber());
            assertEquals(name, customer.getName());
            assertEquals(LocalDate.now(), customer.getRegistrationDate());
            assertTrue(customer.getActive());
            assertNull(customer.getAddress());
            assertNull(customer.getTelephoneNumber());
        }
        
        @Test
        @DisplayName("Should create customer with full details")
        void testConstructorWithFullDetails() {
            customer = new Customer(accountNumber, name, address, telephone);
            
            assertEquals(accountNumber, customer.getAccountNumber());
            assertEquals(name, customer.getName());
            assertEquals(address, customer.getAddress());
            assertEquals(telephone, customer.getTelephoneNumber());
            assertEquals(LocalDate.now(), customer.getRegistrationDate());
            assertTrue(customer.getActive());
        }
    }
    
    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {
        
        @BeforeEach
        void setUp() {
            customer = new Customer();
        }
        
        @Test
        @DisplayName("Should get and set account number")
        void testAccountNumber() {
            customer.setAccountNumber(accountNumber);
            assertEquals(accountNumber, customer.getAccountNumber());
        }
        
        @Test
        @DisplayName("Should get and set name")
        void testName() {
            customer.setName(name);
            assertEquals(name, customer.getName());
        }
        
        @Test
        @DisplayName("Should get and set address")
        void testAddress() {
            customer.setAddress(address);
            assertEquals(address, customer.getAddress());
        }
        
        @Test
        @DisplayName("Should get and set telephone number")
        void testTelephoneNumber() {
            customer.setTelephoneNumber(telephone);
            assertEquals(telephone, customer.getTelephoneNumber());
        }
        
        @Test
        @DisplayName("Should get and set registration date")
        void testRegistrationDate() {
            LocalDate testDate = LocalDate.of(2023, 1, 15);
            customer.setRegistrationDate(testDate);
            assertEquals(testDate, customer.getRegistrationDate());
        }
        
        @Test
        @DisplayName("Should get and set active status")
        void testActive() {
            customer.setActive(false);
            assertFalse(customer.getActive());
            assertFalse(customer.isActive());
            
            customer.setActive(true);
            assertTrue(customer.getActive());
            assertTrue(customer.isActive());
        }
        
        @Test
        @DisplayName("Should get and set bills list")
        void testBills() {
            List<Bill> bills = new ArrayList<>();
            customer.setBills(bills);
            assertEquals(bills, customer.getBills());
        }
    }
    
    @Nested
    @DisplayName("Business Method Tests")
    class BusinessMethodTests {
        
        @BeforeEach
        void setUp() {
            customer = new Customer(accountNumber, name);
        }
        
        @Test
        @DisplayName("Should return correct active status")
        void testIsActive() {
            assertTrue(customer.isActive());
            
            customer.setActive(false);
            assertFalse(customer.isActive());
            
            customer.setActive(null);
            assertFalse(customer.isActive());
        }
        
        @Test
        @DisplayName("Should add bill correctly")
        void testAddBill() {
            Bill bill = new Bill(customer);
            customer.addBill(bill);
            
            assertTrue(customer.getBills().contains(bill));
            assertEquals(customer, bill.getCustomer());
        }
        
        @Test
        @DisplayName("Should remove bill correctly")
        void testRemoveBill() {
            Bill bill = new Bill(customer);
            customer.addBill(bill);
            
            customer.removeBill(bill);
            
            assertFalse(customer.getBills().contains(bill));
            assertNull(bill.getCustomer());
        }
        
        @Test
        @DisplayName("Should calculate total bill amount correctly")
        void testGetTotalBillAmount() {
            Bill bill1 = new Bill(customer, new BigDecimal("100.00"));
            Bill bill2 = new Bill(customer, new BigDecimal("250.50"));
            
            customer.addBill(bill1);
            customer.addBill(bill2);
            
            assertEquals(350.50, customer.getTotalBillAmount(), 0.01);
        }
        
        @Test
        @DisplayName("Should count pending bills correctly")
        void testGetPendingBillsCount() {
            Bill bill1 = new Bill(customer);
            Bill bill2 = new Bill(customer);
            Bill bill3 = new Bill(customer);
            
            bill1.setStatus(Bill.BillStatus.PENDING);
            bill2.setStatus(Bill.BillStatus.PAID);
            bill3.setStatus(Bill.BillStatus.PENDING);
            
            customer.addBill(bill1);
            customer.addBill(bill2);
            customer.addBill(bill3);
            
            assertEquals(2, customer.getPendingBillsCount());
        }
    }
    
    @Nested
    @DisplayName("Date Conversion Tests")
    class DateConversionTests {
        
        @BeforeEach
        void setUp() {
            customer = new Customer();
        }
        
        @Test
        @DisplayName("Should convert LocalDate to Date for JSP compatibility")
        void testGetRegistrationDateAsDate() {
            LocalDate localDate = LocalDate.of(2023, 6, 15);
            customer.setRegistrationDate(localDate);
            
            Date dateResult = customer.getRegistrationDateAsDate();
            
            assertNotNull(dateResult);
        }
        
        @Test
        @DisplayName("Should return null Date when registration date is null")
        void testGetRegistrationDateAsDateWithNull() {
            customer.setRegistrationDate(null);
            
            Date dateResult = customer.getRegistrationDateAsDate();
            
            assertNull(dateResult);
        }
    }
    
    @Nested
    @DisplayName("Equality and Hash Tests")
    class EqualityTests {
        
        @Test
        @DisplayName("Should be equal when account numbers match")
        void testEquals() {
            Customer customer1 = new Customer(accountNumber, "Name 1");
            Customer customer2 = new Customer(accountNumber, "Name 2");
            
            assertEquals(customer1, customer2);
            assertEquals(customer1.hashCode(), customer2.hashCode());
        }
        
        @Test
        @DisplayName("Should not be equal when account numbers differ")
        void testNotEquals() {
            Customer customer1 = new Customer("ACC001", name);
            Customer customer2 = new Customer("ACC002", name);
            
            assertNotEquals(customer1, customer2);
        }
        
        @Test
        @DisplayName("Should handle null in equals comparison")
        void testEqualsWithNull() {
            Customer customer1 = new Customer(accountNumber, name);
            
            assertNotEquals(customer1, null);
            assertEquals(customer1, customer1);
        }
        
        @Test
        @DisplayName("Should handle different classes in equals comparison")
        void testEqualsWithDifferentClass() {
            Customer customer1 = new Customer(accountNumber, name);
            String notCustomer = "not a customer";
            
            assertNotEquals(customer1, notCustomer);
        }
        
        @Test
        @DisplayName("Should handle null account numbers in equals")
        void testEqualsWithNullAccountNumbers() {
            Customer customer1 = new Customer();
            Customer customer2 = new Customer();
            
            assertEquals(customer1, customer2);
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("Should return meaningful string representation")
        void testToString() {
            customer = new Customer(accountNumber, name, address, telephone);
            
            String result = customer.toString();
            
            assertNotNull(result);
            assertTrue(result.contains(accountNumber));
            assertTrue(result.contains(name));
            assertTrue(result.contains(address));
            assertTrue(result.contains(telephone));
            assertTrue(result.contains("Customer{"));
        }
        
        @Test
        @DisplayName("Should handle null values in toString")
        void testToStringWithNulls() {
            customer = new Customer();
            
            String result = customer.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("Customer{"));
        }
    }
    
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle empty bills list operations")
        void testEmptyBillsOperations() {
            customer = new Customer(accountNumber, name);
            
            assertEquals(0.0, customer.getTotalBillAmount());
            assertEquals(0, customer.getPendingBillsCount());
        }
        
        @Test
        @DisplayName("Should handle null bill operations safely")
        void testNullBillOperations() {
            customer = new Customer(accountNumber, name);
            
            assertDoesNotThrow(() -> customer.removeBill(null));
        }
        
        @Test
        @DisplayName("Should maintain bill relationship consistency")
        void testBillRelationshipConsistency() {
            customer = new Customer(accountNumber, name);
            Bill bill = new Bill();
            
            customer.addBill(bill);
            assertEquals(customer, bill.getCustomer());
            
            customer.removeBill(bill);
            assertNull(bill.getCustomer());
        }
    }
}