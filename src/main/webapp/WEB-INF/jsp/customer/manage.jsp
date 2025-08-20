<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Management - Pahana Edu Bookshop</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .search-bar {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        .search-form {
            display: flex;
            gap: 15px;
            align-items: center;
        }
        .search-input {
            flex: 1;
            padding: 10px 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
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
        .customer-grid {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .customer-header {
            background: #f8f9fa;
            padding: 20px;
            border-bottom: 1px solid #dee2e6;
            display: flex;
            justify-content: between;
            align-items: center;
        }
        .customer-table {
            width: 100%;
            border-collapse: collapse;
        }
        .customer-table th,
        .customer-table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }
        .customer-table th {
            background: #f8f9fa;
            font-weight: 600;
            color: #495057;
        }
        .customer-table tbody tr:hover {
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
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
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
        }
        .form-control:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 0.2rem rgba(0,123,255,.25);
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
        .customer-actions {
            display: flex;
            gap: 5px;
        }
        .no-customers {
            text-align: center;
            padding: 40px;
            color: #6c757d;
        }
        .customer-details {
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
    </style>
</head>
<body>
    <%@ include file="../../includes/navigation.jsp" %>

    <div class="main-content">
        <div class="container-fluid">
            <div class="page-header">
                <h1><i class="fas fa-users"></i> Customer Management</h1>
                <p class="text-muted">Search, manage, and view customer information</p>
            </div>

            <!-- Success/Error Messages -->
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i>
                    ${sessionScope.successMessage}
                </div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>

            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-circle"></i>
                    ${sessionScope.errorMessage}
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>

            <!-- Search Bar -->
            <div class="search-bar">
                <form class="search-form" action="${pageContext.request.contextPath}/customer/search" method="get">
                    <input type="text" name="telephoneNumber" class="search-input" 
                           placeholder="Search by telephone number..." 
                           value="${param.telephoneNumber}">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-search"></i> Search
                    </button>
                    <a href="${pageContext.request.contextPath}/customer/manage" class="btn btn-secondary">
                        <i class="fas fa-refresh"></i> Clear
                    </a>
                    <button type="button" class="btn btn-success" onclick="showAddCustomerModal()">
                        <i class="fas fa-user-plus"></i> Add Customer
                    </button>
                </form>
            </div>

            <!-- Customer List -->
            <div class="customer-grid">
                <div class="customer-header">
                    <h3><i class="fas fa-list"></i> Customers</h3>
                    <span class="text-muted">
                        <c:choose>
                            <c:when test="${not empty customers}">
                                ${customers.size()} customer(s) found
                            </c:when>
                            <c:otherwise>
                                No customers found
                            </c:otherwise>
                        </c:choose>
                    </span>
                </div>

                <c:choose>
                    <c:when test="${not empty customers}">
                        <table class="customer-table">
                            <thead>
                                <tr>
                                    <th>Account Number</th>
                                    <th>Name</th>
                                    <th>Telephone</th>
                                    <th>Address</th>
                                    <th>Registration Date</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="customer" items="${customers}">
                                    <tr>
                                        <td><strong>${customer.accountNumber}</strong></td>
                                        <td>${customer.name}</td>
                                        <td>${customer.telephoneNumber}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty customer.address}">
                                                    ${customer.address.length() > 30 ? customer.address.substring(0, 30).concat('...') : customer.address}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">Not provided</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${customer.registrationDateAsDate}" pattern="MMM dd, yyyy"/>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${customer.active}">
                                                    <span class="badge badge-success">Active</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-danger">Inactive</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="customer-actions">
                                                <button type="button" class="btn btn-primary" 
                                                        onclick="viewCustomer('${customer.accountNumber}')">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                                <button type="button" class="btn btn-warning" 
                                                        onclick="editCustomer('${customer.accountNumber}')">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <c:choose>
                                                    <c:when test="${customer.active}">
                                                        <button type="button" class="btn btn-secondary" 
                                                                onclick="toggleStatus('${customer.accountNumber}', false)">
                                                            <i class="fas fa-ban"></i>
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button type="button" class="btn btn-success" 
                                                                onclick="toggleStatus('${customer.accountNumber}', true)">
                                                            <i class="fas fa-check"></i>
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                                <button type="button" class="btn btn-danger" 
                                                        onclick="deleteCustomer('${customer.accountNumber}')">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="no-customers">
                            <i class="fas fa-users" style="font-size: 48px; color: #dee2e6; margin-bottom: 15px;"></i>
                            <h4>No customers found</h4>
                            <p>Start by adding your first customer or try a different search term.</p>
                            <button type="button" class="btn btn-success" onclick="showAddCustomerModal()">
                                <i class="fas fa-user-plus"></i> Add First Customer
                            </button>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Add/Edit Customer Modal -->
    <div id="customerModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h4 id="modalTitle"><i class="fas fa-user-plus"></i> Add New Customer</h4>
                <button type="button" class="close" onclick="closeModal()">&times;</button>
            </div>
            <form id="customerForm" method="post">
                <div class="modal-body">
                    <div class="form-group">
                        <label for="accountNumber">Account Number</label>
                        <input type="text" class="form-control" id="accountNumber" name="accountNumber" 
                               placeholder="Leave blank for auto-generation">
                        <small class="form-text text-muted">Leave blank to auto-generate account number</small>
                    </div>
                    <div class="form-group">
                        <label for="name">Name <span style="color: red;">*</span></label>
                        <input type="text" class="form-control" id="name" name="name" required>
                    </div>
                    <div class="form-group">
                        <label for="telephoneNumber">Telephone Number</label>
                        <input type="text" class="form-control" id="telephoneNumber" name="telephoneNumber">
                    </div>
                    <div class="form-group">
                        <label for="address">Address</label>
                        <textarea class="form-control" id="address" name="address" rows="3"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancel</button>
                    <button type="submit" class="btn btn-success" id="submitBtn">
                        <i class="fas fa-save"></i> Save Customer
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- View Customer Modal -->
    <div id="viewModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h4><i class="fas fa-user"></i> Customer Details</h4>
                <button type="button" class="close" onclick="closeViewModal()">&times;</button>
            </div>
            <div class="modal-body" id="customerDetails">
                <!-- Customer details will be loaded here -->
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" onclick="editFromView()">
                    <i class="fas fa-edit"></i> Edit Customer
                </button>
                <button type="button" class="btn btn-secondary" onclick="closeViewModal()">Close</button>
            </div>
        </div>
    </div>

    <script>
        let currentCustomerAccount = null;

        function showAddCustomerModal() {
            document.getElementById('modalTitle').innerHTML = '<i class="fas fa-user-plus"></i> Add New Customer';
            document.getElementById('customerForm').action = '${pageContext.request.contextPath}/customer/add';
            document.getElementById('submitBtn').innerHTML = '<i class="fas fa-save"></i> Save Customer';
            
            // Clear form
            document.getElementById('accountNumber').value = '';
            document.getElementById('accountNumber').readOnly = false;
            document.getElementById('accountNumber').placeholder = 'Leave blank for auto-generation';
            document.getElementById('name').value = '';
            document.getElementById('telephoneNumber').value = '';
            document.getElementById('address').value = '';
            
            document.getElementById('customerModal').style.display = 'block';
        }

        function editCustomer(accountNumber) {
            currentCustomerAccount = accountNumber;
            
            // Fetch customer data and populate form
            fetch('${pageContext.request.contextPath}/customer/edit?accountNumber=' + accountNumber)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to fetch customer data');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.error) {
                        throw new Error(data.error);
                    }
                    
                    // Populate form with fetched data
                    document.getElementById('accountNumber').value = data.accountNumber;
                    document.getElementById('accountNumber').readOnly = true;
                    document.getElementById('accountNumber').placeholder = '';
                    document.getElementById('name').value = data.name || '';
                    document.getElementById('telephoneNumber').value = data.telephoneNumber || '';
                    document.getElementById('address').value = data.address || '';
                    
                    // Update modal for editing
                    document.getElementById('modalTitle').innerHTML = '<i class="fas fa-user-edit"></i> Edit Customer';
                    document.getElementById('customerForm').action = '${pageContext.request.contextPath}/customer/edit';
                    document.getElementById('submitBtn').innerHTML = '<i class="fas fa-save"></i> Update Customer';
                    
                    document.getElementById('customerModal').style.display = 'block';
                })
                .catch(error => {
                    console.error('Error fetching customer data:', error);
                    alert('Error loading customer data. Please try again.');
                });
        }

        function viewCustomer(accountNumber) {
            currentCustomerAccount = accountNumber;
            
            // Fetch customer data from dedicated view endpoint
            fetch('${pageContext.request.contextPath}/customer/view?accountNumber=' + accountNumber)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to fetch customer data');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.error) {
                        throw new Error(data.error);
                    }
                    
                    console.log('Customer data received:', data); // Debug log
                    
                    // Format registration date
                    let formattedDate = 'Not available';
                    if (data.registrationDate) {
                        try {
                            const date = new Date(data.registrationDate);
                            formattedDate = date.toLocaleDateString('en-US', { 
                                year: 'numeric', 
                                month: 'short', 
                                day: '2-digit' 
                            });
                        } catch (e) {
                            formattedDate = data.registrationDate;
                        }
                    }
                    
                    // Format status
                    const isActive = data.active === true;
                    const statusText = isActive ? 'Active' : 'Inactive';
                    const statusClass = isActive ? 'badge-success' : 'badge-danger';
                    
                    // Safe string handling with proper fallbacks
                    const accountNumber = data.accountNumber && data.accountNumber.trim() !== '' ? data.accountNumber : 'N/A';
                    const name = data.name && data.name.trim() !== '' ? data.name : 'N/A';
                    const telephoneNumber = data.telephoneNumber && data.telephoneNumber.trim() !== '' ? data.telephoneNumber : 'Not provided';
                    const address = data.address && data.address.trim() !== '' ? data.address : 'Not provided';
                    
                    // Build HTML using string concatenation to avoid template literal issues
                    let detailsHtml = '<div class="customer-details">';
                    
                    detailsHtml += '<div class="detail-row">';
                    detailsHtml += '<div class="detail-label">Account Number:</div>';
                    detailsHtml += '<div class="detail-value"><strong>' + accountNumber + '</strong></div>';
                    detailsHtml += '</div>';
                    
                    detailsHtml += '<div class="detail-row">';
                    detailsHtml += '<div class="detail-label">Name:</div>';
                    detailsHtml += '<div class="detail-value">' + name + '</div>';
                    detailsHtml += '</div>';
                    
                    detailsHtml += '<div class="detail-row">';
                    detailsHtml += '<div class="detail-label">Telephone:</div>';
                    detailsHtml += '<div class="detail-value">' + telephoneNumber + '</div>';
                    detailsHtml += '</div>';
                    
                    detailsHtml += '<div class="detail-row">';
                    detailsHtml += '<div class="detail-label">Address:</div>';
                    detailsHtml += '<div class="detail-value">' + address + '</div>';
                    detailsHtml += '</div>';
                    
                    detailsHtml += '<div class="detail-row">';
                    detailsHtml += '<div class="detail-label">Registration Date:</div>';
                    detailsHtml += '<div class="detail-value">' + formattedDate + '</div>';
                    detailsHtml += '</div>';
                    
                    detailsHtml += '<div class="detail-row">';
                    detailsHtml += '<div class="detail-label">Status:</div>';
                    detailsHtml += '<div class="detail-value">';
                    detailsHtml += '<span class="badge ' + statusClass + '">' + statusText + '</span>';
                    detailsHtml += '</div>';
                    detailsHtml += '</div>';
                    
                    detailsHtml += '</div>';
                    
                    document.getElementById('customerDetails').innerHTML = detailsHtml;
                    document.getElementById('viewModal').style.display = 'block';
                })
                .catch(error => {
                    console.error('Error fetching customer data:', error);
                    alert('Error loading customer data. Please try again.');
                });
        }

        function editFromView() {
            closeViewModal();
            editCustomer(currentCustomerAccount);
        }

        function toggleStatus(accountNumber, activate) {
            const action = activate ? 'activate' : 'deactivate';
            if (confirm('Are you sure you want to ' + action + ' this customer?')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/customer/toggle-status';
                
                const accountInput = document.createElement('input');
                accountInput.type = 'hidden';
                accountInput.name = 'accountNumber';
                accountInput.value = accountNumber;
                
                const activeInput = document.createElement('input');
                activeInput.type = 'hidden';
                activeInput.name = 'active';
                activeInput.value = activate;
                
                form.appendChild(accountInput);
                form.appendChild(activeInput);
                document.body.appendChild(form);
                form.submit();
            }
        }

        function deleteCustomer(accountNumber) {
            if (confirm('Are you sure you want to delete this customer? This will deactivate the customer account.')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/customer/delete';
                
                const accountInput = document.createElement('input');
                accountInput.type = 'hidden';
                accountInput.name = 'accountNumber';
                accountInput.value = accountNumber;
                
                form.appendChild(accountInput);
                document.body.appendChild(form);
                form.submit();
            }
        }

        function closeModal() {
            document.getElementById('customerModal').style.display = 'none';
        }

        function closeViewModal() {
            document.getElementById('viewModal').style.display = 'none';
        }

        // Close modals when clicking outside
        window.onclick = function(event) {
            const customerModal = document.getElementById('customerModal');
            const viewModal = document.getElementById('viewModal');
            
            if (event.target == customerModal) {
                closeModal();
            }
            if (event.target == viewModal) {
                closeViewModal();
            }
        }

        // Handle form submission
        document.getElementById('customerForm').addEventListener('submit', function(e) {
            const name = document.getElementById('name').value.trim();
            if (!name) {
                e.preventDefault();
                alert('Please enter customer name');
                document.getElementById('name').focus();
            }
        });
    </script>
</body>
</html>