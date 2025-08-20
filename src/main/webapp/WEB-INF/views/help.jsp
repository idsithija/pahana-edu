<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${helpTitle} - Pahana Edu Bookshop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .help-sidebar {
            background-color: #f8f9fa;
            min-height: 100vh;
            padding: 20px 0;
        }
        
        .help-content {
            padding: 20px;
        }
        
        .help-nav-link {
            color: #495057;
            text-decoration: none;
            padding: 10px 20px;
            display: block;
            border-left: 3px solid transparent;
        }
        
        .help-nav-link:hover {
            background-color: #e9ecef;
            color: #007bff;
        }
        
        .help-nav-link.active {
            background-color: #e3f2fd;
            color: #1976d2;
            border-left-color: #1976d2;
        }
        
        .help-section {
            margin-bottom: 30px;
        }
        
        .help-content h4 {
            color: #1976d2;
            border-bottom: 2px solid #e3f2fd;
            padding-bottom: 10px;
            margin-bottom: 20px;
        }
        
        .help-content h5 {
            color: #555;
            margin-top: 25px;
            margin-bottom: 15px;
        }
        
        .help-content h6 {
            color: #666;
            margin-top: 20px;
            margin-bottom: 10px;
        }
        
        .help-content ul, .help-content ol {
            margin-bottom: 20px;
        }
        
        .help-content li {
            margin-bottom: 8px;
        }
        
        .breadcrumb-item + .breadcrumb-item::before {
            content: ">";
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
                <i class="fas fa-book"></i> Pahana Edu Bookshop
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                            <i class="fas fa-home"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/help">
                            <i class="fas fa-question-circle"></i> Help
                        </a>
                    </li>
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                            <i class="fas fa-user"></i> ${sessionScope.user.username}
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                <i class="fas fa-sign-out-alt"></i> Logout
                            </a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 help-sidebar">
                <div class="sticky-top" style="top: 20px;">
                    <h5 class="text-center text-primary mb-4">
                        <i class="fas fa-life-ring"></i> Help Topics
                    </h5>
                    
                    <nav class="nav flex-column">
                        <a href="${pageContext.request.contextPath}/help?section=overview" 
                           class="help-nav-link ${currentSection == 'overview' ? 'active' : ''}">
                            <i class="fas fa-home"></i> System Overview
                        </a>
                        <a href="${pageContext.request.contextPath}/help?section=customers" 
                           class="help-nav-link ${currentSection == 'customers' ? 'active' : ''}">
                            <i class="fas fa-users"></i> Customer Management
                        </a>
                        <a href="${pageContext.request.contextPath}/help?section=items" 
                           class="help-nav-link ${currentSection == 'items' ? 'active' : ''}">
                            <i class="fas fa-book"></i> Item Management
                        </a>
                        <a href="${pageContext.request.contextPath}/help?section=billing" 
                           class="help-nav-link ${currentSection == 'billing' ? 'active' : ''}">
                            <i class="fas fa-file-invoice"></i> Billing System
                        </a>
                        <a href="${pageContext.request.contextPath}/help?section=users" 
                           class="help-nav-link ${currentSection == 'users' ? 'active' : ''}">
                            <i class="fas fa-user-cog"></i> User Management
                        </a>
                        <a href="${pageContext.request.contextPath}/help?section=troubleshooting" 
                           class="help-nav-link ${currentSection == 'troubleshooting' ? 'active' : ''}">
                            <i class="fas fa-tools"></i> Troubleshooting
                        </a>
                    </nav>
                </div>
            </div>

            <!-- Main Content -->
            <div class="col-md-9">
                <div class="help-content">
                    <!-- Breadcrumb -->
                    <nav aria-label="breadcrumb" class="mb-4">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item">
                                <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                            </li>
                            <li class="breadcrumb-item">
                                <a href="${pageContext.request.contextPath}/help">Help</a>
                            </li>
                            <li class="breadcrumb-item active" aria-current="page">${helpTitle}</li>
                        </ol>
                    </nav>

                    <!-- Help Content -->
                    <div class="card">
                        <div class="card-header bg-primary text-white">
                            <h3 class="card-title mb-0">
                                <i class="fas fa-question-circle"></i> ${helpTitle}
                            </h3>
                        </div>
                        <div class="card-body">
                            ${helpContent}
                        </div>
                    </div>

                    <!-- Quick Actions -->
                    <div class="row mt-4">
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header">
                                    <h5><i class="fas fa-rocket"></i> Quick Actions</h5>
                                </div>
                                <div class="card-body">
                                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary btn-sm me-2 mb-2">
                                        <i class="fas fa-home"></i> Go to Dashboard
                                    </a>
                                    <a href="${pageContext.request.contextPath}/customer" class="btn btn-success btn-sm me-2 mb-2">
                                        <i class="fas fa-users"></i> Customers
                                    </a>
                                    <a href="${pageContext.request.contextPath}/item" class="btn btn-info btn-sm me-2 mb-2">
                                        <i class="fas fa-book"></i> Items
                                    </a>
                                    <a href="${pageContext.request.contextPath}/billing" class="btn btn-warning btn-sm mb-2">
                                        <i class="fas fa-file-invoice"></i> Billing
                                    </a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header">
                                    <h5><i class="fas fa-info-circle"></i> System Information</h5>
                                </div>
                                <div class="card-body">
                                    <p><strong>Version:</strong> 1.0</p>
                                    <p><strong>Technology:</strong> Java EE, PostgreSQL</p>
                                    <p><strong>Session User:</strong> ${sessionScope.user.username}</p>
                                    <p><strong>User Role:</strong> ${sessionScope.user.role}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <footer class="bg-light text-center py-3 mt-5">
        <div class="container">
            <p class="text-muted mb-0">
                &copy; 2025 Pahana Edu Bookshop Management System | 
                <a href="${pageContext.request.contextPath}/help">Need Help?</a>
            </p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>