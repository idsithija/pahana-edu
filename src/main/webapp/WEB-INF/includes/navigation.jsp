<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar">
    <div class="navbar-container">
        <!-- Brand -->
        <div class="navbar-brand">
            <a href="${pageContext.request.contextPath}/dashboard">
                <i class="fas fa-book-open"></i>
                <span>Pahana Edu Bookshop</span>
            </a>
        </div>

        <!-- Navigation Menu -->
        <div class="navbar-menu" id="navbarMenu">
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/dashboard"
                       class="nav-link ${pageContext.request.requestURI.contains('/dashboard') ? 'active' : ''}">
                        <i class="fas fa-tachometer-alt"></i>
                        <span>Dashboard</span>
                    </a>
                </li>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/customer/manage"
                       class="nav-link ${pageContext.request.requestURI.contains('/customer') ? 'active' : ''}">
                        <i class="fas fa-users-cog"></i>
                        <span>Customers</span>
                    </a>
                </li>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/item/manage"
                       class="nav-link ${pageContext.request.requestURI.contains('/item') ? 'active' : ''}">
                        <i class="fas fa-boxes"></i>
                        Inventory
                    </a>
                </li>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/billing/manage"
                       class="nav-link ${pageContext.request.requestURI.contains('/billing') ? 'active' : ''}">
                        <i class="fas fa-list"></i>
                        Bills
                    </a>
                </li>

                <c:if test="${isAdmin == true}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/admin/users"
                           class="nav-link dropdown-toggle ${pageContext.request.requestURI.contains('/admin') ? 'active' : ''}">
                            <i class="fas fa-cog"></i>
                            <span>Users</span>
                        </a>
                    </li>
                </c:if>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/help"
                       class="nav-link ${pageContext.request.requestURI.contains('/help') ? 'active' : ''}">
                        <i class="fas fa-question-circle"></i>
                        <span>Help</span>
                    </a>
                </li>
            </ul>
        </div>

        <!-- User Menu -->
        <div class="navbar-user">
            <div class="dropdown user-dropdown">
                <button class="user-dropdown-toggle" data-toggle="dropdown">
                    <div class="user-avatar">
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="user-info">
                        <span class="user-name">${currentUser.username}</span>
                        <span class="user-role">${userRole}</span>
                    </div>
                    <i class="fas fa-chevron-down"></i>
                </button>

                <div class="dropdown-menu dropdown-menu-right">
                    <form action="${pageContext.request.contextPath}/login" method="post" style="margin: 0;">
                        <input type="hidden" name="action" value="logout">
                        <button type="submit" class="dropdown-item logout-btn">
                            <i class="fas fa-sign-out-alt"></i>
                            Logout
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <!-- Mobile Menu Toggle -->
        <button class="navbar-toggle" id="navbarToggle">
            <span></span>
            <span></span>
            <span></span>
        </button>
    </div>
</nav>

<!-- Mobile Overlay -->
<div class="mobile-overlay" id="mobileOverlay"></div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        // Mobile menu toggle
        const navbarToggle = document.getElementById('navbarToggle');
        const navbarMenu = document.getElementById('navbarMenu');
        const mobileOverlay = document.getElementById('mobileOverlay');

        navbarToggle.addEventListener('click', function () {
            navbarMenu.classList.toggle('active');
            mobileOverlay.classList.toggle('active');
            navbarToggle.classList.toggle('active');
        });

        mobileOverlay.addEventListener('click', function () {
            navbarMenu.classList.remove('active');
            mobileOverlay.classList.remove('active');
            navbarToggle.classList.remove('active');
        });

        // Dropdown functionality
        const dropdownToggles = document.querySelectorAll('[data-toggle="dropdown"]');

        dropdownToggles.forEach(toggle => {
            toggle.addEventListener('click', function (e) {
                e.preventDefault();

                // Close other dropdowns
                dropdownToggles.forEach(otherToggle => {
                    if (otherToggle !== toggle) {
                        otherToggle.parentElement.classList.remove('show');
                    }
                });

                // Toggle current dropdown
                this.parentElement.classList.toggle('show');
            });
        });

        // Close dropdowns when clicking outside
        document.addEventListener('click', function (e) {
            if (!e.target.closest('.dropdown')) {
                dropdownToggles.forEach(toggle => {
                    toggle.parentElement.classList.remove('show');
                });
            }
        });

        // Prevent dropdown from closing when clicking inside
        document.querySelectorAll('.dropdown-menu').forEach(menu => {
            menu.addEventListener('click', function (e) {
                e.stopPropagation();
            });
        });

        // Logout confirmation
        const logoutBtn = document.querySelector('.logout-btn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', function (e) {
                e.preventDefault();

                if (confirm('Are you sure you want to logout?')) {
                    this.closest('form').submit();
                }
            });
        }
    });
</script>

