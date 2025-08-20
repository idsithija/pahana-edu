package com.pahanaedu.dao;

import com.pahanaedu.model.Bill;
import com.pahanaedu.model.Customer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Bill entity
 * Provides bill-specific database operations
 */
public interface BillDAO extends BaseDAO<Bill, Long> {
    
    /**
     * Find bill by ID
     * @param billId Bill ID
     * @return Optional containing bill if found
     */
    Optional<Bill> findByBillId(Long billId);
    
    /**
     * Find bills by customer
     * @param customer Customer
     * @return List of bills for the customer
     */
    List<Bill> findByCustomer(Customer customer);
    
    /**
     * Find bills by customer account number
     * @param accountNumber Customer account number
     * @return List of bills for the customer
     */
    List<Bill> findByCustomerAccountNumber(String accountNumber);
    
    /**
     * Find bills by status
     * @param status Bill status
     * @return List of bills with specified status
     */
    List<Bill> findByStatus(Bill.BillStatus status);
    
    /**
     * Find bills within date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of bills within date range
     */
    List<Bill> findByBillDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find bills by total amount range
     * @param minAmount Minimum amount (inclusive)
     * @param maxAmount Maximum amount (inclusive)
     * @return List of bills within amount range
     */
    List<Bill> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * Find pending bills
     * @return List of pending bills
     */
    List<Bill> findPendingBills();
    
    /**
     * Find paid bills
     * @return List of paid bills
     */
    List<Bill> findPaidBills();
    
    /**
     * Find cancelled bills
     * @return List of cancelled bills
     */
    List<Bill> findCancelledBills();
    
    /**
     * Find bills for today
     * @return List of bills created today
     */
    List<Bill> findTodaysBills();
    
    /**
     * Find bills for current month
     * @return List of bills created in current month
     */
    List<Bill> findCurrentMonthBills();
    
    /**
     * Calculate total revenue for date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Total revenue for the period
     */
    BigDecimal calculateRevenueForPeriod(LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate total revenue for customer
     * @param accountNumber Customer account number
     * @return Total revenue from customer
     */
    BigDecimal calculateCustomerRevenue(String accountNumber);
    
    /**
     * Get bill statistics
     * @return Array containing [totalBills, pendingBills, paidBills, cancelledBills]
     */
    long[] getBillStatistics();
    
    /**
     * Get daily revenue for date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of [date, revenue] pairs
     */
    List<Object[]> getDailyRevenue(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get monthly revenue for year
     * @param year Year
     * @return List of [month, revenue] pairs
     */
    List<Object[]> getMonthlyRevenue(int year);
    
    /**
     * Find top customers by revenue
     * @param limit Maximum number of customers to return
     * @return List of [customer, totalRevenue] pairs
     */
    List<Object[]> getTopCustomersByRevenue(int limit);
    
    /**
     * Search bills by multiple criteria
     * @param customerName Customer name (partial match)
     * @param status Bill status filter
     * @param startDate Start date filter
     * @param endDate End date filter
     * @param minAmount Minimum amount filter
     * @param maxAmount Maximum amount filter
     * @return List of matching bills
     */
    List<Bill> searchBills(String customerName, Bill.BillStatus status, 
                          LocalDate startDate, LocalDate endDate, 
                          BigDecimal minAmount, BigDecimal maxAmount);
}

