package com.pahanaedu.dao.impl;

import com.pahanaedu.dao.BillItemDAO;
import com.pahanaedu.model.Bill;
import com.pahanaedu.model.BillItem;
import com.pahanaedu.model.Item;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of BillItemDAO interface
 * Provides bill item-specific database operations using JPA
 */
@Stateless
public class BillItemDAOImpl extends BaseDAOImpl<BillItem, Long> implements BillItemDAO {
    
    @Override
    public Optional<BillItem> findByBillItemId(Long billItemId) {
        return findById(billItemId);
    }
    
    @Override
    public List<BillItem> findByBill(Bill bill) {
        TypedQuery<BillItem> query = createQuery(
            "SELECT bi FROM BillItem bi WHERE bi.bill = :bill ORDER BY bi.item.itemName"
        );
        query.setParameter("bill", bill);
        return query.getResultList();
    }
    
    @Override
    public List<BillItem> findByBillId(Long billId) {
        TypedQuery<BillItem> query = createQuery(
            "SELECT bi FROM BillItem bi WHERE bi.bill.billId = :billId ORDER BY bi.item.itemName"
        );
        query.setParameter("billId", billId);
        return query.getResultList();
    }
    
    @Override
    public List<BillItem> findByItem(Item item) {
        TypedQuery<BillItem> query = createQuery(
            "SELECT bi FROM BillItem bi WHERE bi.item = :item ORDER BY bi.bill.billDate DESC"
        );
        query.setParameter("item", item);
        return query.getResultList();
    }
    
    @Override
    public List<BillItem> findByItemId(Long itemId) {
        TypedQuery<BillItem> query = createQuery(
            "SELECT bi FROM BillItem bi WHERE bi.item.itemId = :itemId ORDER BY bi.bill.billDate DESC"
        );
        query.setParameter("itemId", itemId);
        return query.getResultList();
    }
    
    @Override
    public Optional<BillItem> findByBillAndItem(Bill bill, Item item) {
        try {
            TypedQuery<BillItem> query = createQuery(
                "SELECT bi FROM BillItem bi WHERE bi.bill = :bill AND bi.item = :item"
            );
            query.setParameter("bill", bill);
            query.setParameter("item", item);
            
            BillItem billItem = query.getSingleResult();
            return Optional.of(billItem);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public int calculateTotalQuantitySold(Long itemId) {
        TypedQuery<Long> query = getEntityManager().createQuery(
            "SELECT COALESCE(SUM(bi.quantity), 0) FROM BillItem bi WHERE bi.item.itemId = :itemId", 
            Long.class
        );
        query.setParameter("itemId", itemId);
        Long result = query.getSingleResult();
        return result != null ? result.intValue() : 0;
    }
    
    @Override
    public List<Object[]> getMostPopularItems(int limit) {
        TypedQuery<Object[]> query = getEntityManager().createQuery(
            "SELECT bi.item, SUM(bi.quantity) " +
            "FROM BillItem bi " +
            "GROUP BY bi.item " +
            "ORDER BY SUM(bi.quantity) DESC", 
            Object[].class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    @Override
    public Object[] getItemSalesStatistics(Long itemId) {
        // Total quantity sold
        TypedQuery<Long> quantityQuery = getEntityManager().createQuery(
            "SELECT COALESCE(SUM(bi.quantity), 0) FROM BillItem bi WHERE bi.item.itemId = :itemId", 
            Long.class
        );
        quantityQuery.setParameter("itemId", itemId);
        Long totalQuantity = quantityQuery.getSingleResult();
        
        // Total revenue
        TypedQuery<java.math.BigDecimal> revenueQuery = getEntityManager().createQuery(
            "SELECT COALESCE(SUM(bi.totalPrice), 0) FROM BillItem bi WHERE bi.item.itemId = :itemId", 
            java.math.BigDecimal.class
        );
        revenueQuery.setParameter("itemId", itemId);
        java.math.BigDecimal totalRevenue = revenueQuery.getSingleResult();
        
        // Number of bills
        TypedQuery<Long> billsQuery = getEntityManager().createQuery(
            "SELECT COUNT(DISTINCT bi.bill) FROM BillItem bi WHERE bi.item.itemId = :itemId", 
            Long.class
        );
        billsQuery.setParameter("itemId", itemId);
        Long numberOfBills = billsQuery.getSingleResult();
        
        return new Object[]{
            totalQuantity != null ? totalQuantity : 0L,
            totalRevenue != null ? totalRevenue : java.math.BigDecimal.ZERO,
            numberOfBills != null ? numberOfBills : 0L
        };
    }
    
    @Override
    public void deleteByBillId(Long billId) {
        javax.persistence.Query query = getEntityManager().createQuery(
            "DELETE FROM BillItem bi WHERE bi.bill.billId = :billId"
        );
        query.setParameter("billId", billId);
        query.executeUpdate();
    }
    
    @Override
    public BillItem updateQuantity(Long billItemId, int newQuantity) {
        Optional<BillItem> billItemOpt = findByBillItemId(billItemId);
        if (billItemOpt.isPresent()) {
            BillItem billItem = billItemOpt.get();
            billItem.updateQuantity(newQuantity);
            return update(billItem);
        }
        throw new IllegalArgumentException("BillItem not found: " + billItemId);
    }
}

