package com.pahanaedu.controller;

import com.pahanaedu.service.CustomerService;
import com.pahanaedu.service.ItemService;
import com.pahanaedu.service.BillingService;
import com.pahanaedu.util.ServiceFactory;
import com.pahanaedu.util.SessionUtil;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet controller for dashboard functionality
 * Displays system overview and statistics
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", "/dashboard/*"})
public class DashboardServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(DashboardServlet.class.getName());
    
    @EJB
    private CustomerService customerService;
    
    @EJB
    private ItemService itemService;
    
    @EJB
    private BillingService billingService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Fallback for development environments where EJB injection might fail
        if (customerService == null) {
            customerService = ServiceFactory.getCustomerService();
            logger.info("CustomerService EJB injection failed, using ServiceFactory fallback");
        }
        if (itemService == null) {
            itemService = ServiceFactory.getItemService();
            logger.info("ItemService EJB injection failed, using ServiceFactory fallback");
        }
        if (billingService == null) {
            billingService = ServiceFactory.getBillingService();
            logger.info("BillingService EJB injection failed, using ServiceFactory fallback");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Update last activity
        SessionUtil.updateLastActivity(request);
        
        try {
            // Get dashboard statistics
            loadDashboardStatistics(request);
            
            // Forward to dashboard JSP
            request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.severe("Error loading dashboard: " + e.getMessage());
            request.setAttribute("errorMessage", "Error loading dashboard data");
            request.getRequestDispatcher("/WEB-INF/jsp/error/500.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Dashboard typically doesn't handle POST requests
        // Redirect to GET
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
    
    /**
     * Load dashboard statistics and data
     */
    private void loadDashboardStatistics(HttpServletRequest request) {
        try {
            // Customer statistics
            long[] customerStats = customerService.getCustomerStatistics();
            request.setAttribute("totalCustomers", customerStats[0]);
            request.setAttribute("activeCustomers", customerStats[1]);
            request.setAttribute("customersWithPendingBills", customerStats[2]);
            
            // Item statistics (using low stock threshold of 10)
            long[] itemStats = itemService.getItemStatistics(10);
            request.setAttribute("totalItems", itemStats[0]);
            request.setAttribute("itemsInStock", itemStats[1]);
            request.setAttribute("outOfStockItems", itemStats[2]);
            request.setAttribute("lowStockItems", itemStats[3]);
            
            // Bill statistics
            long[] billStats = billingService.getBillStatistics();
            request.setAttribute("totalBills", billStats[0]);
            request.setAttribute("pendingBills", billStats[1]);
            request.setAttribute("paidBills", billStats[2]);
            request.setAttribute("cancelledBills", billStats[3]);
            
            // Recent activity
            request.setAttribute("recentCustomers", customerService.getCustomersWithPagination(0, 5));
            request.setAttribute("lowStockItemsList", itemService.getLowStockItems(10));
            request.setAttribute("pendingBillsList", billingService.getPendingBills());
            request.setAttribute("todaysBills", billingService.getTodaysBills());
            
            // Revenue information
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate startOfMonth = today.withDayOfMonth(1);
            
            java.math.BigDecimal todaysRevenue = billingService.calculateRevenueForPeriod(today, today);
            java.math.BigDecimal monthlyRevenue = billingService.calculateRevenueForPeriod(startOfMonth, today);
            
            request.setAttribute("todaysRevenue", todaysRevenue);
            request.setAttribute("monthlyRevenue", monthlyRevenue);
            
        } catch (Exception e) {
            logger.warning("Error loading some dashboard statistics: " + e.getMessage());
            // Set default values to prevent JSP errors
            request.setAttribute("totalCustomers", 0L);
            request.setAttribute("activeCustomers", 0L);
            request.setAttribute("customersWithPendingBills", 0L);
            request.setAttribute("totalItems", 0L);
            request.setAttribute("itemsInStock", 0L);
            request.setAttribute("outOfStockItems", 0L);
            request.setAttribute("lowStockItems", 0L);
            request.setAttribute("totalBills", 0L);
            request.setAttribute("pendingBills", 0L);
            request.setAttribute("paidBills", 0L);
            request.setAttribute("cancelledBills", 0L);
            request.setAttribute("todaysRevenue", java.math.BigDecimal.ZERO);
            request.setAttribute("monthlyRevenue", java.math.BigDecimal.ZERO);
        }
    }
}
