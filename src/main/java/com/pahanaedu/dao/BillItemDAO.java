package com.pahanaedu.dao;

import com.pahanaedu.model.Bill;
import com.pahanaedu.model.BillItem;
import com.pahanaedu.model.Item;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for BillItem entity
 * Provides bill item-specific database operations
 */
public interface BillItemDAO extends BaseDAO<BillItem, Long> {
    
    /**
     * Find bill item by ID
     * @param billItemId Bill item ID
     * @return Optional containing bill item if found
     */
    Optional<BillItem> findByBillItemId(Long billItemId);
    
    /**
     * Find bill items by bill
     * @param bill Bill
     * @return List of bill items for the bill
     */
    List<BillItem> findByBill(Bill bill);
    
    /**
     * Find bill items by bill ID
     * @param billId Bill ID
     * @return List of bill items for the bill
     */
    List<BillItem> findByBillId(Long billId);
    
    /**
     * Find bill items by item
     * @param item Item
     * @return List of bill items containing the item
     */
    List<BillItem> findByItem(Item item);
    
    /**
     * Find bill items by item ID
     * @param itemId Item ID
     * @return List of bill items containing the item
     */
    List<BillItem> findByItemId(Long itemId);
    
    /**
     * Find bill item by bill and item
     * @param bill Bill
     * @param item Item
     * @return Optional containing bill item if found
     */
    Optional<BillItem> findByBillAndItem(Bill bill, Item item);
    
    /**
     * Calculate total quantity sold for an item
     * @param itemId Item ID
     * @return Total quantity sold
     */
    int calculateTotalQuantitySold(Long itemId);
    
    /**
     * Get most popular items (by quantity sold)
     * @param limit Maximum number of items to return
     * @return List of [item, totalQuantitySold] pairs
     */
    List<Object[]> getMostPopularItems(int limit);
    
    /**
     * Get item sales statistics
     * @param itemId Item ID
     * @return Array containing [totalQuantitySold, totalRevenue, numberOfBills]
     */
    Object[] getItemSalesStatistics(Long itemId);
    
    /**
     * Delete all bill items for a bill
     * @param billId Bill ID
     */
    void deleteByBillId(Long billId);
    
    /**
     * Update bill item quantity
     * @param billItemId Bill item ID
     * @param newQuantity New quantity
     * @return Updated bill item
     */
    BillItem updateQuantity(Long billItemId, int newQuantity);
}

