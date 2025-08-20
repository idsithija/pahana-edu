<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Pahana Edu Bookshop</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css?v=2">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <!-- Include Navigation -->
    <%@ include file="../includes/navigation.jsp" %>
    
    <div class="main-content">
        <div class="container-fluid">
            <!-- Dashboard Header -->
            <div class="dashboard-header">
                <div class="header-content">
                    <h1>
                        <i class="fas fa-tachometer-alt"></i>
                        Dashboard
                    </h1>
                    <p class="header-subtitle">Welcome back, ${currentUser.username}! Here's what's happening in your bookshop today.</p>
                </div>
                <div class="header-actions">
                    <div class="current-time" id="currentTime"></div>
                </div>
            </div>
            
            <!-- Statistics Cards -->
            <div class="stats-grid">
                <!-- Customer Statistics -->
                <div class="stat-card">
                    <div class="stat-icon bg-primary">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${totalCustomers}</h3>
                        <p>Total Customers</p>
                        <div class="stat-detail">
                            <span class="text-success">
                                <i class="fas fa-user-check"></i>
                                ${activeCustomers} Active
                            </span>
                        </div>
                    </div>
                </div>
                
                <!-- Item Statistics -->
                <div class="stat-card">
                    <div class="stat-icon bg-success">
                        <i class="fas fa-book"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${totalItems}</h3>
                        <p>Total Items</p>
                        <div class="stat-detail">
                            <span class="text-warning">
                                <i class="fas fa-exclamation-triangle"></i>
                                ${lowStockItems} Low Stock
                            </span>
                        </div>
                    </div>
                </div>
                
                <!-- Bill Statistics -->
                <div class="stat-card">
                    <div class="stat-icon bg-warning">
                        <i class="fas fa-file-invoice"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${totalBills}</h3>
                        <p>Total Bills</p>
                        <div class="stat-detail">
                            <span class="text-danger">
                                <i class="fas fa-clock"></i>
                                ${pendingBills} Pending
                            </span>
                        </div>
                    </div>
                </div>
                
                <!-- Revenue Statistics -->
                <div class="stat-card">
                    <div class="stat-icon bg-info">
                        <i class="fas fa-dollar-sign"></i>
                    </div>
                    <div class="stat-content">
                        <h3>
                            <fmt:formatNumber value="${monthlyRevenue}" type="currency" currencySymbol="$" />
                        </h3>
                        <p>Monthly Revenue</p>
                        <div class="stat-detail">
                            <span class="text-success">
                                <i class="fas fa-calendar-day"></i>
                                Today: <fmt:formatNumber value="${todaysRevenue}" type="currency" currencySymbol="$" />
                            </span>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Dashboard Content Grid -->
            <div class="dashboard-grid">
                <!-- Quick Actions -->
                <div class="dashboard-section">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i class="fas fa-bolt"></i>
                                Quick Actions
                            </h3>
                        </div>
                        <div class="card-body">
                            <div class="quick-actions">
                                <a href="${pageContext.request.contextPath}/customer/add" class="quick-action-btn">
                                    <i class="fas fa-user-plus"></i>
                                    <span>Add Customer</span>
                                </a>
                                <a href="${pageContext.request.contextPath}/item/add" class="quick-action-btn">
                                    <i class="fas fa-plus"></i>
                                    <span>Add Item</span>
                                </a>
                                <a href="${pageContext.request.contextPath}/billing/create" class="quick-action-btn">
                                    <i class="fas fa-file-invoice-dollar"></i>
                                    <span>Create Bill</span>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Low Stock Alert -->
                <div class="dashboard-section">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i class="fas fa-exclamation-triangle text-warning"></i>
                                Low Stock Alert
                            </h3>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty lowStockItemsList}">
                                    <div class="low-stock-list">
                                        <c:forEach items="${lowStockItemsList}" var="item" varStatus="status">
                                            <c:if test="${status.index < 5}">
                                                <div class="low-stock-item">
                                                    <div class="item-info">
                                                        <h4>${item.itemName}</h4>
                                                        <p class="text-muted">${item.category}</p>
                                                    </div>
                                                    <div class="stock-info">
                                                        <span class="stock-quantity ${item.stockQuantity <= 5 ? 'text-danger' : 'text-warning'}">
                                                            ${item.stockQuantity} left
                                                        </span>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                    <div class="card-footer">
                                        <a href="${pageContext.request.contextPath}/item/manage" class="btn btn-outline btn-sm">
                                            <i class="fas fa-boxes"></i>
                                            Manage Inventory
                                        </a>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-state">
                                        <i class="fas fa-check-circle text-success"></i>
                                        <p>All items are well stocked!</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                
                <!-- Pending Bills -->
                <div class="dashboard-section">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i class="fas fa-clock text-warning"></i>
                                Pending Bills
                            </h3>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty pendingBillsList}">
                                    <div class="pending-bills-list">
                                        <c:forEach items="${pendingBillsList}" var="bill" varStatus="status">
                                            <c:if test="${status.index < 5}">
                                                <div class="pending-bill-item">
                                                    <div class="bill-info">
                                                        <h4>Bill #${bill.billId}</h4>
                                                        <p class="text-muted">${bill.customer.name}</p>
                                                    </div>
                                                    <div class="bill-amount">
                                                        <span class="amount">
                                                            <fmt:formatNumber value="${bill.totalAmount}" type="currency" currencySymbol="$" />
                                                        </span>
                                                        <span class="badge badge-warning">Pending</span>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                    <div class="card-footer">
                                        <a href="${pageContext.request.contextPath}/billing/manage" class="btn btn-outline btn-sm">
                                            <i class="fas fa-file-invoice"></i>
                                            Manage Bills
                                        </a>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-state">
                                        <i class="fas fa-check-circle text-success"></i>
                                        <p>No pending bills!</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Recent Activity - Full Width Section -->
            <div class="dashboard-full-width">
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">
                            <i class="fas fa-clock"></i>
                            Recent Activity
                        </h3>
                    </div>
                    <div class="card-body">
                        <div class="activity-list">
                            <c:forEach items="${todaysBills}" var="bill" varStatus="status">
                                <c:if test="${status.index < 8}">
                                    <div class="activity-item">
                                        <div class="activity-icon">
                                            <i class="fas fa-file-invoice text-primary"></i>
                                        </div>
                                        <div class="activity-content">
                                            <p class="activity-title">
                                                New bill created for ${bill.customer.name}
                                            </p>
                                            <p class="activity-time">
                                                <fmt:formatNumber value="${bill.totalAmount}" type="currency" currencySymbol="$" />
                                                • <span class="badge badge-${bill.status == 'PAID' ? 'success' : bill.status == 'PENDING' ? 'warning' : 'danger'}">${bill.status}</span>
                                            </p>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                            
                            <c:forEach items="${recentCustomers}" var="customer" varStatus="status">
                                <c:if test="${status.index < 5}">
                                    <div class="activity-item">
                                        <div class="activity-icon">
                                            <i class="fas fa-user-plus text-success"></i>
                                        </div>
                                        <div class="activity-content">
                                            <p class="activity-title">
                                                New customer registered: ${customer.name}
                                            </p>
                                            <p class="activity-time">
                                                Account: ${customer.accountNumber}
                                            </p>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Include Footer -->
    <%@ include file="../includes/footer.jsp" %>
    
    <script>
        // Update current time
        function updateTime() {
            const now = new Date();
            const timeString = now.toLocaleString('en-US', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
            document.getElementById('currentTime').textContent = timeString;
        }
        
        // Update time every minute
        updateTime();
        setInterval(updateTime, 60000);
        
        // Add animation to stat cards
        document.addEventListener('DOMContentLoaded', function() {
            const statCards = document.querySelectorAll('.stat-card');
            statCards.forEach((card, index) => {
                card.style.animationDelay = `${index * 0.1}s`;
                card.classList.add('animate-in');
            });
        });
    </script>
</body>
</html>

