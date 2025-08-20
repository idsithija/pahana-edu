package com.pahanaedu.service;

import com.pahanaedu.model.Customer;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Customer-related business operations
 * Provides customer management services
 */
public interface CustomerService {
    
    /**
     * Register a new customer
     * @param name Customer name
     * @param address Customer address
     * @param telephoneNumber Customer telephone number
     * @return Created customer with generated account number
     */
    Customer registerCustomer(String name, String address, String telephoneNumber);
    
    /**
     * Register a new customer with custom account number
     * @param accountNumber Custom account number (if null/empty, auto-generate)
     * @param name Customer name
     * @param address Customer address
     * @param telephoneNumber Customer telephone number
     * @return Created customer
     */
    Customer registerCustomerWithAccount(String accountNumber, String name, String address, String telephoneNumber);
    
    /**
     * Update customer information
     * @param customer Customer to update
     * @return Updated customer
     */
    Customer updateCustomer(Customer customer);
    
    /**
     * Find customer by account number
     * @param accountNumber Customer account number
     * @return Optional containing customer if found
     */
    Optional<Customer> findByAccountNumber(String accountNumber);
    
    /**
     * Search customers by name
     * @param name Customer name (partial match)
     * @return List of matching customers
     */
    List<Customer> searchByName(String name);
    
    /**
     * Find customer by telephone number
     * @param telephoneNumber Telephone number
     * @return List of customers with matching telephone number
     */
    List<Customer> findByTelephoneNumber(String telephoneNumber);
    
    /**
     * Get all active customers
     * @return List of active customers
     */
    List<Customer> getActiveCustomers();
    
    /**
     * Get all customers
     * @return List of all customers
     */
    List<Customer> getAllCustomers();
    
    /**
     * Activate or deactivate customer
     * @param accountNumber Customer account number
     * @param active Active status
     * @return Updated customer
     */
    Customer updateActiveStatus(String accountNumber, boolean active);
    
    /**
     * Get customers registered within date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of customers registered in date range
     */
    List<Customer> getCustomersRegisteredBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get customers with pending bills
     * @return List of customers with pending bills
     */
    List<Customer> getCustomersWithPendingBills();
    
    /**
     * Search customers by multiple criteria
     * @param name Customer name (partial match)
     * @param telephoneNumber Telephone number
     * @param active Active status filter
     * @return List of matching customers
     */
    List<Customer> searchCustomers(String name, String telephoneNumber, Boolean active);
    
    /**
     * Get customer statistics
     * @return Array containing [totalCustomers, activeCustomers, customersWithPendingBills]
     */
    long[] getCustomerStatistics();
    
    /**
     * Validate customer data
     * @param name Customer name
     * @param telephoneNumber Telephone number
     * @return true if data is valid
     */
    boolean validateCustomerData(String name, String telephoneNumber);
    
    /**
     * Check if account number exists
     * @param accountNumber Account number to check
     * @return true if account number exists
     */
    boolean accountNumberExists(String accountNumber);
    
    /**
     * Delete customer (soft delete - mark as inactive)
     * @param accountNumber Customer account number
     */
    void deleteCustomer(String accountNumber);
    
    /**
     * Get customers with pagination
     * @param offset Starting position
     * @param limit Maximum number of results
     * @return List of customers
     */
    List<Customer> getCustomersWithPagination(int offset, int limit);
    
    /**
     * Count total customers
     * @return Total number of customers
     */
    long getTotalCustomerCount();
}

