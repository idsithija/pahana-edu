package com.pahanaedu.model;

import com.pahanaedu.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bill Entity Tests")
class BillTest extends BaseTestCase {
    
    private Bill bill;
    private Customer customer;
    private Item item1;
    private Item item2;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        customer = new Customer("ACC001", "John Doe");
        item1 = new Item("Java Book", new BigDecimal("29.99"));
        item1.setStockQuantity(10);
        item2 = new Item("Python Book", new BigDecimal("39.99"));
        item2.setStockQuantity(5);
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create bill with default constructor")
        void testDefaultConstructor() {
            bill = new Bill();
            
            assertNotNull(bill);
            assertNull(bill.getBillId());
            assertNull(bill.getCustomer());
            assertEquals(LocalDate.now(), bill.getBillDate());
            assertEquals(BigDecimal.ZERO, bill.getTotalAmount());
            assertEquals(Bill.BillStatus.PENDING, bill.getStatus());
            assertNotNull(bill.getBillItems());
            assertTrue(bill.getBillItems().isEmpty());
        }
        
        @Test
        @DisplayName("Should create bill with customer")
        void testConstructorWithCustomer() {
            bill = new Bill(customer);
            
            assertEquals(customer, bill.getCustomer());
            assertEquals(LocalDate.now(), bill.getBillDate());
            assertEquals(BigDecimal.ZERO, bill.getTotalAmount());
            assertEquals(Bill.BillStatus.PENDING, bill.getStatus());
        }
        
