package com.pahanaedu.dao;

import com.pahanaedu.model.Item;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Item entity
 * Provides item-specific database operations
 */
public interface ItemDAO extends BaseDAO<Item, Long> {
    
    /**
     * Find item by ID
     * @param itemId Item ID
     * @return Optional containing item if found
     */
    Optional<Item> findByItemId(Long itemId);
    
    /**
     * Find items by name (partial match, case-insensitive)
     * @param itemName Item name or partial name
     * @return List of matching items
     */
    List<Item> findByItemNameContaining(String itemName);
    
    /**
     * Find items by category
     * @param category Item category
     * @return List of items in specified category
     */
    List<Item> findByCategory(String category);
    
    /**
     * Find items with stock quantity greater than specified amount
     * @param minQuantity Minimum stock quantity
     * @return List of items with sufficient stock
     */
    List<Item> findByStockQuantityGreaterThan(int minQuantity);
    
    /**
     * Find items that are out of stock (stock quantity = 0)
     * @return List of out-of-stock items
     */
    List<Item> findOutOfStockItems();
    
    /**
     * Find items with low stock (below specified threshold)
     * @param threshold Stock threshold
     * @return List of items with low stock
     */
    List<Item> findLowStockItems(int threshold);
    
    /**
     * Find items within price range
     * @param minPrice Minimum price (inclusive)
     * @param maxPrice Maximum price (inclusive)
     * @return List of items within price range
     */
    List<Item> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Get all distinct categories
     * @return List of all item categories
     */
    List<String> findAllCategories();
    
    /**
     * Update item stock quantity
     * @param itemId Item ID
     * @param newQuantity New stock quantity
     * @return Updated item
     */
    Item updateStockQuantity(Long itemId, int newQuantity);
    
    /**
     * Increase item stock quantity
     * @param itemId Item ID
     * @param quantity Quantity to add
     * @return Updated item
     */
    Item increaseStock(Long itemId, int quantity);
    
    /**
     * Decrease item stock quantity
     * @param itemId Item ID
     * @param quantity Quantity to subtract
     * @return Updated item
     * @throws IllegalArgumentException if insufficient stock
     */
    Item decreaseStock(Long itemId, int quantity);
    
    /**
     * Search items by multiple criteria
     * @param itemName Item name (partial match)
     * @param category Category filter
     * @param minPrice Minimum price filter
     * @param maxPrice Maximum price filter
     * @param inStockOnly Show only items in stock
     * @return List of matching items
     */
    List<Item> searchItems(String itemName, String category, BigDecimal minPrice, BigDecimal maxPrice, boolean inStockOnly);
    
    /**
     * Get item statistics
     * @return Array containing [totalItems, itemsInStock, outOfStockItems, lowStockItems]
     */
    long[] getItemStatistics(int lowStockThreshold);
    
    /**
     * Get top selling items
     * @param limit Maximum number of items to return
     * @return List of top selling items ordered by quantity sold
     */
    List<Item> getTopSellingItems(int limit);
}

