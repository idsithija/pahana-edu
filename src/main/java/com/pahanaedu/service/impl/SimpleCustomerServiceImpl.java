package com.pahanaedu.service.impl;

import com.pahanaedu.dao.CustomerDAO;
import com.pahanaedu.dao.impl.CustomerDAOImpl;
import com.pahanaedu.model.Customer;
import com.pahanaedu.service.CustomerService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Simple POJO implementation of CustomerService interface
 * For use in development environments without full EJB container
 */
public class SimpleCustomerServiceImpl implements CustomerService {
    
    private static final Logger logger = Logger.getLogger(SimpleCustomerServiceImpl.class.getName());
    
    private final CustomerDAO customerDAO;
    
    public SimpleCustomerServiceImpl() {
        this.customerDAO = new CustomerDAOImpl();
    }
    
    @Override
    public Customer registerCustomer(String name, String address, String telephoneNumber) {
        return registerCustomerWithAccount(null, name, address, telephoneNumber);
    }
    
    @Override
    public Customer registerCustomerWithAccount(String accountNumber, String name, String address, String telephoneNumber) {
        if (!validateCustomerData(name, telephoneNumber)) {
            throw new IllegalArgumentException("Invalid customer data");
        }
        
        // Use provided account number or generate one
        String finalAccountNumber;
        if (accountNumber != null && !accountNumber.trim().isEmpty()) {
            finalAccountNumber = accountNumber.trim();
            // Check if account number already exists
            if (customerDAO.accountNumberExists(finalAccountNumber)) {
                throw new IllegalArgumentException("Account number already exists: " + finalAccountNumber);
            }
        } else {
            finalAccountNumber = customerDAO.generateNextAccountNumber();
        }
        
        Customer customer = new Customer(finalAccountNumber, name.trim(), 
                                       address != null ? address.trim() : null, 
                                       telephoneNumber != null ? telephoneNumber.trim() : null);
        
        Customer savedCustomer = customerDAO.save(customer);
        logger.info("Customer registered successfully: " + finalAccountNumber + " - " + name);
        return savedCustomer;
    }
    
    @Override
    public Customer updateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (!validateCustomerData(customer.getName(), customer.getTelephoneNumber())) {
            throw new IllegalArgumentException("Invalid customer data");
        }
        
        Customer updatedCustomer = customerDAO.update(customer);
        logger.info("Customer updated successfully: " + customer.getAccountNumber());
        return updatedCustomer;
    }
    
    @Override
    public Optional<Customer> findByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        return customerDAO.findByAccountNumber(accountNumber.trim());
    }
    
    @Override
    public List<Customer> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerDAO.findByNameContaining(name.trim());
    }
    
    @Override
    public List<Customer> findByTelephoneNumber(String telephoneNumber) {
        if (telephoneNumber == null || telephoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Telephone number cannot be empty");
        }
        return customerDAO.findByTelephoneNumber(telephoneNumber.trim());
    }
    
    @Override
    public List<Customer> getActiveCustomers() {
        return customerDAO.findActiveCustomers();
    }
    
    @Override
    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }
    
    @Override
    public Customer updateActiveStatus(String accountNumber, boolean active) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        
        Customer updatedCustomer = customerDAO.updateActiveStatus(accountNumber.trim(), active);
        logger.info("Customer active status updated: " + accountNumber + " -> " + active);
        return updatedCustomer;
    }
    
    @Override
    public List<Customer> getCustomersRegisteredBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        return customerDAO.findByRegistrationDateBetween(startDate, endDate);
    }
    
    @Override
    public List<Customer> getCustomersWithPendingBills() {
        return customerDAO.findCustomersWithPendingBills();
    }
    
    @Override
    public List<Customer> searchCustomers(String name, String telephoneNumber, Boolean active) {
        return customerDAO.searchCustomers(name, telephoneNumber, active);
    }
    
    @Override
    public long[] getCustomerStatistics() {
        return customerDAO.getCustomerStatistics();
    }
    
    @Override
    public boolean validateCustomerData(String name, String telephoneNumber) {
        // Name validation
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (name.trim().length() < 2 || name.trim().length() > 100) {
            return false;
        }
        
        // Telephone number validation (optional but if provided, must be valid)
        if (telephoneNumber != null && !telephoneNumber.trim().isEmpty()) {
            String cleanPhone = telephoneNumber.trim().replaceAll("[\\s\\-\\(\\)]", "");
            if (!cleanPhone.matches("^\\+?[0-9]{8,15}$")) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean accountNumberExists(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return false;
        }
        return customerDAO.accountNumberExists(accountNumber.trim());
    }
    
    @Override
    public void deleteCustomer(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        
        // Soft delete - mark as inactive instead of actual deletion
        updateActiveStatus(accountNumber.trim(), false);
        logger.info("Customer marked as inactive (soft deleted): " + accountNumber);
    }
    
    @Override
    public List<Customer> getCustomersWithPagination(int offset, int limit) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        
        return customerDAO.findWithPagination(offset, limit);
    }
    
    @Override
    public long getTotalCustomerCount() {
        return customerDAO.count();
    }
}