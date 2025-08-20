package com.pahanaedu.service;

import com.pahanaedu.model.Item;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Item-related business operations
 * Provides item management services
 */
public interface ItemService {
    
    /**
     * Create a new item
     * @param itemName Item name
     * @param description Item description
     * @param unitPrice Unit price
     * @param stockQuantity Initial stock quantity
     * @param category Item category
     * @return Created item
     */
    Item createItem(String itemName, String description, BigDecimal unitPrice, Integer stockQuantity, String category);
    
    /**
     * Update item information
     * @param item Item to update
     * @return Updated item
     */
    Item updateItem(Item item);
    
    /**
     * Find item by ID
     * @param itemId Item ID
     * @return Optional containing item if found
     */
    Optional<Item> findById(Long itemId);
    
    /**
     * Search items by name
     * @param itemName Item name (partial match)
     * @return List of matching items
     */
    List<Item> searchByName(String itemName);
    
    /**
     * Find items by category
     * @param category Item category
     * @return List of items in category
     */
    List<Item> findByCategory(String category);
    
    /**
     * Get all items
     * @return List of all items
     */
    List<Item> getAllItems();
    
    /**
     * Get items in stock
     * @return List of items with stock > 0
     */
    List<Item> getItemsInStock();
    
    /**
     * Get out of stock items
     * @return List of items with stock = 0
     */
    List<Item> getOutOfStockItems();
    
    /**
     * Get low stock items
     * @param threshold Stock threshold
     * @return List of items with low stock
     */
    List<Item> getLowStockItems(int threshold);
    
    /**
     * Update stock quantity
     * @param itemId Item ID
     * @param newQuantity New stock quantity
     * @return Updated item
     */
    Item updateStockQuantity(Long itemId, int newQuantity);
    
    /**
     * Increase stock
     * @param itemId Item ID
     * @param quantity Quantity to add
     * @return Updated item
     */
    Item increaseStock(Long itemId, int quantity);
    
    /**
     * Decrease stock
     * @param itemId Item ID
     * @param quantity Quantity to subtract
     * @return Updated item
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
     * Get all categories
     * @return List of all item categories
     */
    List<String> getAllCategories();
    
    /**
     * Get item statistics
     * @param lowStockThreshold Low stock threshold
     * @return Array containing [totalItems, itemsInStock, outOfStockItems, lowStockItems]
     */
    long[] getItemStatistics(int lowStockThreshold);
    
    /**
     * Get top selling items
     * @param limit Maximum number of items
     * @return List of top selling items
     */
    List<Item> getTopSellingItems(int limit);
    
    /**
     * Validate item data
     * @param itemName Item name
     * @param unitPrice Unit price
     * @param stockQuantity Stock quantity
     * @return true if data is valid
     */
    boolean validateItemData(String itemName, BigDecimal unitPrice, Integer stockQuantity);
    
    /**
     * Delete item
     * @param itemId Item ID
     */
    void deleteItem(Long itemId);
    
    /**
     * Get items with pagination
     * @param offset Starting position
     * @param limit Maximum number of results
     * @return List of items
     */
    List<Item> getItemsWithPagination(int offset, int limit);
    
    /**
     * Count total items
     * @return Total number of items
     */
    long getTotalItemCount();
}

