package com.pahanaedu.model;

import com.pahanaedu.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BillItem Entity Tests")
class BillItemTest extends BaseTestCase {
    
    private BillItem billItem;
    private Bill bill;
    private Customer customer;
    private Item item;
    private Integer quantity;
    private BigDecimal unitPrice;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        customer = new Customer("ACC001", "John Doe");
        bill = new Bill(customer);
        item = new Item("Java Programming Book", new BigDecimal("49.99"));
        item.setStockQuantity(10);
        quantity = 3;
        unitPrice = new BigDecimal("49.99");
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create bill item with default constructor")
        void testDefaultConstructor() {
            billItem = new BillItem();
            
            assertNotNull(billItem);
            assertNull(billItem.getBillItemId());
            assertNull(billItem.getBill());
            assertNull(billItem.getItem());
            assertNull(billItem.getQuantity());
            assertNull(billItem.getUnitPrice());
            assertNull(billItem.getTotalPrice());
        }
        
        @Test
        @DisplayName("Should create bill item with full constructor")
        void testFullConstructor() {
            billItem = new BillItem(bill, item, quantity, unitPrice);
            
            assertEquals(bill, billItem.getBill());
            assertEquals(item, billItem.getItem());
            assertEquals(quantity, billItem.getQuantity());
            assertEquals(unitPrice, billItem.getUnitPrice());
            assertEquals(new BigDecimal("149.97"), billItem.getTotalPrice());
        }
    }
    
    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {
        
        @BeforeEach
        void setUp() {
            billItem = new BillItem();
        }
        
        @Test
        @DisplayName("Should get and set bill item ID")
        void testBillItemId() {
            Long billItemId = 1L;
            billItem.setBillItemId(billItemId);
            assertEquals(billItemId, billItem.getBillItemId());
        }
        
        @Test
        @DisplayName("Should get and set bill")
        void testBill() {
            billItem.setBill(bill);
            assertEquals(bill, billItem.getBill());
        }
        
        @Test
        @DisplayName("Should get and set item")
        void testItem() {
            billItem.setItem(item);
            assertEquals(item, billItem.getItem());
        }
        
        @Test
        @DisplayName("Should get and set quantity with price recalculation")
        void testQuantity() {
            billItem.setUnitPrice(unitPrice);
            billItem.setQuantity(quantity);
            
            assertEquals(quantity, billItem.getQuantity());
            assertEquals(new BigDecimal("149.97"), billItem.getTotalPrice());
        }
        
        @Test
        @DisplayName("Should get and set unit price with price recalculation")
        void testUnitPrice() {
            billItem.setQuantity(quantity);
            billItem.setUnitPrice(unitPrice);
            
            assertEquals(unitPrice, billItem.getUnitPrice());
            assertEquals(new BigDecimal("149.97"), billItem.getTotalPrice());
        }
        
        @Test
        @DisplayName("Should get and set total price")
        void testTotalPrice() {
            BigDecimal totalPrice = new BigDecimal("150.00");
            billItem.setTotalPrice(totalPrice);
            assertEquals(totalPrice, billItem.getTotalPrice());
        }
    }
    
    @Nested
    @DisplayName("Price Calculation Tests")
    class PriceCalculationTests {
        
        @BeforeEach
        void setUp() {
            billItem = new BillItem();
        }
        
        @Test
        @DisplayName("Should recalculate total price correctly")
        void testRecalculateTotalPrice() {
            billItem.setQuantity(5);
            billItem.setUnitPrice(new BigDecimal("10.50"));
            billItem.recalculateTotalPrice();
            
            assertEquals(new BigDecimal("52.50"), billItem.getTotalPrice());
        }
        
        @Test
        @DisplayName("Should handle null values in price recalculation")
        void testRecalculateTotalPriceWithNulls() {
            billItem.setQuantity(null);
            billItem.setUnitPrice(unitPrice);
            billItem.recalculateTotalPrice();
            
            assertEquals(BigDecimal.ZERO, billItem.getTotalPrice());
        }
        
        @Test
        @DisplayName("Should recalculate when setting quantity")
        void testPriceRecalculationOnQuantitySet() {
            billItem.setUnitPrice(new BigDecimal("25.00"));
            billItem.setQuantity(4);
            
            assertEquals(new BigDecimal("100.00"), billItem.getTotalPrice());
        }
        
        @Test
        @DisplayName("Should recalculate when setting unit price")
        void testPriceRecalculationOnUnitPriceSet() {
            billItem.setQuantity(6);
            billItem.setUnitPrice(new BigDecimal("15.99"));
            
            assertEquals(new BigDecimal("95.94"), billItem.getTotalPrice());
        }
        
        @Test
        @DisplayName("Should not recalculate with incomplete data")
        void testNoRecalculationWithIncompleteData() {
            billItem.setQuantity(5);
            // No unit price set
            
            assertNull(billItem.getTotalPrice());
        }
    }
    
    @Nested
    @DisplayName("Business Method Tests")
    class BusinessMethodTests {
        
        @BeforeEach
        void setUp() {
            billItem = new BillItem(bill, item, quantity, unitPrice);
        }
        
        @Test
        @DisplayName("Should update quantity with validation")
        void testUpdateQuantity() {
            billItem.updateQuantity(5);
            
            assertEquals(Integer.valueOf(5), billItem.getQuantity());
            assertEquals(new BigDecimal("249.95"), billItem.getTotalPrice());
        }
        
        @Test
        @DisplayName("Should throw exception for negative quantity update")
        void testUpdateQuantityNegative() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                billItem.updateQuantity(-1);
            });
            
            assertTrue(exception.getMessage().contains("Quantity must be positive"));
        }
        
        @Test
        @DisplayName("Should throw exception for zero quantity update")
        void testUpdateQuantityZero() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                billItem.updateQuantity(0);
            });
            
            assertTrue(exception.getMessage().contains("Quantity must be positive"));
        }
        
        @Test
        @DisplayName("Should throw exception for null quantity update")
        void testUpdateQuantityNull() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                billItem.updateQuantity(null);
            });
            
            assertTrue(exception.getMessage().contains("Quantity must be positive"));
        }
        
        @Test
        @DisplayName("Should throw exception when updating quantity exceeds stock")
        void testUpdateQuantityInsufficientStock() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                billItem.updateQuantity(15); // Item has only 10 in stock
            });
            
            assertTrue(exception.getMessage().contains("Insufficient stock"));
            assertTrue(exception.getMessage().contains("Java Programming Book"));
        }
        
        @Test
        @DisplayName("Should update unit price with validation")
        void testUpdateUnitPrice() {
            BigDecimal newPrice = new BigDecimal("59.99");
            billItem.updateUnitPrice(newPrice);
            
            assertEquals(newPrice, billItem.getUnitPrice());
            assertEquals(new BigDecimal("179.97"), billItem.getTotalPrice());
        }
        
        @Test
        @DisplayName("Should throw exception for negative unit price")
        void testUpdateUnitPriceNegative() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                billItem.updateUnitPrice(new BigDecimal("-10.00"));
            });
            
            assertTrue(exception.getMessage().contains("Unit price must be non-negative"));
        }
        
        @Test
        @DisplayName("Should throw exception for null unit price")
        void testUpdateUnitPriceNull() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                billItem.updateUnitPrice(null);
            });
            
            assertTrue(exception.getMessage().contains("Unit price must be non-negative"));
        }
        
        @Test
        @DisplayName("Should allow zero unit price")
        void testUpdateUnitPriceZero() {
            assertDoesNotThrow(() -> {
                billItem.updateUnitPrice(BigDecimal.ZERO);
            });
            
            assertEquals(BigDecimal.ZERO, billItem.getUnitPrice());
            assertEquals(BigDecimal.ZERO, billItem.getTotalPrice());
        }
    }
    
    @Nested
    @DisplayName("Display Helper Tests")
    class DisplayHelperTests {
        
        @BeforeEach
        void setUp() {
            billItem = new BillItem(bill, item, quantity, unitPrice);
        }
        
        @Test
        @DisplayName("Should get item name correctly")
        void testGetItemName() {
            assertEquals("Java Programming Book", billItem.getItemName());
        }
        
        @Test
        @DisplayName("Should return empty string when item is null")
        void testGetItemNameWithNullItem() {
            billItem.setItem(null);
            assertEquals("", billItem.getItemName());
        }
        
        @Test
        @DisplayName("Should get item category correctly")
        void testGetItemCategory() {
            item.setCategory("Programming");
            assertEquals("Programming", billItem.getItemCategory());
        }
        
        @Test
        @DisplayName("Should return empty string when item category is null")
        void testGetItemCategoryWithNullCategory() {
            item.setCategory(null);
            assertEquals("", billItem.getItemCategory());
        }
        
        @Test
        @DisplayName("Should return empty string when item is null for category")
        void testGetItemCategoryWithNullItem() {
            billItem.setItem(null);
            assertEquals("", billItem.getItemCategory());
        }
        
        @Test
        @DisplayName("Should check if same item correctly")
        void testIsSameItem() {
            Item sameItem = new Item("Different Name", new BigDecimal("99.99"));
            sameItem.setItemId(item.getItemId()); // Same ID
            
            BillItem otherBillItem = new BillItem();
            otherBillItem.setItem(sameItem);
            
            assertTrue(billItem.isSameItem(otherBillItem));
        }
        
        @Test
        @DisplayName("Should return false for different items")
        void testIsSameItemDifferent() {
            Item differentItem = new Item("Python Book", new BigDecimal("39.99"));
            differentItem.setItemId(2L);
            
            BillItem otherBillItem = new BillItem();
            otherBillItem.setItem(differentItem);
            
            assertFalse(billItem.isSameItem(otherBillItem));
        }
        
        @Test
        @DisplayName("Should handle null in isSameItem")
        void testIsSameItemWithNulls() {
            assertFalse(billItem.isSameItem(null));
            
            BillItem nullItemBillItem = new BillItem();
            nullItemBillItem.setItem(null);
            assertFalse(billItem.isSameItem(nullItemBillItem));
            
            billItem.setItem(null);
            assertFalse(billItem.isSameItem(nullItemBillItem));
        }
    }
    
    @Nested
    @DisplayName("Equality and Hash Tests")
    class EqualityTests {
        
        @Test
        @DisplayName("Should be equal when bill item IDs match")
        void testEquals() {
            BillItem billItem1 = new BillItem();
            billItem1.setBillItemId(1L);
            BillItem billItem2 = new BillItem();
            billItem2.setBillItemId(1L);
            
            assertEquals(billItem1, billItem2);
            assertEquals(billItem1.hashCode(), billItem2.hashCode());
        }
        
        @Test
        @DisplayName("Should not be equal when bill item IDs differ")
        void testNotEquals() {
            BillItem billItem1 = new BillItem();
            billItem1.setBillItemId(1L);
            BillItem billItem2 = new BillItem();
            billItem2.setBillItemId(2L);
            
            assertNotEquals(billItem1, billItem2);
        }
        
        @Test
        @DisplayName("Should handle null in equals comparison")
        void testEqualsWithNull() {
            BillItem billItem1 = new BillItem();
            billItem1.setBillItemId(1L);
            
            assertNotEquals(billItem1, null);
            assertEquals(billItem1, billItem1);
        }
        
        @Test
        @DisplayName("Should handle different classes in equals comparison")
        void testEqualsWithDifferentClass() {
            BillItem billItem1 = new BillItem();
            billItem1.setBillItemId(1L);
            String notBillItem = "not a bill item";
            
            assertNotEquals(billItem1, notBillItem);
        }
        
        @Test
        @DisplayName("Should handle null bill item IDs in equals")
        void testEqualsWithNullBillItemIds() {
            BillItem billItem1 = new BillItem();
            BillItem billItem2 = new BillItem();
            
            assertEquals(billItem1, billItem2);
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("Should return meaningful string representation")
        void testToString() {
            billItem = new BillItem(bill, item, quantity, unitPrice);
            billItem.setBillItemId(1L);
            
            String result = billItem.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("1"));
            assertTrue(result.contains("Java Programming Book"));
            assertTrue(result.contains("3"));
            assertTrue(result.contains("49.99"));
            assertTrue(result.contains("149.97"));
            assertTrue(result.contains("BillItem{"));
        }
        
        @Test
        @DisplayName("Should handle null values in toString")
        void testToStringWithNulls() {
            billItem = new BillItem();
            
            String result = billItem.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("BillItem{"));
            assertTrue(result.contains("null"));
        }
    }
    
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle very large quantities")
        void testLargeQuantity() {
            billItem = new BillItem();
            billItem.setUnitPrice(new BigDecimal("1.00"));
            billItem.setQuantity(Integer.MAX_VALUE);
            
            assertEquals(new BigDecimal(Integer.MAX_VALUE), billItem.getTotalPrice());
        }
        
        @Test
        @DisplayName("Should handle very small unit prices")
        void testSmallUnitPrice() {
            billItem = new BillItem();
            billItem.setQuantity(1000);
            billItem.setUnitPrice(new BigDecimal("0.001"));
            
            assertEquals(new BigDecimal("1.000"), billItem.getTotalPrice());
        }
        
        @Test
        @DisplayName("Should handle price precision correctly")
        void testPricePrecision() {
            billItem = new BillItem();
            billItem.setQuantity(3);
            billItem.setUnitPrice(new BigDecimal("33.333"));
            
            assertEquals(new BigDecimal("99.999"), billItem.getTotalPrice());
        }
        
        @Test
        @DisplayName("Should maintain data integrity across operations")
        void testDataIntegrity() {
            billItem = new BillItem(bill, item, 2, new BigDecimal("25.00"));
            
            assertEquals(new BigDecimal("50.00"), billItem.getTotalPrice());
            
            billItem.updateQuantity(4);
            assertEquals(new BigDecimal("100.00"), billItem.getTotalPrice());
            
            billItem.updateUnitPrice(new BigDecimal("30.00"));
            assertEquals(new BigDecimal("120.00"), billItem.getTotalPrice());
        }
    }
}