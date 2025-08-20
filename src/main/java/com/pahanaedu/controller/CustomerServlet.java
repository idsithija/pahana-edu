package com.pahanaedu.controller;

import com.pahanaedu.model.Customer;
import com.pahanaedu.service.CustomerService;
import com.pahanaedu.util.ServiceFactory;
import com.pahanaedu.util.SessionUtil;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Servlet controller for customer management
 * Handles customer CRUD operations and search functionality
 */
@WebServlet(name = "CustomerServlet", urlPatterns = {"/customer/*"})
public class CustomerServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(CustomerServlet.class.getName());
    
    @EJB
    private CustomerService customerService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Fallback for development environments where EJB injection might fail
        if (customerService == null) {
            customerService = ServiceFactory.getCustomerService();
            logger.info("CustomerService EJB injection failed, using ServiceFactory fallback");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        SessionUtil.updateLastActivity(request);
        
        String pathInfo = request.getPathInfo();
        String action = pathInfo != null ? pathInfo.substring(1) : "manage";
        
        try {
            switch (action) {
                case "manage":
                    handleManageCustomers(request, response);
                    break;
                case "add":
                    // GET request for add should redirect to manage page
                    response.sendRedirect(request.getContextPath() + "/customer/manage");
                    break;
                case "edit":
                    handleEditCustomerData(request, response);
                    break;
                case "view":
                    handleViewCustomerData(request, response);
                    break;
                case "search":
                    handleSearchCustomers(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.severe("Error in CustomerServlet: " + e.getMessage());
            request.setAttribute("errorMessage", "An error occurred while processing your request");
            request.getRequestDispatcher("/WEB-INF/jsp/error/500.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        String action = pathInfo != null ? pathInfo.substring(1) : "";
        
        try {
            switch (action) {
                case "add":
                    handleAddCustomer(request, response);
                    break;
                case "edit":
                    handleEditCustomer(request, response);
                    break;
                case "delete":
                    handleDeleteCustomer(request, response);
                    break;
                case "search":
                    handleSearchCustomers(request, response);
                    break;
                case "toggle-status":
                    handleToggleCustomerStatus(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.severe("Error in CustomerServlet POST: " + e.getMessage());
            request.setAttribute("errorMessage", "An error occurred while processing your request");
            request.getRequestDispatcher("/WEB-INF/jsp/error/500.jsp").forward(request, response);
        }
    }
    
    /**
     * Handle manage customers page
     */
    private void handleManageCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get all customers for the unified management page
        List<Customer> customers = customerService.getAllCustomers();
        
        request.setAttribute("customers", customers);
        
        request.getRequestDispatcher("/WEB-INF/jsp/customer/manage.jsp").forward(request, response);
    }
    
    
    /**
     * Handle add customer
     */
    private void handleAddCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accountNumber = request.getParameter("accountNumber");
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String telephoneNumber = request.getParameter("telephoneNumber");
        
        try {
            Customer customer = customerService.registerCustomerWithAccount(accountNumber, name, address, telephoneNumber);
            
            request.getSession().setAttribute("successMessage", 
                "Customer registered successfully! Account Number: " + customer.getAccountNumber());
            response.sendRedirect(request.getContextPath() + "/customer/manage");
            
        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/customer/manage");
        }
    }
    
    /**
     * Handle edit customer data request (for modal)
     */
    private void handleEditCustomerData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accountNumber = request.getParameter("accountNumber");
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Account number is required\"}");
            return;
        }
        
        try {
            Optional<Customer> customerOpt = customerService.findByAccountNumber(accountNumber);
            if (!customerOpt.isPresent()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Customer not found\"}");
                return;
            }
            
            Customer customer = customerOpt.get();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Return customer data as JSON
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"accountNumber\":\"").append(escapeJson(customer.getAccountNumber())).append("\",");
            json.append("\"name\":\"").append(escapeJson(customer.getName())).append("\",");
            json.append("\"telephoneNumber\":\"").append(escapeJson(customer.getTelephoneNumber() != null ? customer.getTelephoneNumber() : "")).append("\",");
            json.append("\"address\":\"").append(escapeJson(customer.getAddress() != null ? customer.getAddress() : "")).append("\"");
            json.append("}");
            
            response.getWriter().write(json.toString());
            
        } catch (Exception e) {
            logger.severe("Error fetching customer data: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error loading customer data\"}");
        }
    }
    
    /**
     * Helper method to escape JSON strings
     */
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    /**
     * Handle edit customer
     */
    private void handleEditCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accountNumber = request.getParameter("accountNumber");
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String telephoneNumber = request.getParameter("telephoneNumber");
        
        try {
            Optional<Customer> customerOpt = customerService.findByAccountNumber(accountNumber);
            if (!customerOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer not found");
                return;
            }
            
            Customer customer = customerOpt.get();
            customer.setName(name);
            customer.setAddress(address);
            customer.setTelephoneNumber(telephoneNumber);
            
            customerService.updateCustomer(customer);
            
            request.getSession().setAttribute("successMessage", "Customer updated successfully!");
            response.sendRedirect(request.getContextPath() + "/customer/manage");
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("accountNumber", accountNumber);
            request.setAttribute("name", name);
            request.setAttribute("address", address);
            request.setAttribute("telephoneNumber", telephoneNumber);
            request.getRequestDispatcher("/WEB-INF/jsp/customer/edit.jsp").forward(request, response);
        }
    }
    
    /**
     * Handle view customer data request (for modal) - JSON response
     */
    private void handleViewCustomerData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accountNumber = request.getParameter("accountNumber");
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Account number is required\"}");
            return;
        }
        
        try {
            Optional<Customer> customerOpt = customerService.findByAccountNumber(accountNumber);
            if (!customerOpt.isPresent()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Customer not found\"}");
                return;
            }
            
            Customer customer = customerOpt.get();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Log customer data for debugging
            logger.info("View customer data - Account: " + customer.getAccountNumber() + 
                       ", Name: " + customer.getName() + 
                       ", Phone: " + customer.getTelephoneNumber() + 
                       ", Address: " + customer.getAddress() + 
                       ", Active: " + customer.getActive());
            
            // Return complete customer data as JSON including registration date
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"accountNumber\":\"").append(escapeJson(customer.getAccountNumber() != null ? customer.getAccountNumber() : "")).append("\",");
            json.append("\"name\":\"").append(escapeJson(customer.getName() != null ? customer.getName() : "")).append("\",");
            json.append("\"telephoneNumber\":\"").append(escapeJson(customer.getTelephoneNumber() != null ? customer.getTelephoneNumber() : "")).append("\",");
            json.append("\"address\":\"").append(escapeJson(customer.getAddress() != null ? customer.getAddress() : "")).append("\",");
            json.append("\"registrationDate\":\"").append(customer.getRegistrationDate() != null ? customer.getRegistrationDate().toString() : "").append("\",");
            json.append("\"active\":").append(customer.getActive() != null ? customer.getActive() : false);
            json.append("}");
            
            String jsonString = json.toString();
            logger.info("JSON response: " + jsonString);
            response.getWriter().write(jsonString);
            
        } catch (Exception e) {
            logger.severe("Error fetching customer data for view: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Error loading customer data\"}");
        }
    }
    
    /**
     * Handle view customer (legacy - for JSP page)
     */
    private void handleViewCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accountNumber = request.getParameter("accountNumber");
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account number is required");
            return;
        }
        
        Optional<Customer> customerOpt = customerService.findByAccountNumber(accountNumber);
        if (!customerOpt.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer not found");
            return;
        }
        
        request.setAttribute("customer", customerOpt.get());
        request.getRequestDispatcher("/WEB-INF/jsp/customer/view.jsp").forward(request, response);
    }
    
    /**
     * Handle search customers
     */
    private void handleSearchCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String telephoneNumber = request.getParameter("telephoneNumber");
        String activeParam = request.getParameter("active");
        Boolean active = activeParam != null && !activeParam.isEmpty() ? Boolean.valueOf(activeParam) : null;
        
        List<Customer> customers;
        
        if ((name == null || name.trim().isEmpty()) && 
            (telephoneNumber == null || telephoneNumber.trim().isEmpty()) && 
            active == null) {
            // No search criteria, show all customers
            customers = customerService.getAllCustomers();
        } else {
            customers = customerService.searchCustomers(name, telephoneNumber, active);
        }
        
        request.setAttribute("customers", customers);
        request.setAttribute("searchName", name);
        request.setAttribute("searchTelephoneNumber", telephoneNumber);
        request.setAttribute("searchActive", activeParam);
        request.setAttribute("isSearchResult", true);
        
        request.getRequestDispatcher("/WEB-INF/jsp/customer/manage.jsp").forward(request, response);
    }
    
    /**
     * Handle delete customer (soft delete)
     */
    private void handleDeleteCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accountNumber = request.getParameter("accountNumber");
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account number is required");
            return;
        }
        
        try {
            customerService.deleteCustomer(accountNumber);
            request.getSession().setAttribute("successMessage", "Customer deactivated successfully!");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error deactivating customer: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/customer/manage");
    }
    
    /**
     * Handle toggle customer status
     */
    private void handleToggleCustomerStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accountNumber = request.getParameter("accountNumber");
        String activeParam = request.getParameter("active");
        
        if (accountNumber == null || accountNumber.trim().isEmpty() || activeParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account number and status are required");
            return;
        }
        
        try {
            boolean active = Boolean.parseBoolean(activeParam);
            customerService.updateActiveStatus(accountNumber, active);
            
            String message = active ? "Customer activated successfully!" : "Customer deactivated successfully!";
            request.getSession().setAttribute("successMessage", message);
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error updating customer status: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/customer/manage");
    }
    
    /**
     * Helper method to get integer parameter with default value
     */
    private int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        String paramValue = request.getParameter(paramName);
        if (paramValue != null && !paramValue.trim().isEmpty()) {
            try {
                return Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
                // Return default value if parsing fails
            }
        }
        return defaultValue;
    }
}

