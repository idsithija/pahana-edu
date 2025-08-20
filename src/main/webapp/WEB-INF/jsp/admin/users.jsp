<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - Pahana Edu Bookshop</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .search-bar {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }

        .search-form {
            display: flex;
            gap: 15px;
            align-items: center;
            flex-wrap: wrap;
        }

        .search-input {
            flex: 1;
            min-width: 200px;
            padding: 10px 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }

        .filter-select {
            padding: 10px 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            min-width: 120px;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
            transition: all 0.3s ease;
            text-align: center;
        }

        .btn-primary {
            background: #007bff;
            color: white;
        }

        .btn-primary:hover {
            background: #0056b3;
        }

        .btn-success {
            background: #28a745;
            color: white;
        }

        .btn-success:hover {
            background: #1e7e34;
        }

        .btn-warning {
            background: #ffc107;
            color: #212529;
        }

        .btn-warning:hover {
            background: #e0a800;
        }

        .btn-danger {
            background: #dc3545;
            color: white;
        }

        .btn-danger:hover {
            background: #c82333;
        }

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #5a6268;
        }

        .btn-info {
            background: #17a2b8;
            color: white;
        }

        .btn-info:hover {
            background: #138496;
        }

        .btn-sm {
            padding: 6px 12px;
            font-size: 12px;
        }

        .user-grid {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }

        .user-header {
            background: #f8f9fa;
            padding: 20px;
            border-bottom: 1px solid #dee2e6;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .user-table {
            width: 100%;
            border-collapse: collapse;
        }

        .user-table th,
        .user-table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }

        .user-table th {
            background: #f8f9fa;
            font-weight: 600;
            color: #495057;
        }

        .user-table tbody tr:hover {
            background: #f8f9fa;
        }

        .badge {
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
        }

        .badge-success {
            background: #d4edda;
            color: #155724;
        }

        .badge-danger {
            background: #f8d7da;
            color: #721c24;
        }

        .badge-primary {
            background: #d1ecf1;
            color: #0c5460;
        }

        .badge-warning {
            background: #fff3cd;
            color: #856404;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
        }

        .modal-content {
            background: white;
            margin: 5% auto;
            padding: 0;
            border-radius: 8px;
            width: 90%;
            max-width: 600px;
            max-height: 80%;
            overflow-y: auto;
        }

        .modal-header {
            padding: 20px;
            border-bottom: 1px solid #dee2e6;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .modal-body {
            padding: 20px;
        }

        .modal-footer {
            padding: 20px;
            border-top: 1px solid #dee2e6;
            text-align: right;
        }

        .close {
            background: none;
            border: none;
            font-size: 24px;
            cursor: pointer;
            color: #6c757d;
        }

        .close:hover {
            color: #495057;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 500;
            color: #495057;
        }

        .form-control {
            width: 100%;
            padding: 10px 15px;
            border: 1px solid #ced4da;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.15s ease-in-out;
            box-sizing: border-box;
        }

        .form-control:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, .25);
        }

        .alert {
            padding: 12px 15px;
            margin-bottom: 20px;
            border: 1px solid transparent;
            border-radius: 5px;
        }

        .alert-success {
            color: #155724;
            background-color: #d4edda;
            border-color: #c3e6cb;
        }

        .alert-danger {
            color: #721c24;
            background-color: #f8d7da;
            border-color: #f5c6cb;
        }

        .user-actions {
            display: flex;
            gap: 5px;
            flex-wrap: wrap;
        }

        .no-users {
            text-align: center;
            padding: 40px;
            color: #6c757d;
        }

        .user-details {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .detail-row {
            display: flex;
            margin-bottom: 10px;
        }

        .detail-label {
            font-weight: 600;
            min-width: 150px;
            color: #495057;
        }

        .detail-value {
            color: #212529;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }

        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            text-align: center;
        }

        .stat-number {
            font-size: 2em;
            font-weight: bold;
            color: #007bff;
        }

        .stat-label {
            color: #6c757d;
            margin-top: 5px;
        }
    </style>
</head>
<body>
<!-- Include Navigation -->
<%@ include file="../../includes/navigation.jsp" %>

<div class="main-content">
    <div class="container-fluid">
        <h1><i class="fas fa-users"></i> User Management</h1>

        <!-- Success/Error Messages -->
        <c:if test="${param.success != null}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i> ${param.success}
            </div>
        </c:if>
        <c:if test="${param.error != null}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i> ${param.error}
            </div>
        </c:if>
        <c:if test="${errorMessage != null}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i> ${errorMessage}
            </div>
        </c:if>

        <!-- User Statistics -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-number">${users.size()}</div>
                <div class="stat-label">Total Users</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">
                    <c:set var="activeCount" value="0"/>
                    <c:forEach var="user" items="${users}">
                        <c:if test="${user.active}">
                            <c:set var="activeCount" value="${activeCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${activeCount}
                </div>
                <div class="stat-label">Active Users</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">
                    <c:set var="adminCount" value="0"/>
                    <c:forEach var="user" items="${users}">
                        <c:if test="${user.role == 'ADMIN'}">
                            <c:set var="adminCount" value="${adminCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${adminCount}
                </div>
                <div class="stat-label">Administrators</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">
                    <c:set var="operatorCount" value="0"/>
                    <c:forEach var="user" items="${users}">
                        <c:if test="${user.role == 'OPERATOR'}">
                            <c:set var="operatorCount" value="${operatorCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${operatorCount}
                </div>
                <div class="stat-label">Operators</div>
            </div>
        </div>

        <!-- Search and Filter Bar -->
        <div class="search-bar">
            <form method="GET" action="${pageContext.request.contextPath}/admin/users/manage" class="search-form">
                <input type="text" name="search" placeholder="Search by username..."
                       value="${search}" class="search-input">

                <select name="role" class="filter-select">
                    <option value="">All Roles</option>
                    <option value="ADMIN" ${roleFilter == 'ADMIN' ? 'selected' : ''}>Admin</option>
                    <option value="OPERATOR" ${roleFilter == 'OPERATOR' ? 'selected' : ''}>Operator</option>
                </select>

                <select name="status" class="filter-select">
                    <option value="">All Status</option>
                    <option value="ACTIVE" ${statusFilter == 'ACTIVE' ? 'selected' : ''}>Active</option>
                    <option value="INACTIVE" ${statusFilter == 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                </select>

                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-search"></i> Search
                </button>

                <a href="${pageContext.request.contextPath}/admin/users/manage" class="btn btn-secondary">
                    <i class="fas fa-times"></i> Clear
                </a>
            </form>
        </div>

        <!-- Users Table -->
        <div class="user-grid">
            <div class="user-header">
                <h3><i class="fas fa-list"></i> Users List</h3>
                <button class="btn btn-success" onclick="openAddUserModal()">
                    <i class="fas fa-plus"></i> Add New User
                </button>
            </div>

            <c:choose>
                <c:when test="${empty users}">
                    <div class="no-users">
                        <i class="fas fa-users fa-3x" style="color: #dee2e6; margin-bottom: 20px;"></i>
                        <h4>No Users Found</h4>
                        <p>No users match your search criteria.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="user-table">
                        <thead>
                        <tr>
                            <th>Username</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Created Date</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td>
                                    <strong>${user.username}</strong>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${user.role == 'ADMIN'}">
                                                    <span class="badge badge-primary">
                                                        <i class="fas fa-crown"></i> Admin
                                                    </span>
                                        </c:when>
                                        <c:otherwise>
                                                    <span class="badge badge-warning">
                                                        <i class="fas fa-user"></i> Operator
                                                    </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${user.active}">
                                                    <span class="badge badge-success">
                                                        <i class="fas fa-check"></i> Active
                                                    </span>
                                        </c:when>
                                        <c:otherwise>
                                                    <span class="badge badge-danger">
                                                        <i class="fas fa-times"></i> Inactive
                                                    </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${user.createdDate != null}">
                                            ${user.createdDate.toString().replace('T', ' ').substring(0, 16)}
                                        </c:when>
                                        <c:otherwise>
                                            N/A
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="user-actions">
                                        <button class="btn btn-info btn-sm"
                                                onclick="viewUser('${user.username}')">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                        <button class="btn btn-warning btn-sm"
                                                onclick="editUser('${user.username}')">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <c:if test="${user.username != sessionScope.username}">
                                            <button class="btn ${user.active ? 'btn-secondary' : 'btn-success'} btn-sm"
                                                    onclick="toggleUserStatus('${user.username}', ${user.active})">
                                                <i class="fas ${user.active ? 'fa-pause' : 'fa-play'}"></i>
                                            </button>
                                            <button class="btn btn-primary btn-sm"
                                                    onclick="resetPassword('${user.username}')">
                                                <i class="fas fa-key"></i>
                                            </button>
                                            <button class="btn btn-danger btn-sm"
                                                    onclick="deleteUser('${user.username}')">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<!-- Add User Modal -->
<div id="addUserModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h4><i class="fas fa-user-plus"></i> Add New User</h4>
            <button type="button" class="close" onclick="closeModal('addUserModal')">&times;</button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/admin/users/manage">
            <div class="modal-body">
                <input type="hidden" name="action" value="add">

                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" class="form-control"
                           required maxlength="50" placeholder="Enter username">
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control"
                           required placeholder="Enter password">
                </div>

                <div class="form-group">
                    <label for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                           required placeholder="Confirm password">
                </div>

                <div class="form-group">
                    <label for="role">Role</label>
                    <select id="role" name="role" class="form-control" required>
                        <option value="">Select Role</option>
                        <option value="ADMIN">Admin</option>
                        <option value="OPERATOR">Operator</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeModal('addUserModal')">Cancel</button>
                <button type="submit" class="btn btn-success">
                    <i class="fas fa-save"></i> Create User
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Edit User Modal -->
<div id="editUserModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h4><i class="fas fa-user-edit"></i> Edit User</h4>
            <button type="button" class="close" onclick="closeModal('editUserModal')">&times;</button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/admin/users/manage">
            <div class="modal-body">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" id="editUsername" name="username">

                <div class="form-group">
                    <label>Username</label>
                    <input type="text" id="editUsernameDisplay" class="form-control" readonly>
                </div>

                <div class="form-group">
                    <label for="editRole">Role</label>
                    <select id="editRole" name="role" class="form-control" required>
                        <option value="ADMIN">Admin</option>
                        <option value="OPERATOR">Operator</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="editActive">Status</label>
                    <select id="editActive" name="active" class="form-control" required>
                        <option value="true">Active</option>
                        <option value="false">Inactive</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeModal('editUserModal')">Cancel</button>
                <button type="submit" class="btn btn-warning">
                    <i class="fas fa-save"></i> Update User
                </button>
            </div>
        </form>
    </div>
</div>

<!-- View User Modal -->
<div id="viewUserModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h4><i class="fas fa-user"></i> User Details</h4>
            <button type="button" class="close" onclick="closeModal('viewUserModal')">&times;</button>
        </div>
        <div class="modal-body">
            <div class="user-details">
                <div class="detail-row">
                    <span class="detail-label">Username:</span>
                    <span class="detail-value" id="viewUsername"></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Role:</span>
                    <span class="detail-value" id="viewRole"></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Status:</span>
                    <span class="detail-value" id="viewStatus"></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Created Date:</span>
                    <span class="detail-value" id="viewCreatedDate"></span>
                </div>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" onclick="closeModal('viewUserModal')">Close</button>
        </div>
    </div>
</div>

<!-- Reset Password Modal -->
<div id="resetPasswordModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h4><i class="fas fa-key"></i> Reset Password</h4>
            <button type="button" class="close" onclick="closeModal('resetPasswordModal')">&times;</button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/admin/users/manage">
            <div class="modal-body">
                <input type="hidden" name="action" value="reset-password">
                <input type="hidden" id="resetUsername" name="username">

                <p>Reset password for user: <strong id="resetUsernameDisplay"></strong></p>

                <div class="form-group">
                    <label for="newPassword">New Password</label>
                    <input type="password" id="newPassword" name="newPassword" class="form-control"
                           required placeholder="Enter new password">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeModal('resetPasswordModal')">Cancel
                </button>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-key"></i> Reset Password
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    // Modal functions
    function openAddUserModal() {
        document.getElementById('addUserModal').style.display = 'block';
    }

    function closeModal(modalId) {
        document.getElementById(modalId).style.display = 'none';
    }

    function viewUser(username) {
        // Find user data from the page
        const users = [
            <c:forEach var="user" items="${users}" varStatus="status">
            {
                username: '${user.username}',
                role: '${user.role}',
                active: ${user.active},
                createdDate: '<c:choose><c:when test="${user.createdDate != null}">${user.createdDate.toString().replace('T', ' ').substring(0, 16)}</c:when><c:otherwise>N/A</c:otherwise></c:choose>'
            }<c:if test="${!status.last}">, </c:if>
            </c:forEach>
        ];

        const user = users.find(u => u.username === username);
        if (user) {
            document.getElementById('viewUsername').textContent = user.username;
            document.getElementById('viewRole').innerHTML = user.role === 'ADMIN' ?
                '<span class="badge badge-primary"><i class="fas fa-crown"></i> Admin</span>' :
                '<span class="badge badge-warning"><i class="fas fa-user"></i> Operator</span>';
            document.getElementById('viewStatus').innerHTML = user.active ?
                '<span class="badge badge-success"><i class="fas fa-check"></i> Active</span>' :
                '<span class="badge badge-danger"><i class="fas fa-times"></i> Inactive</span>';
            document.getElementById('viewCreatedDate').textContent = user.createdDate;
            document.getElementById('viewUserModal').style.display = 'block';
        }
    }

    function editUser(username) {
        const users = [
            <c:forEach var="user" items="${users}" varStatus="status">
            {
                username: '${user.username}',
                role: '${user.role}',
                active: ${user.active}
            }<c:if test="${!status.last}">, </c:if>
            </c:forEach>
        ];

        const user = users.find(u => u.username === username);
        if (user) {
            document.getElementById('editUsername').value = user.username;
            document.getElementById('editUsernameDisplay').value = user.username;
            document.getElementById('editRole').value = user.role;
            document.getElementById('editActive').value = user.active.toString();
            document.getElementById('editUserModal').style.display = 'block';
        }
    }

    function toggleUserStatus(username, isActive) {
        const action = isActive ? 'deactivate' : 'activate';
        if (confirm(`Are you sure you want to ${action} user "${username}"?`)) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/admin/users/manage';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'toggle-status';

            const usernameInput = document.createElement('input');
            usernameInput.type = 'hidden';
            usernameInput.name = 'username';
            usernameInput.value = username;

            form.appendChild(actionInput);
            form.appendChild(usernameInput);
            document.body.appendChild(form);
            form.submit();
        }
    }

    function resetPassword(username) {
        document.getElementById('resetUsername').value = username;
        document.getElementById('resetUsernameDisplay').textContent = username;
        document.getElementById('resetPasswordModal').style.display = 'block';
    }

    function deleteUser(username) {
        if (confirm(`Are you sure you want to delete user "${username}"? This action cannot be undone.`)) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/admin/users/manage';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'delete';

            const usernameInput = document.createElement('input');
            usernameInput.type = 'hidden';
            usernameInput.name = 'username';
            usernameInput.value = username;

            form.appendChild(actionInput);
            form.appendChild(usernameInput);
            document.body.appendChild(form);
            form.submit();
        }
    }

    // Close modal when clicking outside
    window.onclick = function (event) {
        const modals = document.getElementsByClassName('modal');
        for (let i = 0; i < modals.length; i++) {
            if (event.target === modals[i]) {
                modals[i].style.display = 'none';
            }
        }
    }

    // Password confirmation validation
    document.getElementById('confirmPassword').addEventListener('input', function () {
        const password = document.getElementById('password').value;
        const confirmPassword = this.value;

        if (password !== confirmPassword) {
            this.setCustomValidity('Passwords do not match');
        } else {
            this.setCustomValidity('');
        }
    });
</script>
</body>
</html>
