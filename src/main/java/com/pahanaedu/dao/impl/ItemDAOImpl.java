package com.pahanaedu.dao.impl;

import com.pahanaedu.dao.ItemDAO;
import com.pahanaedu.model.Item;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ItemDAO interface
 * Provides item-specific database operations using JPA
 */
@Stateless
public class ItemDAOImpl extends BaseDAOImpl<Item, Long> implements ItemDAO {
    
    @Override
    public Optional<Item> findByItemId(Long itemId) {
        return findById(itemId);
    }
    
    @Override
    public List<Item> findByItemNameContaining(String itemName) {
        TypedQuery<Item> query = createQuery(
            "SELECT i FROM Item i WHERE LOWER(i.itemName) LIKE LOWER(:itemName) ORDER BY i.itemName"
        );
        query.setParameter("itemName", "%" + itemName + "%");
        return query.getResultList();
    }
    
    @Override
    public List<Item> findByCategory(String category) {
        TypedQuery<Item> query = createQuery(
            "SELECT i FROM Item i WHERE LOWER(i.category) LIKE LOWER(:category) ORDER BY i.itemName"
        );
        query.setParameter("category", "%" + category + "%");
        return query.getResultList();
    }
    
    @Override
    public List<Item> findByStockQuantityGreaterThan(int minQuantity) {
        TypedQuery<Item> query = createQuery(
            "SELECT i FROM Item i WHERE i.stockQuantity > :minQuantity ORDER BY i.itemName"
        );
        query.setParameter("minQuantity", minQuantity);
        return query.getResultList();
    }
    
    @Override
    public List<Item> findOutOfStockItems() {
        TypedQuery<Item> query = createQuery(
            "SELECT i FROM Item i WHERE i.stockQuantity = 0 ORDER BY i.itemName"
        );
        return query.getResultList();
    }
    
    @Override
    public List<Item> findLowStockItems(int threshold) {
        TypedQuery<Item> query = createQuery(
            "SELECT i FROM Item i WHERE i.stockQuantity > 0 AND i.stockQuantity <= :threshold ORDER BY i.stockQuantity, i.itemName"
        );
        query.setParameter("threshold", threshold);
        return query.getResultList();
    }
    
    @Override
    public List<Item> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        TypedQuery<Item> query = createQuery(
            "SELECT i FROM Item i WHERE i.unitPrice BETWEEN :minPrice AND :maxPrice ORDER BY i.unitPrice"
        );
        query.setParameter("minPrice", minPrice);
        query.setParameter("maxPrice", maxPrice);
        return query.getResultList();
    }
    
    @Override
    public List<String> findAllCategories() {
        TypedQuery<String> query = getEntityManager().createQuery(
            "SELECT DISTINCT i.category FROM Item i WHERE i.category IS NOT NULL ORDER BY i.category", String.class
        );
        return query.getResultList();
    }
    
    @Override
    public Item updateStockQuantity(Long itemId, int newQuantity) {
        Optional<Item> itemOpt = findByItemId(itemId);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            item.setStockQuantity(newQuantity);
            return update(item);
        }
        throw new IllegalArgumentException("Item not found: " + itemId);
    }
    
    @Override
    public Item increaseStock(Long itemId, int quantity) {
        Optional<Item> itemOpt = findByItemId(itemId);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            item.increaseStock(quantity);
            return update(item);
        }
        throw new IllegalArgumentException("Item not found: " + itemId);
    }
    
    @Override
    public Item decreaseStock(Long itemId, int quantity) {
        Optional<Item> itemOpt = findByItemId(itemId);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            item.reduceStock(quantity);
            return update(item);
        }
        throw new IllegalArgumentException("Item not found: " + itemId);
    }
    
    @Override
    public List<Item> searchItems(String itemName, String category, BigDecimal minPrice, BigDecimal maxPrice, boolean inStockOnly) {
        StringBuilder jpql = new StringBuilder("SELECT i FROM Item i WHERE 1=1");
        
        if (itemName != null && !itemName.trim().isEmpty()) {
            jpql.append(" AND LOWER(i.itemName) LIKE LOWER(:itemName)");
        }
        if (category != null && !category.trim().isEmpty()) {
            jpql.append(" AND LOWER(i.category) LIKE LOWER(:category)");
        }
        if (minPrice != null) {
            jpql.append(" AND i.unitPrice >= :minPrice");
        }
        if (maxPrice != null) {
            jpql.append(" AND i.unitPrice <= :maxPrice");
        }
        if (inStockOnly) {
            jpql.append(" AND i.stockQuantity > 0");
        }
        
        jpql.append(" ORDER BY i.itemName");
        
        TypedQuery<Item> query = createQuery(jpql.toString());
        
        if (itemName != null && !itemName.trim().isEmpty()) {
            query.setParameter("itemName", "%" + itemName.trim() + "%");
        }
        if (category != null && !category.trim().isEmpty()) {
            query.setParameter("category", "%" + category.trim() + "%");
        }
        if (minPrice != null) {
            query.setParameter("minPrice", minPrice);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        
        return query.getResultList();
    }
    
    @Override
    public long[] getItemStatistics(int lowStockThreshold) {
        // Total items
        TypedQuery<Long> totalQuery = getEntityManager().createQuery(
            "SELECT COUNT(i) FROM Item i", Long.class
        );
        long totalItems = totalQuery.getSingleResult();
        
        // Items in stock
        TypedQuery<Long> inStockQuery = getEntityManager().createQuery(
            "SELECT COUNT(i) FROM Item i WHERE i.stockQuantity > 0", Long.class
        );
        long itemsInStock = inStockQuery.getSingleResult();
        
        // Out of stock items
        TypedQuery<Long> outOfStockQuery = getEntityManager().createQuery(
            "SELECT COUNT(i) FROM Item i WHERE i.stockQuantity = 0", Long.class
        );
        long outOfStockItems = outOfStockQuery.getSingleResult();
        
        // Low stock items
        TypedQuery<Long> lowStockQuery = getEntityManager().createQuery(
            "SELECT COUNT(i) FROM Item i WHERE i.stockQuantity > 0 AND i.stockQuantity <= :threshold", Long.class
        );
        lowStockQuery.setParameter("threshold", lowStockThreshold);
        long lowStockItems = lowStockQuery.getSingleResult();
        
        return new long[]{totalItems, itemsInStock, outOfStockItems, lowStockItems};
    }
    
    @Override
    public List<Item> getTopSellingItems(int limit) {
        TypedQuery<Item> query = createQuery(
            "SELECT i FROM Item i JOIN i.billItems bi GROUP BY i ORDER BY SUM(bi.quantity) DESC"
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }
}

