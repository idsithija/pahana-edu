package com.pahanaedu.util;

import com.pahanaedu.model.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Utility class for session management
 * Provides helper methods for user session handling
 */
public class SessionUtil {
    
    private static final Logger logger = Logger.getLogger(SessionUtil.class.getName());
    
    public static final String CURRENT_USER_ATTR = "currentUser";
    public static final String LOGIN_TIME_ATTR = "loginTime";
    public static final String LAST_ACTIVITY_ATTR = "lastActivity";
    public static final String ORIGINAL_REQUEST_URL_ATTR = "originalRequestURL";
    
    /**
     * Create a new user session
     * @param request HTTP request
     * @param user Authenticated user
     */
    public static void createUserSession(HttpServletRequest request, User user) {
        if (request == null || user == null) {
            throw new IllegalArgumentException("Request and user cannot be null");
        }
        
        HttpSession session = request.getSession(true);
        
        // Set user information
        session.setAttribute(CURRENT_USER_ATTR, user);
        session.setAttribute(LOGIN_TIME_ATTR, LocalDateTime.now());
        session.setAttribute(LAST_ACTIVITY_ATTR, LocalDateTime.now());
        
        // Set session timeout (30 minutes)
        session.setMaxInactiveInterval(30 * 60);
        
        logger.info("User session created for: " + user.getUsername());
    }
    
    /**
     * Get current user from session
     * @param request HTTP request
     * @return Current user or null if not authenticated
     */
    public static User getCurrentUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        return (User) session.getAttribute(CURRENT_USER_ATTR);
    }
    
    /**
     * Check if user is authenticated
     * @param request HTTP request
     * @return true if user is authenticated
     */
    public static boolean isUserAuthenticated(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }
    
    /**
     * Check if current user has admin role
     * @param request HTTP request
     * @return true if user is admin
     */
    public static boolean isCurrentUserAdmin(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user != null && user.isAdmin();
    }
    
    /**
     * Check if current user has operator role
     * @param request HTTP request
     * @return true if user is operator
     */
    public static boolean isCurrentUserOperator(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user != null && user.isOperator();
    }
    
    /**
     * Update last activity time
     * @param request HTTP request
     */
    public static void updateLastActivity(HttpServletRequest request) {
        if (request == null) {
            return;
        }
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(LAST_ACTIVITY_ATTR, LocalDateTime.now());
        }
    }
    
    /**
     * Get login time
     * @param request HTTP request
     * @return Login time or null if not available
     */
    public static LocalDateTime getLoginTime(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        return (LocalDateTime) session.getAttribute(LOGIN_TIME_ATTR);
    }
    
    /**
     * Get last activity time
     * @param request HTTP request
     * @return Last activity time or null if not available
     */
    public static LocalDateTime getLastActivity(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        return (LocalDateTime) session.getAttribute(LAST_ACTIVITY_ATTR);
    }
    
    /**
     * Invalidate user session
     * @param request HTTP request
     */
    public static void invalidateSession(HttpServletRequest request) {
        if (request == null) {
            return;
        }
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = getCurrentUser(request);
            session.invalidate();
            
            if (user != null) {
                logger.info("User session invalidated for: " + user.getUsername());
            }
        }
    }
    
    /**
     * Store original request URL for redirect after login
     * @param request HTTP request
     * @param url Original request URL
     */
    public static void storeOriginalRequestURL(HttpServletRequest request, String url) {
        if (request == null || url == null) {
            return;
        }
        
        HttpSession session = request.getSession(true);
        session.setAttribute(ORIGINAL_REQUEST_URL_ATTR, url);
    }
    
    /**
     * Get and remove original request URL
     * @param request HTTP request
     * @return Original request URL or null if not available
     */
    public static String getAndRemoveOriginalRequestURL(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        String url = (String) session.getAttribute(ORIGINAL_REQUEST_URL_ATTR);
        session.removeAttribute(ORIGINAL_REQUEST_URL_ATTR);
        return url;
    }
    
    /**
     * Check if current user is admin (alias for isCurrentUserAdmin)
     * @param request HTTP request
     * @return true if user is admin
     */
    public static boolean isAdmin(HttpServletRequest request) {
        return isCurrentUserAdmin(request);
    }
    
    /**
     * Get current username
     * @param request HTTP request
     * @return Current username or null if not authenticated
     */
    public static String getUsername(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user != null ? user.getUsername() : null;
    }
    
    /**
     * Get session information as string
     * @param request HTTP request
     * @return Session information
     */
    public static String getSessionInfo(HttpServletRequest request) {
        if (request == null) {
            return "No request";
        }
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "No session";
        }
        
        User user = getCurrentUser(request);
        LocalDateTime loginTime = getLoginTime(request);
        LocalDateTime lastActivity = getLastActivity(request);
        
        return String.format("User: %s, Login: %s, Last Activity: %s, Session ID: %s",
                user != null ? user.getUsername() : "None",
                loginTime != null ? loginTime.toString() : "Unknown",
                lastActivity != null ? lastActivity.toString() : "Unknown",
                session.getId());
    }
}

