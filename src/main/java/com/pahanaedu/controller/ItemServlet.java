package com.pahanaedu.controller;

import com.pahanaedu.model.Item;
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
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Servlet controller for item management
 * Handles item CRUD operations and search functionality
 */
@WebServlet(name = "ItemServlet", urlPatterns = {"/item/*"})
public class ItemServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(ItemServlet.class.getName());
    
    @EJB
    private ItemService itemService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Fallback for development environments where EJB injection might fail
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
        
        try {
            switch (action) {
                case "manage":
                    handleManageItems(request, response);
                    break;
                case "add":
                    // GET request for add should redirect to manage page
                    response.sendRedirect(request.getContextPath() + "/item/manage");
                    break;
                case "edit":
                    handleEditItemData(request, response);
                    break;
                case "view":
                    handleViewItemData(request, response);
                    break;
                case "search":
                    handleSearchItems(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.severe("Error in ItemServlet: " + e.getMessage());
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
                    handleAddItem(request, response);
                    break;
                case "edit":
                    handleEditItem(request, response);
                    break;
                case "delete":
                    handleDeleteItem(request, response);
                    break;
                case "search":
                    handleSearchItems(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.severe("Error in ItemServlet POST: " + e.getMessage());
            request.setAttribute("errorMessage", "An error occurred while processing your request");
            request.getRequestDispatcher("/WEB-INF/jsp/error/500.jsp").forward(request, response);
        }
    }
    
    /**
     * Handle manage items page
     */
    private void handleManageItems(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get all items for the unified management page
        List<Item> items = itemService.getAllItems();
        
        request.setAttribute("items", items);
        
        request.getRequestDispatcher("/WEB-INF/jsp/item/manage.jsp").forward(request, response);
    }
    
    /**
     * Handle add item
     */
    private void handleAddItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String itemName = request.getParameter("itemName");
        String description = request.getParameter("description");
        String unitPriceStr = request.getParameter("unitPrice");
        String stockQuantityStr = request.getParameter("stockQuantity");
        String category = request.getParameter("category");
        
        try {
            BigDecimal unitPrice = new BigDecimal(unitPriceStr);
            Integer stockQuantity = Integer.parseInt(stockQuantityStr);
            
            Item item = itemService.createItem(itemName, description, unitPrice, stockQuantity, category);
            
            request.getSession().setAttribute("successMessage", 
                "Item added successfully! Item ID: " + item.getItemId());
            response.sendRedirect(request.getContextPath() + "/item/manage");
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid price or quantity format");
            response.sendRedirect(request.getContextPath() + "/item/manage");
        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/item/manage");
        }
    }
    
    /**
     * Handle edit item data request (for modal)
     */
    private void handleEditItemData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String itemIdStr = request.getParameter("itemId");
        if (itemIdStr == null || itemIdStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Item ID is required\"}");
            return;
        }
        
        try {
            Long itemId = Long.parseLong(itemIdStr);
            Optional<Item> itemOpt = itemService.findById(itemId);
            if (!itemOpt.isPresent()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Item not found\"}");
                return;
            }
            
            Item item = itemOpt.get();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Return item data as JSON
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"itemId\":").append(item.getItemId()).append(",");
            json.append("\"itemName\":\"").append(escapeJson(item.getItemName())).append("\",");
            json.append("\"description\":\"").append(escapeJson(item.getDescription() != null ? item.getDescription() : "")).append("\",");
            json.append("\"unitPrice\":").append(item.getUnitPrice()).append(",");
            json.append("\"stockQuantity\":").append(item.getStockQuantity()).append(",");
            json.append("\"category\":\"").append(escapeJson(item.getCategory() != null ? item.getCategory() : "")).append("\"");
            json.append("}");
            
            response.getWriter().write(json.toString());
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid item ID\"}");
        } catch (Exception e) {
            logger.severe("Error fetching item data: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error loading item data\"}");
        }
    }
    
    /**
     * Handle view item data request (for modal) - JSON response
     */
    private void handleViewItemData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String itemIdStr = request.getParameter("itemId");
        if (itemIdStr == null || itemIdStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Item ID is required\"}");
            return;
        }
        
        try {
            Long itemId = Long.parseLong(itemIdStr);
            Optional<Item> itemOpt = itemService.findById(itemId);
            if (!itemOpt.isPresent()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Item not found\"}");
                return;
            }
            
            Item item = itemOpt.get();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Log item data for debugging
            logger.info("View item data - ID: " + item.getItemId() + 
                       ", Name: " + item.getItemName() + 
                       ", Price: " + item.getUnitPrice() + 
                       ", Stock: " + item.getStockQuantity() + 
                       ", Category: " + item.getCategory());
            
            // Return complete item data as JSON
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"itemId\":").append(item.getItemId()).append(",");
            json.append("\"itemName\":\"").append(escapeJson(item.getItemName() != null ? item.getItemName() : "")).append("\",");
            json.append("\"description\":\"").append(escapeJson(item.getDescription() != null ? item.getDescription() : "")).append("\",");
            json.append("\"unitPrice\":").append(item.getUnitPrice() != null ? item.getUnitPrice() : 0).append(",");
            json.append("\"stockQuantity\":").append(item.getStockQuantity() != null ? item.getStockQuantity() : 0).append(",");
            json.append("\"category\":\"").append(escapeJson(item.getCategory() != null ? item.getCategory() : "")).append("\"");
            json.append("}");
            
            String jsonString = json.toString();
            logger.info("JSON response: " + jsonString);
            response.getWriter().write(jsonString);
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid item ID\"}");
        } catch (Exception e) {
            logger.severe("Error fetching item data for view: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Error loading item data\"}");
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
     * Handle edit item
     */
    private void handleEditItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String itemIdStr = request.getParameter("itemId");
        String itemName = request.getParameter("itemName");
        String description = request.getParameter("description");
        String unitPriceStr = request.getParameter("unitPrice");
        String stockQuantityStr = request.getParameter("stockQuantity");
        String category = request.getParameter("category");
        
        try {
            Long itemId = Long.parseLong(itemIdStr);
            BigDecimal unitPrice = new BigDecimal(unitPriceStr);
            Integer stockQuantity = Integer.parseInt(stockQuantityStr);
            
            Optional<Item> itemOpt = itemService.findById(itemId);
            if (!itemOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Item not found");
                return;
            }
            
            Item item = itemOpt.get();
            item.setItemName(itemName);
            item.setDescription(description);
            item.setUnitPrice(unitPrice);
            item.setStockQuantity(stockQuantity);
            item.setCategory(category);
            
            itemService.updateItem(item);
            
            request.getSession().setAttribute("successMessage", "Item updated successfully!");
            response.sendRedirect(request.getContextPath() + "/item/manage");
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid item ID, price, or quantity format");
            response.sendRedirect(request.getContextPath() + "/item/manage");
        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/item/manage");
        }
    }
    
    /**
     * Handle search items
     */
    private void handleSearchItems(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String itemName = request.getParameter("itemName");
        String category = request.getParameter("category");
        
        List<Item> items;
        
        if ((itemName == null || itemName.trim().isEmpty()) && 
            (category == null || category.trim().isEmpty())) {
            // No search criteria, show all items
            items = itemService.getAllItems();
        } else {
            items = itemService.searchItems(itemName, category, null, null, false);
        }
        
        request.setAttribute("items", items);
        request.setAttribute("searchItemName", itemName);
        request.setAttribute("searchCategory", category);
        request.setAttribute("isSearchResult", true);
        
        request.getRequestDispatcher("/WEB-INF/jsp/item/manage.jsp").forward(request, response);
    }
    
    /**
     * Handle delete item
     */
    private void handleDeleteItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String itemIdStr = request.getParameter("itemId");
        if (itemIdStr == null || itemIdStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Item ID is required");
            return;
        }
        
        try {
            Long itemId = Long.parseLong(itemIdStr);
            itemService.deleteItem(itemId);
            request.getSession().setAttribute("successMessage", "Item deleted successfully!");
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid item ID");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error deleting item: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/item/manage");
    }
}