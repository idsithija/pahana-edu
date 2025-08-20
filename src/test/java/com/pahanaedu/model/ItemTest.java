package com.pahanaedu.model;

import com.pahanaedu.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Item Entity Tests")
class ItemTest extends BaseTestCase {
    
    private Item item;
    private String itemName;
    private String description;
    private BigDecimal unitPrice;
    private Integer stockQuantity;
    private String category;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        itemName = "Java Programming Book";
        description = "Complete guide to Java programming";
        unitPrice = new BigDecimal("49.99");
        stockQuantity = 10;
        category = "Programming";
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create item with default constructor")
        void testDefaultConstructor() {
            item = new Item();
            
            assertNotNull(item);
            assertNull(item.getItemId());
            assertNull(item.getItemName());
            assertNull(item.getDescription());
            assertNull(item.getUnitPrice());
            assertEquals(Integer.valueOf(0), item.getStockQuantity());
            assertNull(item.getCategory());
            assertNotNull(item.getBillItems());
            assertTrue(item.getBillItems().isEmpty());
        }
        
        @Test
        @DisplayName("Should create item with name and price")
        void testConstructorWithNameAndPrice() {
            item = new Item(itemName, unitPrice);
            
            assertEquals(itemName, item.getItemName());
            assertEquals(unitPrice, item.getUnitPrice());
            assertEquals(Integer.valueOf(0), item.getStockQuantity());
            assertNull(item.getDescription());
            assertNull(item.getCategory());
        }
        
