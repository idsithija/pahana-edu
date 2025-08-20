package com.pahanaedu.controller;

import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;
import com.pahanaedu.service.impl.UserServiceImpl;
import com.pahanaedu.util.SessionUtil;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Servlet controller for user authentication
 * Handles login and logout operations
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
    
    @EJB
    private UserService userService;
    
    /**
     * Get UserService instance, with fallback for development environments
     */
    private UserService getUserService() {
        if (userService == null) {
            // Fallback for development environments without full EJB container
            logger.warning("EJB injection failed, using direct instantiation for development");
            return new UserServiceImpl();
        }
        return userService;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // If user is already authenticated, redirect to dashboard
        if (SessionUtil.isUserAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Forward to login page
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String action = request.getParameter("action");
        
        if ("logout".equals(action)) {
            handleLogout(request, response);
            return;
        }
        
        // Validate input
        if (username == null || password == null || 
            username.trim().isEmpty() || password.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Username and password are required");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Authenticate user
            Optional<User> userOpt = getUserService().authenticateUser(username.trim(), password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Create user session
                SessionUtil.createUserSession(request, user);
                
                logger.info("User logged in successfully: " + username);
                
                // Check for original request URL
                String originalURL = SessionUtil.getAndRemoveOriginalRequestURL(request);
                String redirectURL = originalURL != null ? originalURL : request.getContextPath() + "/dashboard";
                
                response.sendRedirect(redirectURL);
            } else {
                // Authentication failed
                request.setAttribute("errorMessage", "Invalid username or password");
                request.setAttribute("username", username); // Preserve username
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            logger.severe("Error during authentication: " + e.getMessage());
            request.setAttribute("errorMessage", "An error occurred during login. Please try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
    
    /**
     * Handle user logout
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        User currentUser = SessionUtil.getCurrentUser(request);
        if (currentUser != null) {
            logger.info("User logging out: " + currentUser.getUsername());
        }
        
        // Invalidate session
        SessionUtil.invalidateSession(request);
        
        // Redirect to login page with logout message
        response.sendRedirect(request.getContextPath() + "/login.jsp?message=logged_out");
    }
}

