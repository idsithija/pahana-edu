package com.pahanaedu.dao.impl;

import com.pahanaedu.dao.BillDAO;
import com.pahanaedu.model.Bill;
import com.pahanaedu.model.Customer;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of BillDAO interface
 * Provides bill-specific database operations using JPA
 */
@Stateless
public class BillDAOImpl extends BaseDAOImpl<Bill, Long> implements BillDAO {
    
    @Override
    public List<Bill> findAll() {
        // First get distinct bills with customers
        TypedQuery<Bill> query = createQuery(
            "SELECT DISTINCT b FROM Bill b LEFT JOIN FETCH b.customer ORDER BY b.billDate DESC"
        );
        List<Bill> bills = query.getResultList();

        // Then fetch bill items for each bill in a separate query to avoid Cartesian product
        if (!bills.isEmpty()) {
            TypedQuery<Bill> itemsQuery = createQuery(
                "SELECT DISTINCT b FROM Bill b LEFT JOIN FETCH b.billItems bi LEFT JOIN FETCH bi.item WHERE b IN :bills"
            );
            itemsQuery.setParameter("bills", bills);
            itemsQuery.getResultList(); // This will populate the billItems collections
        }

        return bills;
    }
    
    @Override
    public Optional<Bill> findByBillId(Long billId) {
        // First get the bill with customer
        TypedQuery<Bill> query = createQuery(
            "SELECT b FROM Bill b LEFT JOIN FETCH b.customer WHERE b.billId = :billId"
        );
        query.setParameter("billId", billId);
        List<Bill> results = query.getResultList();

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Bill bill = results.get(0);

        // Then fetch bill items for this specific bill
        TypedQuery<Bill> itemsQuery = createQuery(
            "SELECT b FROM Bill b LEFT JOIN FETCH b.billItems bi LEFT JOIN FETCH bi.item WHERE b.billId = :billId"
        );
        itemsQuery.setParameter("billId", billId);
        itemsQuery.getResultList(); // This will populate the billItems collection

        return Optional.of(bill);
    }
    
    @Override
    public List<Bill> findByCustomer(Customer customer) {
        TypedQuery<Bill> query = createQuery(
            "SELECT DISTINCT b FROM Bill b LEFT JOIN FETCH b.customer WHERE b.customer = :customer ORDER BY b.billDate DESC"
        );
        query.setParameter("customer", customer);
        return query.getResultList();
    }
    
    @Override
    public List<Bill> findByCustomerAccountNumber(String accountNumber) {
        TypedQuery<Bill> query = createQuery(
            "SELECT DISTINCT b FROM Bill b LEFT JOIN FETCH b.customer WHERE b.customer.accountNumber = :accountNumber ORDER BY b.billDate DESC"
        );
        query.setParameter("accountNumber", accountNumber);
        return query.getResultList();
    }
    
    @Override
    public List<Bill> findByStatus(Bill.BillStatus status) {
        TypedQuery<Bill> query = createQuery(
            "SELECT DISTINCT b FROM Bill b LEFT JOIN FETCH b.customer WHERE b.status = :status ORDER BY b.billDate DESC"
        );
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    @Override
    public List<Bill> findByBillDateBetween(LocalDate startDate, LocalDate endDate) {
        TypedQuery<Bill> query = createQuery(
            "SELECT b FROM Bill b WHERE b.billDate BETWEEN :startDate AND :endDate ORDER BY b.billDate DESC"
        );
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    @Override
    public List<Bill> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        TypedQuery<Bill> query = createQuery(
            "SELECT b FROM Bill b WHERE b.totalAmount BETWEEN :minAmount AND :maxAmount ORDER BY b.totalAmount DESC"
        );
        query.setParameter("minAmount", minAmount);
        query.setParameter("maxAmount", maxAmount);
        return query.getResultList();
    }
    
    @Override
    public List<Bill> findPendingBills() {
        return findByStatus(Bill.BillStatus.PENDING);
    }
    
    @Override
    public List<Bill> findPaidBills() {
        return findByStatus(Bill.BillStatus.PAID);
    }
    
    @Override
    public List<Bill> findCancelledBills() {
        return findByStatus(Bill.BillStatus.CANCELLED);
    }
    
    @Override
    public List<Bill> findTodaysBills() {
        LocalDate today = LocalDate.now();
        return findByBillDateBetween(today, today);
    }
    
    @Override
    public List<Bill> findCurrentMonthBills() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        return findByBillDateBetween(startOfMonth, endOfMonth);
    }
    