        @Test
        @DisplayName("Should create bill with customer and total amount")
        void testConstructorWithCustomerAndAmount() {
            BigDecimal totalAmount = new BigDecimal("100.00");
            bill = new Bill(customer, totalAmount);
            
            assertEquals(customer, bill.getCustomer());
            assertEquals(totalAmount, bill.getTotalAmount());
            assertEquals(Bill.BillStatus.PENDING, bill.getStatus());
        }
    }
    
    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {
        
        @BeforeEach
        void setUp() {
            bill = new Bill();
        }
        
        @Test
        @DisplayName("Should get and set bill ID")
        void testBillId() {
            Long billId = 1L;
            bill.setBillId(billId);
            assertEquals(billId, bill.getBillId());
        }
        
        @Test
        @DisplayName("Should get and set customer")
        void testCustomer() {
            bill.setCustomer(customer);
            assertEquals(customer, bill.getCustomer());
        }
        
        @Test
        @DisplayName("Should get and set bill date")
        void testBillDate() {
            LocalDate testDate = LocalDate.of(2023, 6, 15);
            bill.setBillDate(testDate);
            assertEquals(testDate, bill.getBillDate());
        }
        
        @Test
        @DisplayName("Should get and set total amount")
        void testTotalAmount() {
            BigDecimal amount = new BigDecimal("150.75");
            bill.setTotalAmount(amount);
            assertEquals(amount, bill.getTotalAmount());
        }
        
        @Test
        @DisplayName("Should get and set status")
        void testStatus() {
            bill.setStatus(Bill.BillStatus.PAID);
            assertEquals(Bill.BillStatus.PAID, bill.getStatus());
        }
        
        @Test
        @DisplayName("Should get and set bill items")
        void testBillItems() {
            List<BillItem> billItems = new ArrayList<>();
            bill.setBillItems(billItems);
            assertEquals(billItems, bill.getBillItems());
        }
    }
    
    @Nested
    @DisplayName("Bill Item Management Tests")
    class BillItemManagementTests {
        
        @BeforeEach
        void setUp() {
            bill = new Bill(customer);
        }
        
        @Test
        @DisplayName("Should add bill item correctly")
        void testAddBillItem() {
            BillItem billItem = new BillItem(bill, item1, 2, item1.getUnitPrice());
            
            bill.addBillItem(billItem);
            
            assertTrue(bill.getBillItems().contains(billItem));
            assertEquals(bill, billItem.getBill());
            assertEquals(new BigDecimal("59.98"), bill.getTotalAmount());
        }
        
        @Test
        @DisplayName("Should remove bill item correctly")
        void testRemoveBillItem() {
            BillItem billItem = new BillItem(bill, item1, 2, item1.getUnitPrice());
            bill.addBillItem(billItem);
            
            bill.removeBillItem(billItem);
            
            assertFalse(bill.getBillItems().contains(billItem));
            assertNull(billItem.getBill());
            assertEquals(BigDecimal.ZERO, bill.getTotalAmount());
        }
        
        @Test
        @DisplayName("Should add item with quantity correctly")
        void testAddItem() {
            bill.addItem(item1, 3);
            
            assertEquals(1, bill.getBillItems().size());
            BillItem billItem = bill.getBillItems().get(0);
            assertEquals(item1, billItem.getItem());
            assertEquals(Integer.valueOf(3), billItem.getQuantity());
            assertEquals(new BigDecimal("89.97"), bill.getTotalAmount());
        }
        
        @Test
        @DisplayName("Should update existing item quantity when adding same item")
        void testAddExistingItem() {
            bill.addItem(item1, 2);
            bill.addItem(item1, 3);
            
            assertEquals(1, bill.getBillItems().size());
            BillItem billItem = bill.getBillItems().get(0);
            assertEquals(Integer.valueOf(5), billItem.getQuantity());
            assertEquals(new BigDecimal("149.95"), bill.getTotalAmount());
        }
        
        @Test
        @DisplayName("Should throw exception when adding item with insufficient stock")
        void testAddItemInsufficientStock() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                bill.addItem(item1, 15);
            });
            
            assertTrue(exception.getMessage().contains("Insufficient stock"));
            assertTrue(exception.getMessage().contains("Java Book"));
        }
        
        @Test
        @DisplayName("Should recalculate total after item changes")
        void testRecalculateTotal() {
            BillItem billItem1 = new BillItem(bill, item1, 2, item1.getUnitPrice());
            BillItem billItem2 = new BillItem(bill, item2, 1, item2.getUnitPrice());
            
            bill.getBillItems().add(billItem1);
            bill.getBillItems().add(billItem2);
            bill.recalculateTotal();
            
            assertEquals(new BigDecimal("99.97"), bill.getTotalAmount());
        }
    }
    
    @Nested
    @DisplayName("Status Management Tests")
    class StatusManagementTests {
        
        @BeforeEach
        void setUp() {
            bill = new Bill(customer);
        }
        
        @Test
        @DisplayName("Should check if bill is pending")
        void testIsPending() {
            assertTrue(bill.isPending());
            assertFalse(bill.isPaid());
            assertFalse(bill.isCancelled());
        }
        
        @Test
        @DisplayName("Should check if bill is paid")
        void testIsPaid() {
            bill.setStatus(Bill.BillStatus.PAID);
            
            assertFalse(bill.isPending());
            assertTrue(bill.isPaid());
            assertFalse(bill.isCancelled());
        }
        
        @Test
        @DisplayName("Should check if bill is cancelled")
        void testIsCancelled() {
            bill.setStatus(Bill.BillStatus.CANCELLED);
            
            assertFalse(bill.isPending());
            assertFalse(bill.isPaid());
            assertTrue(bill.isCancelled());
        }
        
        @Test
        @DisplayName("Should mark bill as paid")
        void testMarkAsPaid() {
            bill.markAsPaid();
            
            assertEquals(Bill.BillStatus.PAID, bill.getStatus());
            assertTrue(bill.isPaid());
        }
        
        @Test
        @DisplayName("Should mark bill as cancelled")
        void testMarkAsCancelled() {
            bill.markAsCancelled();
            
            assertEquals(Bill.BillStatus.CANCELLED, bill.getStatus());
            assertTrue(bill.isCancelled());
        }
    }
    
    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {
        
        @BeforeEach
        void setUp() {
            bill = new Bill(customer);
        }
        
        @Test
        @DisplayName("Should calculate total item count correctly")
        void testGetTotalItemCount() {
            bill.addItem(item1, 3);
            bill.addItem(item2, 2);
            
            assertEquals(5, bill.getTotalItemCount());
        }
        
        @Test
        @DisplayName("Should return zero item count for empty bill")
        void testGetTotalItemCountEmpty() {
            assertEquals(0, bill.getTotalItemCount());
        }
        
        @Test
        @DisplayName("Should find existing bill item by item")
        void testFindBillItemByItem() {
            bill.addItem(item1, 2);
            
            // Use reflection or add the item again to test the internal method
            bill.addItem(item1, 1);
            
            assertEquals(1, bill.getBillItems().size());
            assertEquals(Integer.valueOf(3), bill.getBillItems().get(0).getQuantity());
        }
    }
    
    @Nested
    @DisplayName("Equality and Hash Tests")
    class EqualityTests {
        
        @Test
        @DisplayName("Should be equal when bill IDs match")
        void testEquals() {
            Bill bill1 = new Bill();
            bill1.setBillId(1L);
            Bill bill2 = new Bill();
            bill2.setBillId(1L);
            
            assertEquals(bill1, bill2);
            assertEquals(bill1.hashCode(), bill2.hashCode());
        }
        
        @Test
        @DisplayName("Should not be equal when bill IDs differ")
        void testNotEquals() {
            Bill bill1 = new Bill();
            bill1.setBillId(1L);
            Bill bill2 = new Bill();
            bill2.setBillId(2L);
            
            assertNotEquals(bill1, bill2);
        }
        
        @Test
        @DisplayName("Should handle null in equals comparison")
        void testEqualsWithNull() {
            Bill bill1 = new Bill();
            bill1.setBillId(1L);
            
            assertNotEquals(bill1, null);
            assertEquals(bill1, bill1);
        }
        
        @Test
        @DisplayName("Should handle different classes in equals comparison")
        void testEqualsWithDifferentClass() {
            Bill bill1 = new Bill();
            bill1.setBillId(1L);
            String notBill = "not a bill";
            
            assertNotEquals(bill1, notBill);
        }
        
        @Test
        @DisplayName("Should handle null bill IDs in equals")
        void testEqualsWithNullBillIds() {
            Bill bill1 = new Bill();
            Bill bill2 = new Bill();
            
            assertEquals(bill1, bill2);
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("Should return meaningful string representation")
        void testToString() {
            bill = new Bill(customer, new BigDecimal("100.00"));
            bill.setBillId(1L);
            bill.addItem(item1, 2);
            
            String result = bill.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("1"));
            assertTrue(result.contains("ACC001"));
            assertTrue(result.contains("100.00"));
            assertTrue(result.contains("PENDING"));
            assertTrue(result.contains("itemCount=1"));
            assertTrue(result.contains("Bill{"));
        }
        
        @Test
        @DisplayName("Should handle null values in toString")
        void testToStringWithNulls() {
            bill = new Bill();
            
            String result = bill.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("Bill{"));
            assertTrue(result.contains("null"));
        }
    }
    
    @Nested
    @DisplayName("Bill Status Enum Tests")
    class BillStatusEnumTests {
        
        @Test
        @DisplayName("Should have correct enum values")
        void testBillStatusEnumValues() {
            Bill.BillStatus[] statuses = Bill.BillStatus.values();
            
            assertEquals(3, statuses.length);
            assertTrue(java.util.Arrays.asList(statuses).contains(Bill.BillStatus.PENDING));
            assertTrue(java.util.Arrays.asList(statuses).contains(Bill.BillStatus.PAID));
            assertTrue(java.util.Arrays.asList(statuses).contains(Bill.BillStatus.CANCELLED));
        }
        
        @Test
        @DisplayName("Should convert enum to string correctly")
        void testBillStatusToString() {
            assertEquals("PENDING", Bill.BillStatus.PENDING.toString());
            assertEquals("PAID", Bill.BillStatus.PAID.toString());
            assertEquals("CANCELLED", Bill.BillStatus.CANCELLED.toString());
        }
        
        @Test
        @DisplayName("Should convert string to enum correctly")
        void testStringToBillStatus() {
            assertEquals(Bill.BillStatus.PENDING, Bill.BillStatus.valueOf("PENDING"));
            assertEquals(Bill.BillStatus.PAID, Bill.BillStatus.valueOf("PAID"));
            assertEquals(Bill.BillStatus.CANCELLED, Bill.BillStatus.valueOf("CANCELLED"));
        }
    }
    
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle adding multiple different items")
        void testAddMultipleItems() {
            bill = new Bill(customer);
            
            bill.addItem(item1, 2);
            bill.addItem(item2, 1);
            
            assertEquals(2, bill.getBillItems().size());
            assertEquals(new BigDecimal("99.97"), bill.getTotalAmount());
        }
        
        @Test
        @DisplayName("Should handle zero quantity additions")
        void testAddZeroQuantity() {
            bill = new Bill(customer);
            
            // This should not throw an exception but also shouldn't add the item
            assertDoesNotThrow(() -> bill.addItem(item1, 0));
        }
        
        @Test
        @DisplayName("Should handle bill with no customer in toString")
        void testToStringWithNoCustomer() {
            bill = new Bill();
            
            String result = bill.toString();
            
            assertTrue(result.contains("customer=null"));
        }
        
        @Test
        @DisplayName("Should maintain total amount consistency")
        void testTotalAmountConsistency() {
            bill = new Bill(customer);
            
            bill.addItem(item1, 2);
            BigDecimal expectedTotal = item1.getUnitPrice().multiply(BigDecimal.valueOf(2));
            assertEquals(expectedTotal, bill.getTotalAmount());
            
            bill.addItem(item2, 1);
            expectedTotal = expectedTotal.add(item2.getUnitPrice());
            assertEquals(expectedTotal, bill.getTotalAmount());
        }
    }
}