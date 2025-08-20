package com.pahanaedu.util;

import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.dao.CustomerDAO;
import com.pahanaedu.dao.impl.UserDAOImpl;
import com.pahanaedu.dao.impl.CustomerDAOImpl;
import com.pahanaedu.service.UserService;
import com.pahanaedu.service.CustomerService;
import com.pahanaedu.service.ItemService;
import com.pahanaedu.service.BillingService;
import com.pahanaedu.service.impl.UserServiceImpl;
import com.pahanaedu.service.impl.SimpleCustomerServiceImpl;
import com.pahanaedu.service.impl.SimpleItemServiceImpl;
import com.pahanaedu.service.impl.SimpleBillingServiceImpl;

/**
 * Simple service factory to replace EJB dependency injection
 * Provides singleton instances of services and DAOs
 */
public class ServiceFactory {
    
    // Singleton instances
    private static UserDAO userDAO;
    private static CustomerDAO customerDAO;
    private static UserService userService;
    private static CustomerService customerService;
    private static ItemService itemService;
    private static BillingService billingService;
    
    /**
     * Get UserDAO instance
     */
    public static synchronized UserDAO getUserDAO() {
        if (userDAO == null) {
            userDAO = new UserDAOImpl();
        }
        return userDAO;
    }
    
    /**
     * Get CustomerDAO instance  
     */
    public static synchronized CustomerDAO getCustomerDAO() {
        if (customerDAO == null) {
            customerDAO = new CustomerDAOImpl();
        }
        return customerDAO;
    }
    
    /**
     * Get UserService instance
     */
    public static synchronized UserService getUserService() {
        if (userService == null) {
            userService = new UserServiceImpl();
        }
        return userService;
    }
    
    /**
     * Get CustomerService instance
     */
    public static synchronized CustomerService getCustomerService() {
        if (customerService == null) {
            customerService = new SimpleCustomerServiceImpl();
        }
        return customerService;
    }
    
    /**
     * Get ItemService instance
     */
    public static synchronized ItemService getItemService() {
        if (itemService == null) {
            itemService = new SimpleItemServiceImpl();
        }
        return itemService;
    }
    
    /**
     * Get BillingService instance
     */
    public static synchronized BillingService getBillingService() {
        if (billingService == null) {
            billingService = new SimpleBillingServiceImpl();
        }
        return billingService;
    }
}