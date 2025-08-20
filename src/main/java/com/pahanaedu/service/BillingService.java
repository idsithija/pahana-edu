package com.pahanaedu.service;

import com.pahanaedu.model.Bill;
import com.pahanaedu.model.Customer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Billing-related business operations
 * Provides billing and invoice management services
 */
public interface BillingService {
    
    /**
     * Create a new bill for a customer
     * @param customer Customer
     * @return Created bill
     */
    Bill createBill(Customer customer);
    
    /**
     * Create a new bill by customer account number
     * @param accountNumber Customer account number
     * @return Created bill
     */
    Bill createBill(String accountNumber);
    
    /**
     * Add item to bill
     * @param billId Bill ID
     * @param itemId Item ID
     * @param quantity Quantity
     * @return Updated bill
     */
    Bill addItemToBill(Long billId, Long itemId, int quantity);
    
    /**
     * Remove item from bill
     * @param billId Bill ID
     * @param itemId Item ID
     * @return Updated bill
     */
    Bill removeItemFromBill(Long billId, Long itemId);
    
    /**
     * Update item quantity in bill
     * @param billId Bill ID
     * @param itemId Item ID
     * @param newQuantity New quantity
     * @return Updated bill
     */
    Bill updateItemQuantityInBill(Long billId, Long itemId, int newQuantity);
    
    /**
     * Calculate bill total
     * @param billId Bill ID
     * @return Updated bill with calculated total
     */
    Bill calculateBillTotal(Long billId);
    
    /**
     * Mark bill as paid
     * @param billId Bill ID
     * @return Updated bill
     */
    Bill markBillAsPaid(Long billId);
    
    /**
     * Cancel bill
     * @param billId Bill ID
     * @return Updated bill
     */
    Bill cancelBill(Long billId);
    
    /**
     * Find bill by ID
     * @param billId Bill ID
     * @return Optional containing bill if found
     */
    Optional<Bill> findById(Long billId);
    
    /**
     * Get bills by customer
     * @param accountNumber Customer account number
     * @return List of customer bills
     */
    List<Bill> getBillsByCustomer(String accountNumber);
    
    /**
     * Get pending bills
     * @return List of pending bills
     */
    List<Bill> getPendingBills();
    
    /**
     * Get paid bills
     * @return List of paid bills
     */
    List<Bill> getPaidBills();
    
    /**
     * Get cancelled bills
     * @return List of cancelled bills
     */
    List<Bill> getCancelledBills();
    
    /**
     * Get today's bills
     * @return List of bills created today
     */
    List<Bill> getTodaysBills();
    
    /**
     * Get current month bills
     * @return List of bills created in current month
     */
    List<Bill> getCurrentMonthBills();
    
    /**
     * Get bills within date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of bills in date range
     */
    List<Bill> getBillsInDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate revenue for period
     * @param startDate Start date
     * @param endDate End date
     * @return Total revenue for period
     */
    BigDecimal calculateRevenueForPeriod(LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate customer revenue
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
     * Get top customers by revenue
     * @param limit Maximum number of customers
     * @return List of [customer, totalRevenue] pairs
     */
    List<Object[]> getTopCustomersByRevenue(int limit);
    
    /**
     * Search bills by criteria
     * @param customerName Customer name (partial match)
     * @param status Bill status
     * @param startDate Start date filter
     * @param endDate End date filter
     * @param minAmount Minimum amount filter
     * @param maxAmount Maximum amount filter
     * @return List of matching bills
     */
    List<Bill> searchBills(String customerName, Bill.BillStatus status, 
                          LocalDate startDate, LocalDate endDate, 
                          BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * Get all bills
     * @return List of all bills
     */
    List<Bill> getAllBills();
    
    /**
     * Delete bill
     * @param billId Bill ID
     */
    void deleteBill(Long billId);
    
    /**
     * Get bills with pagination
     * @param offset Starting position
     * @param limit Maximum number of results
     * @return List of bills
     */
    List<Bill> getBillsWithPagination(int offset, int limit);
    
    /**
     * Count total bills
     * @return Total number of bills
     */
    long getTotalBillCount();
}

