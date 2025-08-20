package com.pahanaedu.util;

import com.pahanaedu.model.User;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Authentication filter to protect secured resources
 * Ensures users are authenticated before accessing protected pages
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/dashboard/*", "/customer/*", "/item/*", "/billing/*", "/admin/*", "/help"})
public class AuthenticationFilter implements Filter {
    
    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        logger.info("AuthenticationFilter: Processing request for " + requestURI);
        
        // Check if user is authenticated
        HttpSession session = httpRequest.getSession(false);
        User currentUser = null;
        
        if (session != null) {
            currentUser = (User) session.getAttribute("currentUser");
        }
        
        if (currentUser == null) {
            logger.warning("Unauthenticated access attempt to: " + requestURI);
            
            // Store the original request URL for redirect after login
            if (session == null) {
                session = httpRequest.getSession(true);
            }
            session.setAttribute("originalRequestURL", requestURI);
            
            // Redirect to login page with correct path
            httpResponse.sendRedirect(contextPath + "/login?error=session_expired");
            return;
        }
        
        // Check if user is active
        if (!currentUser.isActive()) {
            logger.warning("Inactive user access attempt: " + currentUser.getUsername());
            
            // Invalidate session and redirect to login
            session.invalidate();
            httpResponse.sendRedirect(contextPath + "/login?error=account_disabled");
            return;
        }
        
        // User is authenticated and active, proceed with request
        logger.info("Authenticated user " + currentUser.getUsername() + " accessing " + requestURI);
        
        // Add user information to request attributes for easy access in JSPs
        httpRequest.setAttribute("currentUser", currentUser);
        httpRequest.setAttribute("userRole", currentUser.getRole().toString());
        httpRequest.setAttribute("isAdmin", currentUser.isAdmin());
        httpRequest.setAttribute("isOperator", currentUser.isOperator());
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed");
    }
}
