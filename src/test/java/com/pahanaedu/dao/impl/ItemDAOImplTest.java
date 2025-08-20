package com.pahanaedu.dao.impl;

import com.pahanaedu.BaseTestCase;
import com.pahanaedu.model.Item;
import com.pahanaedu.model.BillItem;
import com.pahanaedu.model.Bill;
import com.pahanaedu.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Item DAO Implementation Tests")
class ItemDAOImplTest extends BaseTestCase {
    
    private ItemDAOImpl itemDAO;
    private Item testItem1;
    private Item testItem2;
    private Item testItem3;
    private Item testItem4;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        if (isTestEnvironmentReady()) {
            itemDAO = new ItemDAOImpl();
            itemDAO.entityManager = entityManager;
            
            testItem1 = new Item("Java Programming Guide", new BigDecimal("49.99"));
            testItem1.setDescription("Comprehensive Java programming book");
            testItem1.setStockQuantity(10);
            testItem1.setCategory("Programming");
            
            testItem2 = new Item("Python for Beginners", new BigDecimal("29.99"));
            testItem2.setDescription("Introduction to Python programming");
            testItem2.setStockQuantity(0); // Out of stock
            testItem2.setCategory("Programming");
            
            testItem3 = new Item("Mathematics Textbook", new BigDecimal("89.99"));
            testItem3.setDescription("Advanced mathematics concepts");
            testItem3.setStockQuantity(3); // Low stock
            testItem3.setCategory("Mathematics");
            
            testItem4 = new Item("History of Science", new BigDecimal("39.99"));
            testItem4.setDescription("Scientific discoveries throughout history");
            testItem4.setStockQuantity(25);
            testItem4.setCategory("History");
        }
    }
    
    @Nested
    @DisplayName("Base CRUD Operation Tests")
    class BaseCrudTests {
        
        @Test
        @DisplayName("Should save item successfully")
        void testSaveItem() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Item savedItem = itemDAO.save(testItem1);
            commitTransaction();
            
            assertNotNull(savedItem);
            assertNotNull(savedItem.getItemId());
            assertEquals(testItem1.getItemName(), savedItem.getItemName());
            assertEquals(testItem1.getUnitPrice(), savedItem.getUnitPrice());
            assertEquals(testItem1.getStockQuantity(), savedItem.getStockQuantity());
        }
        
        @Test
        @DisplayName("Should update item successfully")
        void testUpdateItem() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Item savedItem = itemDAO.save(testItem1);
            commitTransaction();
            
            savedItem.setItemName("Updated Java Guide");
            savedItem.setUnitPrice(new BigDecimal("59.99"));
            
            beginTransaction();
            Item updatedItem = itemDAO.update(savedItem);
            commitTransaction();
            
            assertEquals("Updated Java Guide", updatedItem.getItemName());
            assertEquals(new BigDecimal("59.99"), updatedItem.getUnitPrice());
        }
        
        @Test
        @DisplayName("Should find item by ID")
        void testFindById() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Item savedItem = itemDAO.save(testItem1);
            commitTransaction();
            clearEntityManager();
            
            Optional<Item> foundItem = itemDAO.findById(savedItem.getItemId());
            
            assertTrue(foundItem.isPresent());
            assertEquals(savedItem.getItemId(), foundItem.get().getItemId());
            assertEquals(savedItem.getItemName(), foundItem.get().getItemName());
        }
        
        @Test
        @DisplayName("Should delete item by ID")
        void testDeleteById() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Item savedItem = itemDAO.save(testItem1);
            commitTransaction();
            
            Long itemId = savedItem.getItemId();
            
            beginTransaction();
            itemDAO.deleteById(itemId);
            commitTransaction();
            clearEntityManager();
            
            Optional<Item> foundItem = itemDAO.findById(itemId);
            assertFalse(foundItem.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Item-Specific Query Tests")
    class ItemSpecificQueryTests {
        
        @BeforeEach
        void setUpItems() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            itemDAO.save(testItem1);
            itemDAO.save(testItem2);
            itemDAO.save(testItem3);
            itemDAO.save(testItem4);
            commitTransaction();
            clearEntityManager();
        }
        
        @Test
        @DisplayName("Should find item by item ID")
        void testFindByItemId() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Item savedItem = itemDAO.save(new Item("Test Item", new BigDecimal("10.00")));
            commitTransaction();
            clearEntityManager();
            
            Optional<Item> foundItem = itemDAO.findByItemId(savedItem.getItemId());
            
            assertTrue(foundItem.isPresent());
            assertEquals("Test Item", foundItem.get().getItemName());
        }
        
        @Test
        @DisplayName("Should find items by name containing")
        void testFindByItemNameContaining() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> programmingBooks = itemDAO.findByItemNameContaining("Programming");
            
            assertNotNull(programmingBooks);
            assertTrue(programmingBooks.size() >= 1);
            assertTrue(programmingBooks.stream()
                .anyMatch(item -> item.getItemName().contains("Programming")));
        }
        
        @Test
        @DisplayName("Should find items by name containing case-insensitive")
        void testFindByItemNameContainingCaseInsensitive() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> programmingBooks = itemDAO.findByItemNameContaining("programming");
            
            assertNotNull(programmingBooks);
            assertTrue(programmingBooks.size() >= 1);
            assertTrue(programmingBooks.stream()
                .anyMatch(item -> item.getItemName().toLowerCase().contains("programming")));
        }
        
        @Test
        @DisplayName("Should find items by category")
        void testFindByCategory() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> programmingItems = itemDAO.findByCategory("Programming");
            
            assertNotNull(programmingItems);
            assertTrue(programmingItems.size() >= 2);
            assertTrue(programmingItems.stream()
                .allMatch(item -> item.getCategory().equals("Programming")));
        }
        
        @Test
        @DisplayName("Should find items with stock greater than specified amount")
        void testFindByStockQuantityGreaterThan() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> itemsWithStock = itemDAO.findByStockQuantityGreaterThan(5);
            
            assertNotNull(itemsWithStock);
            assertTrue(itemsWithStock.size() >= 2); // testItem1 and testItem4
            assertTrue(itemsWithStock.stream()
                .allMatch(item -> item.getStockQuantity() > 5));
        }
        
        @Test
        @DisplayName("Should find out of stock items")
        void testFindOutOfStockItems() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> outOfStockItems = itemDAO.findOutOfStockItems();
            
            assertNotNull(outOfStockItems);
            assertTrue(outOfStockItems.size() >= 1); // testItem2
            assertTrue(outOfStockItems.stream()
                .allMatch(item -> item.getStockQuantity() == 0));
            assertTrue(outOfStockItems.stream()
                .anyMatch(item -> item.getItemName().equals("Python for Beginners")));
        }
        
        @Test
        @DisplayName("Should find low stock items")
        void testFindLowStockItems() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> lowStockItems = itemDAO.findLowStockItems(5);
            
            assertNotNull(lowStockItems);
            assertTrue(lowStockItems.size() >= 1); // testItem3
            assertTrue(lowStockItems.stream()
                .allMatch(item -> item.getStockQuantity() > 0 && item.getStockQuantity() <= 5));
            assertTrue(lowStockItems.stream()
                .anyMatch(item -> item.getItemName().equals("Mathematics Textbook")));
        }
        
        @Test
        @DisplayName("Should find items by price range")
        void testFindByPriceRange() {
            if (!isTestEnvironmentReady()) return;
            
            BigDecimal minPrice = new BigDecimal("30.00");
            BigDecimal maxPrice = new BigDecimal("50.00");
            
            List<Item> itemsInRange = itemDAO.findByPriceRange(minPrice, maxPrice);
            
            assertNotNull(itemsInRange);
            assertTrue(itemsInRange.size() >= 2); // testItem1 and testItem4
            assertTrue(itemsInRange.stream()
                .allMatch(item -> item.getUnitPrice().compareTo(minPrice) >= 0 
                               && item.getUnitPrice().compareTo(maxPrice) <= 0));
        }
        
        @Test
        @DisplayName("Should find all distinct categories")
        void testFindAllCategories() {
            if (!isTestEnvironmentReady()) return;
            
            List<String> categories = itemDAO.findAllCategories();
            
            assertNotNull(categories);
            assertTrue(categories.size() >= 3);
            assertTrue(categories.contains("Programming"));
            assertTrue(categories.contains("Mathematics"));
            assertTrue(categories.contains("History"));
        }
    }
    
    @Nested
    @DisplayName("Stock Management Tests")
    class StockManagementTests {
        
        @Test
        @DisplayName("Should update stock quantity")
        void testUpdateStockQuantity() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Item savedItem = itemDAO.save(testItem1);
            commitTransaction();
            
            beginTransaction();
            Item updatedItem = itemDAO.updateStockQuantity(savedItem.getItemId(), 15);
            commitTransaction();
            
            assertEquals(Integer.valueOf(15), updatedItem.getStockQuantity());
        }
        
        @Test
        @DisplayName("Should throw exception when updating stock of non-existent item")
        void testUpdateStockQuantityNonExistentItem() {
            if (!isTestEnvironmentReady()) return;
            
            assertThrows(IllegalArgumentException.class, () -> {
                beginTransaction();
                itemDAO.updateStockQuantity(999L, 10);
                commitTransaction();
            });
        }
        
        @Test
        @DisplayName("Should increase stock quantity")
        void testIncreaseStock() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Item savedItem = itemDAO.save(testItem1);
            commitTransaction();
            
            int originalStock = savedItem.getStockQuantity();
            
            beginTransaction();
            Item updatedItem = itemDAO.increaseStock(savedItem.getItemId(), 5);
            commitTransaction();
            
            assertEquals(Integer.valueOf(originalStock + 5), updatedItem.getStockQuantity());
        }
        
        @Test
        @DisplayName("Should throw exception when increasing stock of non-existent item")
        void testIncreaseStockNonExistentItem() {
            if (!isTestEnvironmentReady()) return;
            
            assertThrows(IllegalArgumentException.class, () -> {
                beginTransaction();
                itemDAO.increaseStock(999L, 5);
                commitTransaction();
            });
        }
        
        @Test
        @DisplayName("Should decrease stock quantity")
        void testDecreaseStock() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Item savedItem = itemDAO.save(testItem1);
            commitTransaction();
            
            int originalStock = savedItem.getStockQuantity();
            
            beginTransaction();
            Item updatedItem = itemDAO.decreaseStock(savedItem.getItemId(), 3);
            commitTransaction();
            
            assertEquals(Integer.valueOf(originalStock - 3), updatedItem.getStockQuantity());
        }
        
        @Test
        @DisplayName("Should throw exception when decreasing stock below zero")
        void testDecreaseStockInsufficientStock() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Item savedItem = itemDAO.save(testItem1); // Stock = 10
            commitTransaction();
            
            assertThrows(IllegalArgumentException.class, () -> {
                beginTransaction();
                itemDAO.decreaseStock(savedItem.getItemId(), 15); // More than available
                commitTransaction();
            });
        }
        
        @Test
        @DisplayName("Should throw exception when decreasing stock of non-existent item")
        void testDecreaseStockNonExistentItem() {
            if (!isTestEnvironmentReady()) return;
            
            assertThrows(IllegalArgumentException.class, () -> {
                beginTransaction();
                itemDAO.decreaseStock(999L, 1);
                commitTransaction();
            });
        }
    }
    
    @Nested
    @DisplayName("Advanced Search Tests")
    class AdvancedSearchTests {
        
        @BeforeEach
        void setUpItems() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            itemDAO.save(testItem1);
            itemDAO.save(testItem2);
            itemDAO.save(testItem3);
            itemDAO.save(testItem4);
            commitTransaction();
            clearEntityManager();
        }
        
        @Test
        @DisplayName("Should search items by name only")
        void testSearchItemsByName() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> items = itemDAO.searchItems("Programming", null, null, null, false);
            
            assertNotNull(items);
            assertTrue(items.size() >= 1);
            assertTrue(items.stream()
                .anyMatch(item -> item.getItemName().contains("Programming")));
        }
        
        @Test
        @DisplayName("Should search items by category only")
        void testSearchItemsByCategory() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> items = itemDAO.searchItems(null, "Programming", null, null, false);
            
            assertNotNull(items);
            assertTrue(items.size() >= 2);
            assertTrue(items.stream()
                .allMatch(item -> item.getCategory().equals("Programming")));
        }
        
        @Test
        @DisplayName("Should search items by price range only")
        void testSearchItemsByPriceRange() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> items = itemDAO.searchItems(null, null, 
                new BigDecimal("30.00"), new BigDecimal("50.00"), false);
            
            assertNotNull(items);
            assertTrue(items.stream()
                .allMatch(item -> item.getUnitPrice().compareTo(new BigDecimal("30.00")) >= 0
                               && item.getUnitPrice().compareTo(new BigDecimal("50.00")) <= 0));
        }
        
        @Test
        @DisplayName("Should search items in stock only")
        void testSearchItemsInStockOnly() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> items = itemDAO.searchItems(null, null, null, null, true);
            
            assertNotNull(items);
            assertTrue(items.stream()
                .allMatch(item -> item.getStockQuantity() > 0));
            assertTrue(items.stream()
                .noneMatch(item -> item.getItemName().equals("Python for Beginners")));
        }
        
        @Test
        @DisplayName("Should search items with multiple criteria")
        void testSearchItemsMultipleCriteria() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> items = itemDAO.searchItems("Programming", "Programming", 
                new BigDecimal("20.00"), new BigDecimal("60.00"), true);
            
            assertNotNull(items);
            assertTrue(items.stream()
                .allMatch(item -> item.getItemName().toLowerCase().contains("programming")
                               && item.getCategory().equals("Programming")
                               && item.getUnitPrice().compareTo(new BigDecimal("20.00")) >= 0
                               && item.getUnitPrice().compareTo(new BigDecimal("60.00")) <= 0
                               && item.getStockQuantity() > 0));
        }
        
        @Test
        @DisplayName("Should handle empty search criteria")
        void testSearchItemsEmptyCriteria() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> items = itemDAO.searchItems("", "", null, null, false);
            
            assertNotNull(items);
            // Should return all items when criteria are empty
        }
    }
    
    @Nested
    @DisplayName("Statistics and Analytics Tests")
    class StatisticsTests {
        
        @BeforeEach
        void setUpItems() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            itemDAO.save(testItem1); // In stock (10)
            itemDAO.save(testItem2); // Out of stock (0)
            itemDAO.save(testItem3); // Low stock (3)
            itemDAO.save(testItem4); // High stock (25)
            commitTransaction();
            clearEntityManager();
        }
        
        @Test
        @DisplayName("Should get item statistics correctly")
        void testGetItemStatistics() {
            if (!isTestEnvironmentReady()) return;
            
            long[] stats = itemDAO.getItemStatistics(5); // Low stock threshold = 5
            
            assertNotNull(stats);
            assertEquals(4, stats.length);
            assertTrue(stats[0] >= 4); // Total items
            assertTrue(stats[1] >= 3); // Items in stock
            assertTrue(stats[2] >= 1); // Out of stock items
            assertTrue(stats[3] >= 1); // Low stock items (testItem3)
        }
        
        @Test
        @DisplayName("Should get top selling items")
        void testGetTopSellingItems() {
            if (!isTestEnvironmentReady()) return;
            
            // Create some bill items to simulate sales
            beginTransaction();
            Item savedItem1 = itemDAO.save(testItem1);
            Item savedItem4 = itemDAO.save(testItem4);
            
            Customer customer = new Customer("ACC001", "Test Customer");
            entityManager.persist(customer);
            
            Bill bill = new Bill(customer);
            entityManager.persist(bill);
            
            BillItem billItem1 = new BillItem(bill, savedItem1, 5, savedItem1.getUnitPrice());
            BillItem billItem2 = new BillItem(bill, savedItem4, 10, savedItem4.getUnitPrice());
            
            entityManager.persist(billItem1);
            entityManager.persist(billItem2);
            
            commitTransaction();
            clearEntityManager();
            
            List<Item> topSellingItems = itemDAO.getTopSellingItems(5);
            
            assertNotNull(topSellingItems);
            // Should return items ordered by quantity sold
        }
        
        @Test
        @DisplayName("Should limit top selling items correctly")
        void testGetTopSellingItemsWithLimit() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> topSellingItems = itemDAO.getTopSellingItems(2);
            
            assertNotNull(topSellingItems);
            assertTrue(topSellingItems.size() <= 2);
        }
    }
    
    @Nested
    @DisplayName("Edge Case and Error Handling Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should return empty list when no items match search")
        void testSearchWithNoMatches() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> items = itemDAO.findByItemNameContaining("NonexistentItem");
            
            assertNotNull(items);
            assertTrue(items.isEmpty());
        }
        
        @Test
        @DisplayName("Should handle null parameters in search")
        void testSearchWithNullParameters() {
            if (!isTestEnvironmentReady()) return;
            
            assertDoesNotThrow(() -> {
                List<Item> items = itemDAO.searchItems(null, null, null, null, false);
                assertNotNull(items);
            });
        }
        
        @Test
        @DisplayName("Should handle invalid price range in search")
        void testSearchWithInvalidPriceRange() {
            if (!isTestEnvironmentReady()) return;
            
            BigDecimal minPrice = new BigDecimal("100.00");
            BigDecimal maxPrice = new BigDecimal("10.00"); // Max < Min
            
            List<Item> items = itemDAO.findByPriceRange(minPrice, maxPrice);
            
            assertNotNull(items);
            assertTrue(items.isEmpty());
        }
        
        @Test
        @DisplayName("Should handle negative stock threshold in statistics")
        void testStatisticsWithNegativeThreshold() {
            if (!isTestEnvironmentReady()) return;
            
            assertDoesNotThrow(() -> {
                long[] stats = itemDAO.getItemStatistics(-1);
                assertNotNull(stats);
                assertEquals(4, stats.length);
            });
        }
        
        @Test
        @DisplayName("Should handle zero limit in top selling items")
        void testTopSellingItemsWithZeroLimit() {
            if (!isTestEnvironmentReady()) return;
            
            List<Item> topSellingItems = itemDAO.getTopSellingItems(0);
            
            assertNotNull(topSellingItems);
            assertTrue(topSellingItems.isEmpty());
        }
        
        @Test
        @DisplayName("Should handle very large limit in top selling items")
        void testTopSellingItemsWithLargeLimit() {
            if (!isTestEnvironmentReady()) return;
            
            assertDoesNotThrow(() -> {
                List<Item> topSellingItems = itemDAO.getTopSellingItems(1000);
                assertNotNull(topSellingItems);
            });
        }
        
        @Test
        @DisplayName("Should handle categories with null values")
        void testFindCategoriesWithNullValues() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            Item itemWithoutCategory = new Item("No Category Item", new BigDecimal("10.00"));
            itemWithoutCategory.setCategory(null);
            itemDAO.save(itemWithoutCategory);
            commitTransaction();
            
            List<String> categories = itemDAO.findAllCategories();
            
            assertNotNull(categories);
            assertFalse(categories.contains(null));
        }
    }
}