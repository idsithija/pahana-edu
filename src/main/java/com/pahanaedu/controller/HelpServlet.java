package com.pahanaedu.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Help Servlet - Provides user guidance and system information
 * Part of MVC Controller layer
 */
@WebServlet("/help")
public class HelpServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(HelpServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String section = request.getParameter("section");
        if (section == null) {
            section = "overview";
        }
        
        // Set help content based on requested section
        setHelpContent(request, section);
        
        // Forward to help page
        request.getRequestDispatcher("/WEB-INF/views/help.jsp").forward(request, response);
    }
    
    private void setHelpContent(HttpServletRequest request, String section) {
        switch (section.toLowerCase()) {
            case "customers":
                request.setAttribute("helpTitle", "Customer Management");
                request.setAttribute("helpContent", getCustomerHelp());
                break;
            case "items":
                request.setAttribute("helpTitle", "Item Management");
                request.setAttribute("helpContent", getItemHelp());
                break;
            case "billing":
                request.setAttribute("helpTitle", "Billing System");
                request.setAttribute("helpContent", getBillingHelp());
                break;
            case "users":
                request.setAttribute("helpTitle", "User Management");
                request.setAttribute("helpContent", getUserHelp());
                break;
            case "troubleshooting":
                request.setAttribute("helpTitle", "Troubleshooting");
                request.setAttribute("helpContent", getTroubleshootingHelp());
                break;
            default:
                request.setAttribute("helpTitle", "System Overview");
                request.setAttribute("helpContent", getOverviewHelp());
        }
        
        request.setAttribute("currentSection", section);
    }
    
    private String getOverviewHelp() {
        return "<div class='help-content'>" +
               "<h4>Welcome to Pahana Edu Bookshop Management System</h4>" +
               "<p>This system helps you manage bookshop operations efficiently.</p>" +
               "<h5>Main Features:</h5>" +
               "<ul>" +
               "<li><strong>Customer Management:</strong> Add, edit, and manage customer accounts</li>" +
               "<li><strong>Item Management:</strong> Manage bookshop inventory</li>" +
               "<li><strong>Billing System:</strong> Create and manage customer bills</li>" +
               "<li><strong>User Management:</strong> Manage system users and permissions</li>" +
               "</ul>" +
               "<h5>Getting Started:</h5>" +
               "<ol>" +
               "<li>Use the navigation menu to access different sections</li>" +
               "<li>Start by adding customers and items</li>" +
               "<li>Create bills for customer purchases</li>" +
               "<li>Monitor sales and inventory through reports</li>" +
               "</ol>" +
               "</div>";
    }
    
    private String getCustomerHelp() {
        return "<div class='help-content'>" +
               "<h4>Customer Management Guide</h4>" +
               "<h5>Adding New Customers:</h5>" +
               "<ol>" +
               "<li>Click 'Add New Customer' button</li>" +
               "<li>Fill in customer details (Name is required)</li>" +
               "<li>Telephone number is optional but recommended</li>" +
               "<li>Account number is generated automatically</li>" +
               "</ol>" +
               "<h5>Managing Existing Customers:</h5>" +
               "<ul>" +
               "<li><strong>Search:</strong> Use the search box to find customers by name</li>" +
               "<li><strong>Edit:</strong> Click the edit button to update customer information</li>" +
               "<li><strong>View Details:</strong> Click on customer name to see full details</li>" +
               "<li><strong>Deactivate:</strong> Mark customers as inactive instead of deleting</li>" +
               "</ul>" +
               "<h5>Tips:</h5>" +
               "<ul>" +
               "<li>Keep customer contact information up-to-date for billing</li>" +
               "<li>Use descriptive addresses for delivery purposes</li>" +
               "</ul>" +
               "</div>";
    }
    
    private String getItemHelp() {
        return "<div class='help-content'>" +
               "<h4>Item Management Guide</h4>" +
               "<h5>Adding New Items:</h5>" +
               "<ol>" +
               "<li>Navigate to Item Management section</li>" +
               "<li>Click 'Add New Item' button</li>" +
               "<li>Enter item name, description, and unit price</li>" +
               "<li>Set initial stock quantity</li>" +
               "<li>Assign a category for better organization</li>" +
               "</ol>" +
               "<h5>Stock Management:</h5>" +
               "<ul>" +
               "<li><strong>Stock Tracking:</strong> System automatically updates stock when bills are created</li>" +
               "<li><strong>Low Stock Alerts:</strong> Monitor items with low stock quantities</li>" +
               "<li><strong>Price Updates:</strong> Edit item prices as needed</li>" +
               "</ul>" +
               "<h5>Categories:</h5>" +
               "<p>Organize items by categories such as:</p>" +
               "<ul>" +
               "<li>Programming Books</li>" +
               "<li>Mathematics Textbooks</li>" +
               "<li>Language Guides</li>" +
               "<li>Science Manuals</li>" +
               "<li>History Books</li>" +
               "</ul>" +
               "</div>";
    }
    
    private String getBillingHelp() {
        return "<div class='help-content'>" +
               "<h4>Billing System Guide</h4>" +
               "<h5>Creating Bills:</h5>" +
               "<ol>" +
               "<li>Select customer from the dropdown or search</li>" +
               "<li>Add items to the bill by selecting from inventory</li>" +
               "<li>Specify quantity for each item</li>" +
               "<li>Review total amount calculation</li>" +
               "<li>Save bill and optionally print</li>" +
               "</ol>" +
               "<h5>Bill Status Management:</h5>" +
               "<ul>" +
               "<li><strong>PENDING:</strong> Bill created but payment not received</li>" +
               "<li><strong>PAID:</strong> Payment completed</li>" +
               "<li><strong>CANCELLED:</strong> Bill cancelled (restores stock)</li>" +
               "</ul>" +
               "<h5>Important Notes:</h5>" +
               "<ul>" +
               "<li>Stock is automatically reduced when bill is created</li>" +
               "<li>Cancelling bills restores stock quantities</li>" +
               "<li>Bill totals are calculated automatically</li>" +
               "<li>Cannot create bills for items with insufficient stock</li>" +
               "</ul>" +
               "</div>";
    }
    
    private String getUserHelp() {
        return "<div class='help-content'>" +
               "<h4>User Management Guide</h4>" +
               "<p><em>Note: Only ADMIN users can manage other users</em></p>" +
               "<h5>User Roles:</h5>" +
               "<ul>" +
               "<li><strong>ADMIN:</strong> Full system access including user management</li>" +
               "<li><strong>OPERATOR:</strong> Standard operations (customers, items, billing)</li>" +
               "</ul>" +
               "<h5>Password Requirements:</h5>" +
               "<ul>" +
               "<li>Minimum 6 characters long</li>" +
               "<li>Must contain at least one letter</li>" +
               "<li>Must contain at least one number</li>" +
               "</ul>" +
               "<h5>Default Accounts:</h5>" +
               "<ul>" +
               "<li><strong>Username:</strong> admin, <strong>Password:</strong> admin123</li>" +
               "<li><strong>Username:</strong> operator, <strong>Password:</strong> operator123</li>" +
               "</ul>" +
               "<h5>Security Tips:</h5>" +
               "<ul>" +
               "<li>Change default passwords immediately</li>" +
               "<li>Use strong, unique passwords</li>" +
               "<li>Log out when finished using the system</li>" +
               "</ul>" +
               "</div>";
    }
    
    private String getTroubleshootingHelp() {
        return "<div class='help-content'>" +
               "<h4>Troubleshooting Guide</h4>" +
               "<h5>Common Issues and Solutions:</h5>" +
               "<h6>Login Problems:</h6>" +
               "<ul>" +
               "<li><strong>Invalid credentials:</strong> Check username and password</li>" +
               "<li><strong>Account locked:</strong> Contact administrator</li>" +
               "<li><strong>Browser issues:</strong> Clear browser cache and cookies</li>" +
               "</ul>" +
               "<h6>Bill Creation Errors:</h6>" +
               "<ul>" +
               "<li><strong>Insufficient stock:</strong> Check item availability</li>" +
               "<li><strong>Customer not found:</strong> Ensure customer is registered</li>" +
               "<li><strong>Calculation errors:</strong> Refresh page and try again</li>" +
               "</ul>" +
               "<h6>General Issues:</h6>" +
               "<ul>" +
               "<li><strong>Page not loading:</strong> Check internet connection</li>" +
               "<li><strong>Session expired:</strong> Log in again</li>" +
               "<li><strong>Data not saving:</strong> Ensure all required fields are filled</li>" +
               "</ul>" +
               "<h5>System Requirements:</h5>" +
               "<ul>" +
               "<li>Modern web browser (Chrome, Firefox, Safari, Edge)</li>" +
               "<li>JavaScript enabled</li>" +
               "<li>Stable internet connection</li>" +
               "</ul>" +
               "<h5>Contact Information:</h5>" +
               "<p>For technical support, contact your system administrator.</p>" +
               "</div>";
    }
}