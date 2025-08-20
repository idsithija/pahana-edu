package com.pahanaedu.controller;

import com.pahanaedu.model.Bill;
import com.pahanaedu.model.Customer;
import com.pahanaedu.model.Item;
import com.pahanaedu.service.BillingService;
import com.pahanaedu.service.CustomerService;
import com.pahanaedu.service.ItemService;
import com.pahanaedu.util.ServiceFactory;
import com.pahanaedu.util.SessionUtil;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Comprehensive billing management servlet
 * Handles bill creation, management, customer/item selection, and stock operations
 */
@WebServlet(name = "BillingServlet", urlPatterns = {"/billing/*"})
public class BillingServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(BillingServlet.class.getName());
    
    @EJB
    private BillingService billingService;
    
    @EJB
    private CustomerService customerService;
    
    @EJB
    private ItemService itemService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Fallback for development environments where EJB injection might fail
        if (billingService == null) {
            billingService = ServiceFactory.getBillingService();
            logger.info("BillingService EJB injection failed, using ServiceFactory fallback");
        }
        if (customerService == null) {
            customerService = ServiceFactory.getCustomerService();
            logger.info("CustomerService EJB injection failed, using ServiceFactory fallback");
        }
        if (itemService == null) {
            itemService = ServiceFactory.getItemService();
            logger.info("ItemService EJB injection failed, using ServiceFactory fallback");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        SessionUtil.updateLastActivity(request);
        
        String pathInfo = request.getPathInfo();
        String action = pathInfo != null ? pathInfo.substring(1) : "manage";
        
        logger.info("BillingServlet GET - pathInfo: " + pathInfo + ", action: " + action);
        
        try {
            switch (action) {
                case "manage":
                    handleManageBills(request, response);
                    break;
                case "create":
                    handleCreateBillForm(request, response);
                    break;
                case "view":
                    handleViewBillData(request, response);
                    break;
                case "edit":
                    handleEditBillData(request, response);
                    break;
                case "customers":
                    handleGetCustomers(request, response);
                    break;
                case "items":
                    handleGetItems(request, response);
                    break;
                case "search":
                    handleSearchBills(request, response);
                    break;
                case "test":
                    handleTest(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.severe("Error in BillingServlet: " + e.getMessage());
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
                case "create":
                    handleCreateBill(request, response);
                    break;
                case "add-item":
                    handleAddItemToBill(request, response);
                    break;
                case "remove-item":
                    handleRemoveItemFromBill(request, response);
                    break;
                case "update-quantity":
                    handleUpdateItemQuantity(request, response);
                    break;
                case "complete":
                    handleCompleteBill(request, response);
                    break;
                case "cancel":
                    handleCancelBill(request, response);
                    break;
                case "delete":
                    handleDeleteBill(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.severe("Error in BillingServlet POST: " + e.getMessage());
            request.setAttribute("errorMessage", "An error occurred while processing your request");
            request.getRequestDispatcher("/WEB-INF/jsp/error/500.jsp").forward(request, response);
        }
    }
    
    /**
     * Handle manage bills page - shows all bills
     */
    private void handleManageBills(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            logger.info("Loading billing management page...");
            
            List<Bill> bills = billingService.getAllBills();
            List<Customer> customers = customerService.getAllCustomers();
            List<Item> availableItems = itemService.getItemsInStock();
            
            logger.info("Loaded " + bills.size() + " bills, " + customers.size() + " customers, " + availableItems.size() + " items");
            
            request.setAttribute("bills", bills);
            request.setAttribute("customers", customers);
            request.setAttribute("availableItems", availableItems);
            
            // Create a map of bill dates converted to java.util.Date for JSP compatibility
            java.util.Map<Long, Date> billDatesMap = new java.util.HashMap<>();
            for (Bill bill : bills) {
                billDatesMap.put(bill.getBillId(), convertLocalDateToDate(bill.getBillDate()));
            }
            request.setAttribute("billDatesMap", billDatesMap);
            
            request.getRequestDispatcher("/WEB-INF/jsp/billing/manage.jsp").forward(request, response);
        } catch (Exception e) {
            logger.severe("Error in handleManageBills: " + e.getMessage());
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().write("<h1>Error Loading Billing Page</h1><p>Error: " + e.getMessage() + "</p>");
        }
    }
    
    /**
     * Handle create bill form display - redirect to manage page with modal
     */
    private void handleCreateBillForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Redirect to manage page since we now use modal for creating bills
        request.getSession().setAttribute("showCreateModal", true);
        response.sendRedirect(request.getContextPath() + "/billing/manage");
    }
    
    /**
     * Handle bill creation with items
     */
    private void handleCreateBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accountNumber = request.getParameter("accountNumber");
        
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Please select a customer");
            response.sendRedirect(request.getContextPath() + "/billing/create");
            return;
        }
        
        try {
            // Create the bill first
            Bill bill = billingService.createBill(accountNumber);
            logger.info("Created bill with ID: " + bill.getBillId());
            
            // Process items from the form
            Map<String, String[]> parameterMap = request.getParameterMap();
            boolean hasItems = false;
            int itemCount = 0;
            
            // Find all item parameters
            for (String paramName : parameterMap.keySet()) {
                if (paramName.matches("items\\[\\d+\\]\\.itemId")) {
                    String[] itemIds = parameterMap.get(paramName);
                    if (itemIds != null && itemIds.length > 0 && !itemIds[0].trim().isEmpty()) {
                        
                        // Extract the index from the parameter name
                        String indexStr = paramName.replaceAll("items\\[(\\d+)\\]\\.itemId", "$1");
                        String quantityParam = "items[" + indexStr + "].quantity";
                        
                        String[] quantities = parameterMap.get(quantityParam);
                        if (quantities != null && quantities.length > 0) {
                            try {
                                Long itemId = Long.parseLong(itemIds[0]);
                                int quantity = Integer.parseInt(quantities[0]);
                                
                                if (quantity > 0) {
                                    logger.info("Adding item " + itemId + " with quantity " + quantity + " to bill " + bill.getBillId());
                                    billingService.addItemToBill(bill.getBillId(), itemId, quantity);
                                    hasItems = true;
                                    itemCount++;
                                }
                            } catch (NumberFormatException e) {
                                logger.warning("Invalid item ID or quantity: " + e.getMessage());
                            } catch (IllegalArgumentException e) {
                                logger.warning("Error adding item to bill: " + e.getMessage());
                                request.getSession().setAttribute("errorMessage", "Error adding item: " + e.getMessage());
                                response.sendRedirect(request.getContextPath() + "/billing/create");
                                return;
                            }
                        }
                    }
                }
            }
            
            if (!hasItems) {
                // If no items were added, delete the empty bill
                billingService.deleteBill(bill.getBillId());
                request.getSession().setAttribute("errorMessage", "Please select at least one item");
                response.sendRedirect(request.getContextPath() + "/billing/create");
                return;
            }
            
            request.getSession().setAttribute("successMessage", 
                "Bill created successfully with " + itemCount + " item(s)! Bill ID: " + bill.getBillId());
            response.sendRedirect(request.getContextPath() + "/billing/manage");
            
        } catch (IllegalArgumentException e) {
            logger.severe("Error creating bill: " + e.getMessage());
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/billing/create");
        } catch (Exception e) {
            logger.severe("Unexpected error creating bill: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "An unexpected error occurred while creating the bill");
            response.sendRedirect(request.getContextPath() + "/billing/create");
        }
    }
    
    /**
     * Handle add item to bill
     */
    private void handleAddItemToBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billIdStr = request.getParameter("billId");
        String itemIdStr = request.getParameter("itemId");
        String quantityStr = request.getParameter("quantity");
        
        try {
            Long billId = Long.parseLong(billIdStr);
            Long itemId = Long.parseLong(itemIdStr);
            int quantity = Integer.parseInt(quantityStr);
            
            billingService.addItemToBill(billId, itemId, quantity);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": true, \"message\": \"Item added to bill successfully\"}");
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid bill ID, item ID, or quantity\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        }
    }
    
    /**
     * Handle remove item from bill
     */
    private void handleRemoveItemFromBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billIdStr = request.getParameter("billId");
        String itemIdStr = request.getParameter("itemId");
        
        try {
            Long billId = Long.parseLong(billIdStr);
            Long itemId = Long.parseLong(itemIdStr);
            
            billingService.removeItemFromBill(billId, itemId);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": true, \"message\": \"Item removed from bill successfully\"}");
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid bill ID or item ID\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        }
    }
    
    /**
     * Handle update item quantity in bill
     */
    private void handleUpdateItemQuantity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billIdStr = request.getParameter("billId");
        String itemIdStr = request.getParameter("itemId");
        String quantityStr = request.getParameter("quantity");
        
        try {
            Long billId = Long.parseLong(billIdStr);
            Long itemId = Long.parseLong(itemIdStr);
            int quantity = Integer.parseInt(quantityStr);
            
            billingService.updateItemQuantityInBill(billId, itemId, quantity);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": true, \"message\": \"Item quantity updated successfully\"}");
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid bill ID, item ID, or quantity\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        }
    }
    
    /**
     * Handle complete bill (mark as paid)
     */
    private void handleCompleteBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billIdStr = request.getParameter("billId");
        
        try {
            Long billId = Long.parseLong(billIdStr);
            billingService.markBillAsPaid(billId);
            
            request.getSession().setAttribute("successMessage", "Bill completed successfully!");
            response.sendRedirect(request.getContextPath() + "/billing/manage");
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid bill ID");
            response.sendRedirect(request.getContextPath() + "/billing/manage");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error completing bill: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/billing/manage");
        }
    }
    
    /**
     * Handle cancel bill
     */
    private void handleCancelBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billIdStr = request.getParameter("billId");
        
        try {
            Long billId = Long.parseLong(billIdStr);
            billingService.cancelBill(billId);
            
            request.getSession().setAttribute("successMessage", "Bill cancelled successfully!");
            response.sendRedirect(request.getContextPath() + "/billing/manage");
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid bill ID");
            response.sendRedirect(request.getContextPath() + "/billing/manage");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error cancelling bill: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/billing/manage");
        }
    }
    
    /**
     * Handle delete bill
     */
    private void handleDeleteBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billIdStr = request.getParameter("billId");
        
        try {
            Long billId = Long.parseLong(billIdStr);
            billingService.deleteBill(billId);
            
            request.getSession().setAttribute("successMessage", "Bill deleted successfully!");
            response.sendRedirect(request.getContextPath() + "/billing/manage");
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid bill ID");
            response.sendRedirect(request.getContextPath() + "/billing/manage");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error deleting bill: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/billing/manage");
        }
    }
    
    /**
     * Handle view bill data (for JSON response)
     */
    private void handleViewBillData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billIdStr = request.getParameter("billId");
        if (billIdStr == null || billIdStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Bill ID is required\"}");
            return;
        }
        
        try {
            Long billId = Long.parseLong(billIdStr);
            Optional<Bill> billOpt = billingService.findById(billId);
            
            if (!billOpt.isPresent()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Bill not found\"}");
                return;
            }
            
            Bill bill = billOpt.get();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Build JSON response for bill details
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"billId\":").append(bill.getBillId()).append(",");
            json.append("\"customerName\":\"").append(escapeJson(bill.getCustomer().getName())).append("\",");
            json.append("\"accountNumber\":\"").append(escapeJson(bill.getCustomer().getAccountNumber())).append("\",");
            json.append("\"billDate\":\"").append(bill.getBillDate()).append("\",");
            json.append("\"totalAmount\":").append(bill.getTotalAmount()).append(",");
            json.append("\"status\":\"").append(bill.getStatus()).append("\",");
            json.append("\"itemCount\":").append(bill.getTotalItemCount()).append(",");
            
            // Add bill items
            json.append("\"billItems\":[\n");
            for (int i = 0; i < bill.getBillItems().size(); i++) {
                if (i > 0) json.append(",\n");
                var billItem = bill.getBillItems().get(i);
                json.append("{");
                json.append("\"itemId\":").append(billItem.getItem().getItemId()).append(",");
                json.append("\"itemName\":\"").append(escapeJson(billItem.getItem().getItemName())).append("\",");
                json.append("\"quantity\":").append(billItem.getQuantity()).append(",");
                json.append("\"unitPrice\":").append(billItem.getUnitPrice()).append(",");
                json.append("\"totalPrice\":").append(billItem.getTotalPrice());
                json.append("}");
            }
            json.append("]");
            json.append("}");
            
            response.getWriter().write(json.toString());
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid bill ID\"}");
        } catch (Exception e) {
            logger.severe("Error fetching bill data: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Error loading bill data\"}");
        }
    }
    
    /**
     * Handle get customers (for JSON response)
     */
    private void handleGetCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            List<Customer> customers = customerService.getAllCustomers();
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            StringBuilder json = new StringBuilder();
            json.append("[");
            for (int i = 0; i < customers.size(); i++) {
                if (i > 0) json.append(",");
                Customer customer = customers.get(i);
                json.append("{");
                json.append("\"accountNumber\":\"").append(customer.getAccountNumber()).append("\",");
                json.append("\"name\":\"").append(escapeJson(customer.getName())).append("\",");
                json.append("\"telephoneNumber\":\"").append(escapeJson(customer.getTelephoneNumber() != null ? customer.getTelephoneNumber() : "")).append("\"");
                json.append("}");
            }
            json.append("]");
            
            response.getWriter().write(json.toString());
            
        } catch (Exception e) {
            logger.severe("Error fetching customers: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Error loading customers\"}");
        }
    }
    
    /**
     * Handle get items (for JSON response)
     */
    private void handleGetItems(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            List<Item> items = itemService.getItemsInStock();
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            StringBuilder json = new StringBuilder();
            json.append("[");
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) json.append(",");
                Item item = items.get(i);
                json.append("{");
                json.append("\"itemId\":").append(item.getItemId()).append(",");
                json.append("\"itemName\":\"").append(escapeJson(item.getItemName())).append("\",");
                json.append("\"unitPrice\":").append(item.getUnitPrice()).append(",");
                json.append("\"stockQuantity\":").append(item.getStockQuantity()).append(",");
                json.append("\"category\":\"").append(escapeJson(item.getCategory() != null ? item.getCategory() : "")).append("\"");
                json.append("}");
            }
            json.append("]");
            
            response.getWriter().write(json.toString());
            
        } catch (Exception e) {
            logger.severe("Error fetching items: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Error loading items\"}");
        }
    }
    
    /**
     * Handle search bills
     */
    private void handleSearchBills(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String customerName = request.getParameter("customerName");
        String status = request.getParameter("status");
        
        List<Bill> bills;
        Bill.BillStatus billStatus = null;
        
        if (status != null && !status.trim().isEmpty()) {
            try {
                billStatus = Bill.BillStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        
        if ((customerName == null || customerName.trim().isEmpty()) && billStatus == null) {
            bills = billingService.getAllBills();
        } else {
            bills = billingService.searchBills(customerName, billStatus, null, null, null, null);
        }
        
        List<Customer> customers = customerService.getAllCustomers();
        List<Item> availableItems = itemService.getItemsInStock();
        
        request.setAttribute("bills", bills);
        request.setAttribute("customers", customers);
        request.setAttribute("availableItems", availableItems);
        request.setAttribute("searchCustomerName", customerName);
        request.setAttribute("searchStatus", status);
        request.setAttribute("isSearchResult", true);
        
        // Create a map of bill dates converted to java.util.Date for JSP compatibility
        Map<Long, Date> billDatesMap = new HashMap<>();
        for (Bill bill : bills) {
            billDatesMap.put(bill.getBillId(), convertLocalDateToDate(bill.getBillDate()));
        }
        request.setAttribute("billDatesMap", billDatesMap);
        
        request.getRequestDispatcher("/WEB-INF/jsp/billing/manage.jsp").forward(request, response);
    }
    
    /**
     * Handle edit bill data (placeholder for future use)
     */
    private void handleEditBillData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect to view for now
        handleViewBillData(request, response);
    }
    
    /**
     * Handle test endpoint to verify servlet is working
     */
    private void handleTest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("<h1>Billing Servlet is Working!</h1><p>The servlet is properly loaded and responding.</p>");
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
     * Helper method to convert LocalDate to java.sql.Date for JSP compatibility
     */
    private Date convertLocalDateToDate(java.time.LocalDate localDate) {
        return localDate != null ? Date.valueOf(localDate) : null;
    }
}