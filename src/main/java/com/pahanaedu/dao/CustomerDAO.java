package com.pahanaedu.dao;

import com.pahanaedu.model.Customer;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Customer entity
 * Provides customer-specific database operations
 */
public interface CustomerDAO extends BaseDAO<Customer, String> {
    
    /**
     * Find customer by account number
     * @param accountNumber Customer account number
     * @return Optional containing customer if found
     */
    Optional<Customer> findByAccountNumber(String accountNumber);
    
    /**
     * Find customers by name (partial match, case-insensitive)
     * @param name Customer name or partial name
     * @return List of matching customers
     */
    List<Customer> findByNameContaining(String name);
    
    /**
     * Find customers by telephone number
     * @param telephoneNumber Telephone number
     * @return List of customers with matching telephone number
     */
    List<Customer> findByTelephoneNumber(String telephoneNumber);
    
    /**
     * Find all active customers
     * @return List of active customers
     */
    List<Customer> findActiveCustomers();
    
    /**
     * Find customers registered within date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of customers registered in date range
     */
    List<Customer> findByRegistrationDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find customers with pending bills
     * @return List of customers who have pending bills
     */
    List<Customer> findCustomersWithPendingBills();
    
    /**
     * Generate next available account number
     * @return Next unique account number
     */
    String generateNextAccountNumber();
    
    /**
     * Check if account number exists
     * @param accountNumber Account number to check
     * @return true if account number exists
     */
    boolean accountNumberExists(String accountNumber);
    
    /**
     * Update customer active status
     * @param accountNumber Customer account number
     * @param active Active status
     * @return Updated customer
     */
    Customer updateActiveStatus(String accountNumber, boolean active);
    
    /**
     * Search customers by multiple criteria
     * @param name Customer name (partial match)
     * @param telephoneNumber Telephone number
     * @param active Active status filter (null for all)
     * @return List of matching customers
     */
    List<Customer> searchCustomers(String name, String telephoneNumber, Boolean active);
    
    /**
     * Get customer statistics
     * @return Array containing [totalCustomers, activeCustomers, customersWithPendingBills]
     */
    long[] getCustomerStatistics();
}

