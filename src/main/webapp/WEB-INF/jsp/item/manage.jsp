<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Item Management - Pahana Edu Bookshop</title>
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
        .item-grid {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .item-header {
            background: #f8f9fa;
            padding: 20px;
            border-bottom: 1px solid #dee2e6;
            display: flex;
            justify-content: between;
            align-items: center;
        }
        .item-table {
            width: 100%;
            border-collapse: collapse;
        }
        .item-table th,
        .item-table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }
        .item-table th {
            background: #f8f9fa;
            font-weight: 600;
            color: #495057;
        }
        .item-table tbody tr:hover {
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
        .badge-warning {
            background: #fff3cd;
            color: #856404;
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
        .item-actions {
            display: flex;
            gap: 5px;
        }
        .no-items {
            text-align: center;
            padding: 40px;
            color: #6c757d;
        }
        .item-details {
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
        .stock-status {
            font-weight: bold;
        }
        .price-display {
            font-weight: 600;
            color: #28a745;
        }
    </style>
</head>
<body>
    <%@ include file="../../includes/navigation.jsp" %>

    <div class="main-content">
        <div class="container-fluid">
            <div class="page-header">
                <h1><i class="fas fa-box-open"></i> Item Management</h1>
                <p class="text-muted">Search, manage, and view inventory items</p>
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
                <form class="search-form" action="${pageContext.request.contextPath}/item/search" method="get">
                    <input type="text" name="itemName" class="search-input" 
                           placeholder="Search by item name..." 
                           value="${param.itemName}">
                    <input type="text" name="category" class="search-input" 
                           placeholder="Filter by category..." 
                           value="${param.category}">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-search"></i> Search
                    </button>
                    <a href="${pageContext.request.contextPath}/item/manage" class="btn btn-secondary">
                        <i class="fas fa-refresh"></i> Clear
                    </a>
                    <button type="button" class="btn btn-success" onclick="showAddItemModal()">
                        <i class="fas fa-plus"></i> Add Item
                    </button>
                </form>
            </div>

            <!-- Item List -->
            <div class="item-grid">
                <div class="item-header">
                    <h3><i class="fas fa-list"></i> Items</h3>
                    <span class="text-muted">
                        <c:choose>
                            <c:when test="${not empty items}">
                                ${items.size()} item(s) found
                            </c:when>
                            <c:otherwise>
                                No items found
                            </c:otherwise>
                        </c:choose>
                    </span>
                </div>

                <c:choose>
                    <c:when test="${not empty items}">
                        <table class="item-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Category</th>
                                    <th>Price</th>
                                    <th>Stock</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${items}">
                                    <tr>
                                        <td><strong>${item.itemId}</strong></td>
                                        <td>${item.itemName}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty item.category}">
                                                    ${item.category}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">Uncategorized</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="price-display">
                                            <fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="Rs. " maxFractionDigits="2"/>
                                        </td>
                                        <td class="stock-status">
                                            <c:choose>
                                                <c:when test="${item.stockQuantity == 0}">
                                                    <span class="badge badge-danger">Out of Stock</span>
                                                </c:when>
                                                <c:when test="${item.stockQuantity <= 5}">
                                                    <span class="badge badge-warning">${item.stockQuantity} (Low Stock)</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-success">${item.stockQuantity} in stock</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${item.stockQuantity > 0}">
                                                    <span class="badge badge-success">Available</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-danger">Out of Stock</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="item-actions">
                                                <button type="button" class="btn btn-primary" 
                                                        onclick="viewItem('${item.itemId}')">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                                <button type="button" class="btn btn-warning" 
                                                        onclick="editItem('${item.itemId}')">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <button type="button" class="btn btn-danger" 
                                                        onclick="deleteItem('${item.itemId}', '${item.itemName}')">
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
                        <div class="no-items">
                            <i class="fas fa-box-open" style="font-size: 48px; color: #dee2e6; margin-bottom: 15px;"></i>
                            <h4>No items found</h4>
                            <p>Start by adding your first item or try a different search term.</p>
                            <button type="button" class="btn btn-success" onclick="showAddItemModal()">
                                <i class="fas fa-plus"></i> Add First Item
                            </button>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Add/Edit Item Modal -->
    <div id="itemModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h4 id="modalTitle"><i class="fas fa-plus"></i> Add New Item</h4>
                <button type="button" class="close" onclick="closeModal()">&times;</button>
            </div>
            <form id="itemForm" method="post">
                <div class="modal-body">
                    <input type="hidden" id="itemId" name="itemId">
                    
                    <div class="form-group">
                        <label for="itemName">Item Name <span style="color: red;">*</span></label>
                        <input type="text" class="form-control" id="itemName" name="itemName" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea class="form-control" id="description" name="description" rows="3"></textarea>
                    </div>
                    
                    <div class="form-group">
                        <label for="category">Category</label>
                        <input type="text" class="form-control" id="category" name="category">
                    </div>
                    
                    <div class="form-group">
                        <label for="unitPrice">Unit Price (Rs.) <span style="color: red;">*</span></label>
                        <input type="number" class="form-control" id="unitPrice" name="unitPrice" 
                               step="0.01" min="0" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="stockQuantity">Stock Quantity <span style="color: red;">*</span></label>
                        <input type="number" class="form-control" id="stockQuantity" name="stockQuantity" 
                               min="0" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancel</button>
                    <button type="submit" class="btn btn-success" id="submitBtn">
                        <i class="fas fa-save"></i> Save Item
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- View Item Modal -->
    <div id="viewModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h4><i class="fas fa-box-open"></i> Item Details</h4>
                <button type="button" class="close" onclick="closeViewModal()">&times;</button>
            </div>
            <div class="modal-body" id="itemDetails">
                <!-- Item details will be loaded here -->
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" onclick="editFromView()">
                    <i class="fas fa-edit"></i> Edit Item
                </button>
                <button type="button" class="btn btn-secondary" onclick="closeViewModal()">Close</button>
            </div>
        </div>
    </div>

    <script>
        let currentItemId = null;

        function showAddItemModal() {
            document.getElementById('modalTitle').innerHTML = '<i class="fas fa-plus"></i> Add New Item';
            document.getElementById('itemForm').action = '${pageContext.request.contextPath}/item/add';
            document.getElementById('submitBtn').innerHTML = '<i class="fas fa-save"></i> Save Item';
            
            // Clear form
            document.getElementById('itemId').value = '';
            document.getElementById('itemName').value = '';
            document.getElementById('description').value = '';
            document.getElementById('category').value = '';
            document.getElementById('unitPrice').value = '';
            document.getElementById('stockQuantity').value = '';
            
            document.getElementById('itemModal').style.display = 'block';
        }

        function editItem(itemId) {
            currentItemId = itemId;
            
            // Fetch item data and populate form
            fetch('${pageContext.request.contextPath}/item/edit?itemId=' + itemId)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to fetch item data');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.error) {
                        throw new Error(data.error);
                    }
                    
                    // Populate form with fetched data
                    document.getElementById('itemId').value = data.itemId;
                    document.getElementById('itemName').value = data.itemName || '';
                    document.getElementById('description').value = data.description || '';
                    document.getElementById('category').value = data.category || '';
                    document.getElementById('unitPrice').value = data.unitPrice || '';
                    document.getElementById('stockQuantity').value = data.stockQuantity || '';
                    
                    // Update modal for editing
                    document.getElementById('modalTitle').innerHTML = '<i class="fas fa-edit"></i> Edit Item';
                    document.getElementById('itemForm').action = '${pageContext.request.contextPath}/item/edit';
                    document.getElementById('submitBtn').innerHTML = '<i class="fas fa-save"></i> Update Item';
                    
                    document.getElementById('itemModal').style.display = 'block';
                })
                .catch(error => {
                    console.error('Error fetching item data:', error);
                    alert('Error loading item data. Please try again.');
                });
        }

        function viewItem(itemId) {
            currentItemId = itemId;
            
            // Fetch item data from dedicated view endpoint
            fetch('${pageContext.request.contextPath}/item/view?itemId=' + itemId)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to fetch item data');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.error) {
                        throw new Error(data.error);
                    }
                    
                    console.log('Item data received:', data); // Debug log
                    
                    // Safe string handling with proper fallbacks
                    const itemId = data.itemId || 'N/A';
                    const itemName = data.itemName && data.itemName.trim() !== '' ? data.itemName : 'N/A';
                    const description = data.description && data.description.trim() !== '' ? data.description : 'No description available';
                    const category = data.category && data.category.trim() !== '' ? data.category : 'Uncategorized';
                    const unitPrice = data.unitPrice ? 'Rs. ' + parseFloat(data.unitPrice).toFixed(2) : 'N/A';
                    const stockQuantity = data.stockQuantity !== null ? data.stockQuantity : 0;
                    
                    // Determine stock status
                    let stockStatus = '';
                    let stockClass = '';
                    if (stockQuantity === 0) {
                        stockStatus = 'Out of Stock';
                        stockClass = 'badge-danger';
                    } else if (stockQuantity <= 5) {
                        stockStatus = stockQuantity + ' (Low Stock)';
                        stockClass = 'badge-warning';
                    } else {
                        stockStatus = stockQuantity + ' in stock';
                        stockClass = 'badge-success';
                    }
                    
                    // Build HTML using string concatenation to avoid template literal issues
                    let detailsHtml = '<div class="item-details">';
                    
                    detailsHtml += '<div class="detail-row">';
                    detailsHtml += '<div class="detail-label">Item ID:</div>';
                    detailsHtml += '<div class="detail-value"><strong>' + itemId + '</strong></div>';
                    detailsHtml += '</div>';
                    
                    detailsHtml += '<div class="detail-row">';
                    detailsHtml += '<div class="detail-label">Name:</div>';
                    detailsHtml += '<div class="detail-value">' + itemName + '</div>';
                    detailsHtml += '</div>';
                    
                    detailsHtml += '<div class="detail-row">';
                    detailsHtml += '<div class="detail-label">Description:</div>';
                    detailsHtml += '<div class="detail-value">' + description + '</div>';
                    detailsHtml += '</div>';
                    
                    detailsHtml += '<div class="detail-row">';
                    detailsHtml += '<div class="detail-label">Category:</div>';
                    detailsHtml += '<div class="detail-value">' + category + '</div>';
                    detailsHtml += '</div>';
                    
                    detailsHtml += '<div class="detail-row">';
                    detailsHtml += '<div class="detail-label">Unit Price:</div>';
                    detailsHtml += '<div class="detail-value price-display">' + unitPrice + '</div>';
                    detailsHtml += '</div>';
                    
                    detailsHtml += '<div class="detail-row">';
                    detailsHtml += '<div class="detail-label">Stock Quantity:</div>';
                    detailsHtml += '<div class="detail-value">';
                    detailsHtml += '<span class="badge ' + stockClass + '">' + stockStatus + '</span>';
                    detailsHtml += '</div>';
                    detailsHtml += '</div>';
                    
                    detailsHtml += '</div>';
                    
                    document.getElementById('itemDetails').innerHTML = detailsHtml;
                    document.getElementById('viewModal').style.display = 'block';
                })
                .catch(error => {
                    console.error('Error fetching item data:', error);
                    alert('Error loading item data. Please try again.');
                });
        }

        function editFromView() {
            closeViewModal();
            editItem(currentItemId);
        }

        function deleteItem(itemId, itemName) {
            if (confirm('Are you sure you want to delete "' + itemName + '"? This action cannot be undone.')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/item/delete';
                
                const itemIdInput = document.createElement('input');
                itemIdInput.type = 'hidden';
                itemIdInput.name = 'itemId';
                itemIdInput.value = itemId;
                
                form.appendChild(itemIdInput);
                document.body.appendChild(form);
                form.submit();
            }
        }

        function closeModal() {
            document.getElementById('itemModal').style.display = 'none';
        }

        function closeViewModal() {
            document.getElementById('viewModal').style.display = 'none';
        }

        // Close modals when clicking outside
        window.onclick = function(event) {
            const itemModal = document.getElementById('itemModal');
            const viewModal = document.getElementById('viewModal');
            
            if (event.target == itemModal) {
                closeModal();
            }
            if (event.target == viewModal) {
                closeViewModal();
            }
        }

        // Handle form submission
        document.getElementById('itemForm').addEventListener('submit', function(e) {
            const itemName = document.getElementById('itemName').value.trim();
            const unitPrice = parseFloat(document.getElementById('unitPrice').value);
            const stockQuantity = parseInt(document.getElementById('stockQuantity').value);
            
            if (!itemName) {
                e.preventDefault();
                alert('Please enter item name');
                document.getElementById('itemName').focus();
                return;
            }
            
            if (isNaN(unitPrice) || unitPrice < 0) {
                e.preventDefault();
                alert('Please enter a valid unit price');
                document.getElementById('unitPrice').focus();
                return;
            }
            
            if (isNaN(stockQuantity) || stockQuantity < 0) {
                e.preventDefault();
                alert('Please enter a valid stock quantity');
                document.getElementById('stockQuantity').focus();
                return;
            }
        });
    </script>
</body>
</html>