        @Test
        @DisplayName("Should create item with full details")
        void testConstructorWithFullDetails() {
            item = new Item(itemName, description, unitPrice, stockQuantity, category);
            
            assertEquals(itemName, item.getItemName());
            assertEquals(description, item.getDescription());
            assertEquals(unitPrice, item.getUnitPrice());
            assertEquals(stockQuantity, item.getStockQuantity());
            assertEquals(category, item.getCategory());
        }
    }
    
    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {
        
        @BeforeEach
        void setUp() {
            item = new Item();
        }
        
        @Test
        @DisplayName("Should get and set item ID")
        void testItemId() {
            Long itemId = 1L;
            item.setItemId(itemId);
            assertEquals(itemId, item.getItemId());
        }
        
        @Test
        @DisplayName("Should get and set item name")
        void testItemName() {
            item.setItemName(itemName);
            assertEquals(itemName, item.getItemName());
        }
        
        @Test
        @DisplayName("Should get and set description")
        void testDescription() {
            item.setDescription(description);
            assertEquals(description, item.getDescription());
        }
        
        @Test
        @DisplayName("Should get and set unit price")
        void testUnitPrice() {
            item.setUnitPrice(unitPrice);
            assertEquals(unitPrice, item.getUnitPrice());
        }
        
        @Test
        @DisplayName("Should get and set stock quantity")
        void testStockQuantity() {
            item.setStockQuantity(stockQuantity);
            assertEquals(stockQuantity, item.getStockQuantity());
        }
        
        @Test
        @DisplayName("Should get and set category")
        void testCategory() {
            item.setCategory(category);
            assertEquals(category, item.getCategory());
        }
        
        @Test
        @DisplayName("Should get and set bill items")
        void testBillItems() {
            List<BillItem> billItems = new ArrayList<>();
            item.setBillItems(billItems);
            assertEquals(billItems, item.getBillItems());
        }
    }
    
    @Nested
    @DisplayName("Stock Management Tests")
    class StockManagementTests {
        
        @BeforeEach
        void setUp() {
            item = new Item(itemName, unitPrice);
            item.setStockQuantity(10);
        }
        
        @Test
        @DisplayName("Should check if item is in stock")
        void testIsInStock() {
            assertTrue(item.isInStock());
            
            item.setStockQuantity(0);
            assertFalse(item.isInStock());
            
            item.setStockQuantity(null);
            assertFalse(item.isInStock());
        }
        
        @Test
        @DisplayName("Should check if item has sufficient stock")
        void testHasStock() {
            assertTrue(item.hasStock(5));
            assertTrue(item.hasStock(10));
            assertFalse(item.hasStock(15));
            
            item.setStockQuantity(null);
            assertFalse(item.hasStock(1));
        }
        
        @Test
        @DisplayName("Should reduce stock correctly")
        void testReduceStock() {
            item.reduceStock(3);
            assertEquals(Integer.valueOf(7), item.getStockQuantity());
            
            item.reduceStock(7);
            assertEquals(Integer.valueOf(0), item.getStockQuantity());
        }
        
        @Test
        @DisplayName("Should throw exception when reducing stock below zero")
        void testReduceStockInsufficientStock() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                item.reduceStock(15);
            });
            
            assertTrue(exception.getMessage().contains("Insufficient stock"));
            assertTrue(exception.getMessage().contains("Available: 10"));
            assertTrue(exception.getMessage().contains("Required: 15"));
        }
        
        @Test
        @DisplayName("Should handle null stock quantity in reduce stock")
        void testReduceStockWithNullQuantity() {
            item.setStockQuantity(null);
            
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                item.reduceStock(1);
            });
            
            assertTrue(exception.getMessage().contains("Insufficient stock"));
        }
        
        @Test
        @DisplayName("Should increase stock correctly")
        void testIncreaseStock() {
            item.increaseStock(5);
            assertEquals(Integer.valueOf(15), item.getStockQuantity());
        }
        
        @Test
        @DisplayName("Should handle null stock quantity in increase stock")
        void testIncreaseStockWithNullQuantity() {
            item.setStockQuantity(null);
            item.increaseStock(5);
            assertEquals(Integer.valueOf(5), item.getStockQuantity());
        }
    }
    
    @Nested
    @DisplayName("Price Calculation Tests")
    class PriceCalculationTests {
        
        @BeforeEach
        void setUp() {
            item = new Item(itemName, new BigDecimal("10.00"));
        }
        
        @Test
        @DisplayName("Should calculate total price for given quantity")
        void testCalculateTotalPrice() {
            BigDecimal totalPrice = item.calculateTotalPrice(3);
            assertEquals(new BigDecimal("30.00"), totalPrice);
            
            totalPrice = item.calculateTotalPrice(0);
            assertEquals(BigDecimal.ZERO, totalPrice);
        }
        
        @Test
        @DisplayName("Should return zero when unit price is null")
        void testCalculateTotalPriceWithNullUnitPrice() {
            item.setUnitPrice(null);
            BigDecimal totalPrice = item.calculateTotalPrice(5);
            assertEquals(BigDecimal.ZERO, totalPrice);
        }
        
        @Test
        @DisplayName("Should handle large quantities in price calculation")
        void testCalculateTotalPriceWithLargeQuantity() {
            BigDecimal totalPrice = item.calculateTotalPrice(1000);
            assertEquals(new BigDecimal("10000.00"), totalPrice);
        }
    }
    
    @Nested
    @DisplayName("Sales Analytics Tests")
    class SalesAnalyticsTests {
        
        @BeforeEach
        void setUp() {
            item = new Item(itemName, unitPrice);
        }
        
        @Test
        @DisplayName("Should calculate total quantity sold correctly")
        void testGetTotalQuantitySold() {
            // Create mock bill items
            BillItem billItem1 = new BillItem();
            billItem1.setQuantity(5);
            BillItem billItem2 = new BillItem();
            billItem2.setQuantity(3);
            BillItem billItem3 = new BillItem();
            billItem3.setQuantity(2);
            
            List<BillItem> billItems = new ArrayList<>();
            billItems.add(billItem1);
            billItems.add(billItem2);
            billItems.add(billItem3);
            
            item.setBillItems(billItems);
            
            assertEquals(10, item.getTotalQuantitySold());
        }
        
        @Test
        @DisplayName("Should return zero when no bill items exist")
        void testGetTotalQuantitySoldWithNoBillItems() {
            assertEquals(0, item.getTotalQuantitySold());
        }
    }
    
    @Nested
    @DisplayName("Equality and Hash Tests")
    class EqualityTests {
        
        @Test
        @DisplayName("Should be equal when item IDs match")
        void testEquals() {
            Item item1 = new Item();
            item1.setItemId(1L);
            Item item2 = new Item();
            item2.setItemId(1L);
            
            assertEquals(item1, item2);
            assertEquals(item1.hashCode(), item2.hashCode());
        }
        
        @Test
        @DisplayName("Should not be equal when item IDs differ")
        void testNotEquals() {
            Item item1 = new Item();
            item1.setItemId(1L);
            Item item2 = new Item();
            item2.setItemId(2L);
            
            assertNotEquals(item1, item2);
        }
        
        @Test
        @DisplayName("Should handle null in equals comparison")
        void testEqualsWithNull() {
            Item item1 = new Item();
            item1.setItemId(1L);
            
            assertNotEquals(item1, null);
            assertEquals(item1, item1);
        }
        
        @Test
        @DisplayName("Should handle different classes in equals comparison")
        void testEqualsWithDifferentClass() {
            Item item1 = new Item();
            item1.setItemId(1L);
            String notItem = "not an item";
            
            assertNotEquals(item1, notItem);
        }
        
        @Test
        @DisplayName("Should handle null item IDs in equals")
        void testEqualsWithNullItemIds() {
            Item item1 = new Item();
            Item item2 = new Item();
            
            assertEquals(item1, item2);
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("Should return meaningful string representation")
        void testToString() {
            item = new Item(itemName, description, unitPrice, stockQuantity, category);
            item.setItemId(1L);
            
            String result = item.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("1"));
            assertTrue(result.contains(itemName));
            assertTrue(result.contains(description));
            assertTrue(result.contains(unitPrice.toString()));
            assertTrue(result.contains(stockQuantity.toString()));
            assertTrue(result.contains(category));
            assertTrue(result.contains("Item{"));
        }
        
        @Test
        @DisplayName("Should handle null values in toString")
        void testToStringWithNulls() {
            item = new Item();
            
            String result = item.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("Item{"));
        }
    }
    
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle zero stock quantity operations")
        void testZeroStockOperations() {
            item = new Item(itemName, unitPrice);
            item.setStockQuantity(0);
            
            assertFalse(item.isInStock());
            assertFalse(item.hasStock(1));
            assertTrue(item.hasStock(0));
        }
        
        @Test
        @DisplayName("Should handle negative price in calculations")
        void testNegativePriceCalculations() {
            item = new Item(itemName, new BigDecimal("-5.00"));
            
            BigDecimal totalPrice = item.calculateTotalPrice(3);
            assertEquals(new BigDecimal("-15.00"), totalPrice);
        }
        
        @Test
        @DisplayName("Should handle very large stock quantities")
        void testLargeStockQuantities() {
            item = new Item(itemName, unitPrice);
            item.setStockQuantity(Integer.MAX_VALUE);
            
            assertTrue(item.isInStock());
            assertTrue(item.hasStock(1000000));
        }
        
        @Test
        @DisplayName("Should handle precision in price calculations")
        void testPricePrecision() {
            item = new Item(itemName, new BigDecimal("10.999"));
            
            BigDecimal totalPrice = item.calculateTotalPrice(3);
            assertEquals(new BigDecimal("32.997"), totalPrice);
        }
    }
}