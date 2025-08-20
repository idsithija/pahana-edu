package com.pahanaedu.service.impl;

import com.pahanaedu.dao.ItemDAO;
import com.pahanaedu.dao.impl.ItemDAOImpl;
import com.pahanaedu.model.Item;
import com.pahanaedu.service.ItemService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Simple POJO implementation of ItemService interface
 * For use in development environments without full EJB container
 */
public class SimpleItemServiceImpl implements ItemService {
    
    private static final Logger logger = Logger.getLogger(SimpleItemServiceImpl.class.getName());
    
    private final ItemDAO itemDAO;
    
    public SimpleItemServiceImpl() {
        this.itemDAO = new ItemDAOImpl();
    }
    
    @Override
    public Item createItem(String itemName, String description, BigDecimal unitPrice, Integer stockQuantity, String category) {
        if (!validateItemData(itemName, unitPrice, stockQuantity)) {
            throw new IllegalArgumentException("Invalid item data");
        }
        
        Item item = new Item(itemName.trim(), 
                           description != null ? description.trim() : null, 
                           unitPrice, 
                           stockQuantity, 
                           category != null ? category.trim() : null);
        
        Item savedItem = itemDAO.save(item);
        logger.info("Item created successfully: " + itemName + " (ID: " + savedItem.getItemId() + ")");
        return savedItem;
    }
    
    @Override
    public Item updateItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (!validateItemData(item.getItemName(), item.getUnitPrice(), item.getStockQuantity())) {
            throw new IllegalArgumentException("Invalid item data");
        }
        
        Item updatedItem = itemDAO.update(item);
        logger.info("Item updated successfully: " + item.getItemName() + " (ID: " + item.getItemId() + ")");
        return updatedItem;
    }
    
    @Override
    public Optional<Item> findById(Long itemId) {
        if (itemId == null) {
            return Optional.empty();
        }
        return itemDAO.findByItemId(itemId);
    }
    
    @Override
    public List<Item> searchByName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return getAllItems();
        }
        return itemDAO.findByItemNameContaining(itemName.trim());
    }
    
    @Override
    public List<Item> findByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        return itemDAO.findByCategory(category.trim());
    }
    
    @Override
    public List<Item> getAllItems() {
        return itemDAO.findAll();
    }
    
    @Override
    public List<Item> getItemsInStock() {
        return itemDAO.findByStockQuantityGreaterThan(0);
    }
    
    @Override
    public List<Item> getOutOfStockItems() {
        return itemDAO.findOutOfStockItems();
    }
    
    @Override
    public List<Item> getLowStockItems(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
        return itemDAO.findLowStockItems(threshold);
    }
    
    @Override
    public Item updateStockQuantity(Long itemId, int newQuantity) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        
        Item updatedItem = itemDAO.updateStockQuantity(itemId, newQuantity);
        logger.info("Stock quantity updated for item ID " + itemId + ": " + newQuantity);
        return updatedItem;
    }
    
    @Override
    public Item increaseStock(Long itemId, int quantity) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        Item updatedItem = itemDAO.increaseStock(itemId, quantity);
        logger.info("Stock increased for item ID " + itemId + " by " + quantity);
        return updatedItem;
    }
    
    @Override
    public Item decreaseStock(Long itemId, int quantity) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        Item updatedItem = itemDAO.decreaseStock(itemId, quantity);
        logger.info("Stock decreased for item ID " + itemId + " by " + quantity);
        return updatedItem;
    }
    
    @Override
    public List<Item> searchItems(String itemName, String category, BigDecimal minPrice, BigDecimal maxPrice, boolean inStockOnly) {
        return itemDAO.searchItems(itemName, category, minPrice, maxPrice, inStockOnly);
    }
    
    @Override
    public List<String> getAllCategories() {
        return itemDAO.findAllCategories();
    }
    
    @Override
    public long[] getItemStatistics(int lowStockThreshold) {
        if (lowStockThreshold < 0) {
            throw new IllegalArgumentException("Low stock threshold cannot be negative");
        }
        return itemDAO.getItemStatistics(lowStockThreshold);
    }
    
    @Override
    public List<Item> getTopSellingItems(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        return itemDAO.getTopSellingItems(limit);
    }
    
    @Override
    public boolean validateItemData(String itemName, BigDecimal unitPrice, Integer stockQuantity) {
        // Item name validation
        if (itemName == null || itemName.trim().isEmpty()) {
            return false;
        }
        if (itemName.trim().length() < 2 || itemName.trim().length() > 200) {
            return false;
        }
        
        // Unit price validation
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        
        // Stock quantity validation
        if (stockQuantity == null || stockQuantity < 0) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void deleteItem(Long itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        
        itemDAO.deleteById(itemId);
        logger.info("Item deleted successfully: ID " + itemId);
    }
    
    @Override
    public List<Item> getItemsWithPagination(int offset, int limit) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        
        return itemDAO.findWithPagination(offset, limit);
    }
    
    @Override
    public long getTotalItemCount() {
        return itemDAO.count();
    }
}