    @Override
    public BigDecimal calculateRevenueForPeriod(LocalDate startDate, LocalDate endDate) {
        TypedQuery<BigDecimal> query = getEntityManager().createQuery(
            "SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE b.billDate BETWEEN :startDate AND :endDate AND b.status = 'PAID'", 
            BigDecimal.class
        );
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal calculateCustomerRevenue(String accountNumber) {
        TypedQuery<BigDecimal> query = getEntityManager().createQuery(
            "SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE b.customer.accountNumber = :accountNumber AND b.status = 'PAID'", 
            BigDecimal.class
        );
        query.setParameter("accountNumber", accountNumber);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
    
    @Override
    public long[] getBillStatistics() {
        // Total bills
        TypedQuery<Long> totalQuery = getEntityManager().createQuery(
            "SELECT COUNT(b) FROM Bill b", Long.class
        );
        long totalBills = totalQuery.getSingleResult();
        
        // Pending bills
        TypedQuery<Long> pendingQuery = getEntityManager().createQuery(
            "SELECT COUNT(b) FROM Bill b WHERE b.status = 'PENDING'", Long.class
        );
        long pendingBills = pendingQuery.getSingleResult();
        
        // Paid bills
        TypedQuery<Long> paidQuery = getEntityManager().createQuery(
            "SELECT COUNT(b) FROM Bill b WHERE b.status = 'PAID'", Long.class
        );
        long paidBills = paidQuery.getSingleResult();
        
        // Cancelled bills
        TypedQuery<Long> cancelledQuery = getEntityManager().createQuery(
            "SELECT COUNT(b) FROM Bill b WHERE b.status = 'CANCELLED'", Long.class
        );
        long cancelledBills = cancelledQuery.getSingleResult();
        
        return new long[]{totalBills, pendingBills, paidBills, cancelledBills};
    }
    
    @Override
    public List<Object[]> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        TypedQuery<Object[]> query = getEntityManager().createQuery(
            "SELECT b.billDate, COALESCE(SUM(b.totalAmount), 0) " +
            "FROM Bill b " +
            "WHERE b.billDate BETWEEN :startDate AND :endDate AND b.status = 'PAID' " +
            "GROUP BY b.billDate " +
            "ORDER BY b.billDate", 
            Object[].class
        );
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    @Override
    public List<Object[]> getMonthlyRevenue(int year) {
        TypedQuery<Object[]> query = getEntityManager().createQuery(
            "SELECT EXTRACT(MONTH FROM b.billDate), COALESCE(SUM(b.totalAmount), 0) " +
            "FROM Bill b " +
            "WHERE EXTRACT(YEAR FROM b.billDate) = :year AND b.status = 'PAID' " +
            "GROUP BY EXTRACT(MONTH FROM b.billDate) " +
            "ORDER BY EXTRACT(MONTH FROM b.billDate)", 
            Object[].class
        );
        query.setParameter("year", year);
        return query.getResultList();
    }
    
    @Override
    public List<Object[]> getTopCustomersByRevenue(int limit) {
        TypedQuery<Object[]> query = getEntityManager().createQuery(
            "SELECT b.customer, COALESCE(SUM(b.totalAmount), 0) " +
            "FROM Bill b " +
            "WHERE b.status = 'PAID' " +
            "GROUP BY b.customer " +
            "ORDER BY SUM(b.totalAmount) DESC", 
            Object[].class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    @Override
    public List<Bill> searchBills(String customerName, Bill.BillStatus status, 
                                 LocalDate startDate, LocalDate endDate, 
                                 BigDecimal minAmount, BigDecimal maxAmount) {
        StringBuilder jpql = new StringBuilder("SELECT b FROM Bill b WHERE 1=1");
        
        if (customerName != null && !customerName.trim().isEmpty()) {
            jpql.append(" AND LOWER(b.customer.name) LIKE LOWER(:customerName)");
        }
        if (status != null) {
            jpql.append(" AND b.status = :status");
        }
        if (startDate != null) {
            jpql.append(" AND b.billDate >= :startDate");
        }
        if (endDate != null) {
            jpql.append(" AND b.billDate <= :endDate");
        }
        if (minAmount != null) {
            jpql.append(" AND b.totalAmount >= :minAmount");
        }
        if (maxAmount != null) {
            jpql.append(" AND b.totalAmount <= :maxAmount");
        }
        
        jpql.append(" ORDER BY b.billDate DESC");
        
        TypedQuery<Bill> query = createQuery(jpql.toString());
        
        if (customerName != null && !customerName.trim().isEmpty()) {
            query.setParameter("customerName", "%" + customerName.trim() + "%");
        }
        if (status != null) {
            query.setParameter("status", status);
        }
        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }
        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }
        if (minAmount != null) {
            query.setParameter("minAmount", minAmount);
        }
        if (maxAmount != null) {
            query.setParameter("maxAmount", maxAmount);
        }
        
        return query.getResultList();
    }
}
