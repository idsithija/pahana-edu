package com.pahanaedu.controller;

import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;
import com.pahanaedu.util.ServiceFactory;
import com.pahanaedu.util.SessionUtil;
import com.pahanaedu.util.PasswordUtil;
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
 * Servlet controller for user management
 * Handles user CRUD operations for admin interface
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/admin/users", "/admin/users/*"})
public class UserServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(UserServlet.class.getName());

    @EJB
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Fallback for development environments where EJB injection might fail
        if (userService == null) {
            userService = ServiceFactory.getUserService();
            logger.info("UserService EJB injection failed, using ServiceFactory fallback");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SessionUtil.updateLastActivity(request);

        // Check if user is admin
        if (!SessionUtil.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String pathInfo = request.getPathInfo();
        String action = (pathInfo != null && pathInfo.length() > 1) ? pathInfo.substring(1) : "manage";

        try {
            switch (action) {
                case "manage":
                    handleManageUsers(request, response);
                    break;
                case "view":
                    handleViewUser(request, response);
                    break;
                case "edit":
                    handleEditUser(request, response);
                    break;
                case "add":
                    handleAddUser(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/admin/users/manage");
                    break;
            }
        } catch (Exception e) {
            logger.severe("Error in UserServlet: " + e.getMessage());
            e.printStackTrace();
            if (!response.isCommitted()) {
                request.setAttribute("errorMessage", "An error occurred while processing your request.");
                try {
                    request.getRequestDispatcher("/WEB-INF/jsp/error/error.jsp").forward(request, response);
                } catch (Exception forwardException) {
                    logger.severe("Could not forward to error page: " + forwardException.getMessage());
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SessionUtil.updateLastActivity(request);

        // Check if user is admin
        if (!SessionUtil.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String action = request.getParameter("action");

        try {
            switch (action) {
                case "add":
                    handleAddUserPost(request, response);
                    break;
                case "edit":
                    handleEditUserPost(request, response);
                    break;
                case "delete":
                    handleDeleteUser(request, response);
                    break;
                case "toggle-status":
                    handleToggleUserStatus(request, response);
                    break;
                case "reset-password":
                    handleResetPassword(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/admin/users/manage");
                    break;
            }
        } catch (Exception e) {
            logger.severe("Error in UserServlet POST: " + e.getMessage());
            e.printStackTrace();
            if (!response.isCommitted()) {
                request.setAttribute("errorMessage", "An error occurred while processing your request.");
                try {
                    request.getRequestDispatcher("/WEB-INF/jsp/error/error.jsp").forward(request, response);
                } catch (Exception forwardException) {
                    logger.severe("Could not forward to error page: " + forwardException.getMessage());
                }
            }
        }
    }

    private void handleManageUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String search = request.getParameter("search");
        String roleFilter = request.getParameter("role");
        String statusFilter = request.getParameter("status");

        List<User> users;
        try {
            if (search != null && !search.trim().isEmpty()) {
                users = userService.searchUsers(search.trim());
            } else {
                users = userService.getAllUsers();
            }
        } catch (Exception e) {
            logger.severe("Error retrieving users: " + e.getMessage());
            e.printStackTrace();
            users = new java.util.ArrayList<>();
            request.setAttribute("errorMessage", "Error retrieving user data: " + e.getMessage());
        }

        // Apply filters
        if (roleFilter != null && !roleFilter.isEmpty() && !roleFilter.equals("ALL")) {
            users = users.stream()
                    .filter(user -> user.getRole().name().equals(roleFilter))
                    .collect(java.util.stream.Collectors.toList());
        }

        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("ALL")) {
            boolean isActive = "ACTIVE".equals(statusFilter);
            users = users.stream()
                    .filter(user -> user.isActive() == isActive)
                    .collect(java.util.stream.Collectors.toList());
        }

        request.setAttribute("users", users);
        request.setAttribute("search", search);
        request.setAttribute("roleFilter", roleFilter);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("userRoles", User.UserRole.values());

        request.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(request, response);
    }

    private void handleViewUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users/manage");
            return;
        }

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            request.setAttribute("user", userOpt.get());
            request.getRequestDispatcher("/WEB-INF/jsp/admin/user-details.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "User not found.");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(request, response);
        }
    }

    private void handleEditUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users/manage");
            return;
        }

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            request.setAttribute("user", userOpt.get());
            request.setAttribute("userRoles", User.UserRole.values());
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/user-form.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "User not found.");
            handleManageUsers(request, response);
        }
    }

    private void handleAddUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("userRoles", User.UserRole.values());
        request.setAttribute("mode", "add");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/user-form.jsp").forward(request, response);
    }

    private void handleAddUserPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String roleStr = request.getParameter("role");

        // Validation
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username is required.");
            handleAddUser(request, response);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Password is required.");
            handleAddUser(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            handleAddUser(request, response);
            return;
        }

        if (roleStr == null || roleStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Role is required.");
            handleAddUser(request, response);
            return;
        }

        // Check if username already exists
        if (userService.usernameExists(username.trim())) {
            request.setAttribute("errorMessage", "Username already exists.");
            handleAddUser(request, response);
            return;
        }

        try {
            User.UserRole role = User.UserRole.valueOf(roleStr);
            String passwordHash = PasswordUtil.hashPassword(password);

            User newUser = new User(username.trim(), passwordHash, role);
            userService.createUser(newUser);

            request.setAttribute("successMessage", "User created successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/users/manage?success=User created successfully");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid role selected.");
            handleAddUser(request, response);
        } catch (Exception e) {
            logger.severe("Error creating user: " + e.getMessage());
            request.setAttribute("errorMessage", "Error creating user: " + e.getMessage());
            handleAddUser(request, response);
        }
    }

    private void handleEditUserPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String roleStr = request.getParameter("role");
        String activeStr = request.getParameter("active");

        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users/manage");
            return;
        }

        try {
            Optional<User> userOpt = userService.findByUsername(username);
            if (!userOpt.isPresent()) {
                request.setAttribute("errorMessage", "User not found.");
                handleManageUsers(request, response);
                return;
            }

            User user = userOpt.get();

            if (roleStr != null && !roleStr.trim().isEmpty()) {
                User.UserRole role = User.UserRole.valueOf(roleStr);
                user.setRole(role);
            }

            if (activeStr != null) {
                user.setActive(Boolean.valueOf(activeStr));
            }

            userService.updateUser(user);

            response.sendRedirect(request.getContextPath() + "/admin/users/manage?success=User updated successfully");
        } catch (Exception e) {
            logger.severe("Error updating user: " + e.getMessage());
            request.setAttribute("errorMessage", "Error updating user: " + e.getMessage());
            handleEditUser(request, response);
        }
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users/manage");
            return;
        }

        try {
            // Don't allow deleting the current user
            String currentUsername = SessionUtil.getUsername(request);
            if (username.equals(currentUsername)) {
                response.sendRedirect(request.getContextPath() + "/admin/users/manage?error=Cannot delete your own account");
                return;
            }

            userService.deleteUser(username);
            response.sendRedirect(request.getContextPath() + "/admin/users/manage?success=User deleted successfully");
        } catch (Exception e) {
            logger.severe("Error deleting user: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/users/manage?error=Error deleting user");
        }
    }

    private void handleToggleUserStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users/manage");
            return;
        }

        try {
            // Don't allow deactivating the current user
            String currentUsername = SessionUtil.getUsername(request);
            if (username.equals(currentUsername)) {
                response.sendRedirect(request.getContextPath() + "/admin/users/manage?error=Cannot deactivate your own account");
                return;
            }

            userService.toggleUserStatus(username);
            response.sendRedirect(request.getContextPath() + "/admin/users/manage?success=User status updated successfully");
        } catch (Exception e) {
            logger.severe("Error toggling user status: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/users/manage?error=Error updating user status");
        }
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String newPassword = request.getParameter("newPassword");

        if (username == null || username.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users/manage?error=Invalid parameters");
            return;
        }

        try {
            String passwordHash = PasswordUtil.hashPassword(newPassword);
            userService.updatePassword(username, passwordHash);
            response.sendRedirect(request.getContextPath() + "/admin/users/manage?success=Password reset successfully");
        } catch (Exception e) {
            logger.severe("Error resetting password: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/users/manage?error=Error resetting password");
        }
    }
}
