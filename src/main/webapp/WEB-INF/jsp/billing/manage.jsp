<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Billing Management - Pahana Edu Bookshop</title>
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
        .bill-grid {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .bill-header {
            background: #f8f9fa;
            padding: 20px;
            border-bottom: 1px solid #dee2e6;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .bill-table {
            width: 100%;
            border-collapse: collapse;
        }
        .bill-table th,
        .bill-table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }
        .bill-table th {
            background: #f8f9fa;
            font-weight: 600;
            color: #495057;
        }
        .bill-table tbody tr:hover {
            background: #f8f9fa;
        }
        .badge {
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
        }
        .badge-pending {
            background: #fff3cd;
            color: #856404;
        }
        .badge-paid {
            background: #d4edda;
            color: #155724;
        }
        .badge-cancelled {
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
            max-width: 800px;
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
        .bill-actions {
            display: flex;
            gap: 5px;
        }
        .no-bills {
            text-align: center;
            padding: 40px;
            color: #6c757d;
        }
        .bill-details {
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
        .price-display {
            font-weight: 600;
            color: #28a745;
        }
        .item-selection {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 15px;
        }
        .item-list {
            max-height: 200px;
            overflow-y: auto;
            border: 1px solid #dee2e6;
            border-radius: 5px;
            margin-top: 10px;
        }
        .item-entry {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px;
            border-bottom: 1px solid #dee2e6;
        }
        .item-entry:last-child {
            border-bottom: none;
        }
        .item-entry:hover {
            background: #e9ecef;
        }
        .quantity-input {
            width: 80px;
            padding: 5px;
            border: 1px solid #ddd;
            border-radius: 3px;
            margin: 0 5px;
        }
        .bill-items-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        .bill-items-table th,
        .bill-items-table td {
            padding: 8px 12px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }
        .bill-items-table th {
            background: #e9ecef;
            font-weight: 600;
            color: #495057;
        }
        .btn-sm {
            padding: 5px 10px;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <%@ include file="../../includes/navigation.jsp" %>

    <div class="main-content">
        <div class="container-fluid">
            <div class="page-header">
                <h1><i class="fas fa-receipt"></i> Billing Management</h1>
                <p class="text-muted">Create bills, manage transactions, and track purchases</p>
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
                <form class="search-form" action="${pageContext.request.contextPath}/billing/search" method="get">
                    <input type="text" name="customerName" class="search-input" 
                           placeholder="Search by customer name..." 
                           value="${param.customerName}">
                    <select name="status" class="search-input">
                        <option value="">All Status</option>
                        <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="PAID" ${param.status == 'PAID' ? 'selected' : ''}>Paid</option>
                        <option value="CANCELLED" ${param.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                    </select>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-search"></i> Search
                    </button>
                    <a href="${pageContext.request.contextPath}/billing/manage" class="btn btn-secondary">
                        <i class="fas fa-refresh"></i> Clear
                    </a>
                    <button type="button" class="btn btn-success" onclick="showCreateBillModal()">
                        <i class="fas fa-plus"></i> Create Bill
                    </button>
                </form>
            </div>

            <!-- Bills List -->
            <div class="bill-grid">
                <div class="bill-header">
                    <h3><i class="fas fa-list"></i> Bills</h3>
                    <span class="text-muted">
                        <c:choose>
                            <c:when test="${not empty bills}">
                                ${bills.size()} bill(s) found
                            </c:when>
                            <c:otherwise>
                                No bills found
                            </c:otherwise>
                        </c:choose>
                    </span>
                </div>

                <c:choose>
                    <c:when test="${not empty bills}">
                        <table class="bill-table">
                            <thead>
                                <tr>
                                    <th>Bill ID</th>
                                    <th>Customer</th>
                                    <th>Date</th>
                                    <th>Items</th>
                                    <th>Total Amount</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="bill" items="${bills}">
                                    <tr>
                                        <td><strong>${bill.billId}</strong></td>
                                        <td>
                                            <strong>${bill.customer.name}</strong><br>
                                            <small class="text-muted">${bill.customer.accountNumber}</small>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${billDatesMap[bill.billId]}" pattern="MMM dd, yyyy"/>
                                        </td>
                                        <td>
                                            <span class="text-muted">${bill.totalItemCount} item(s)</span>
                                        </td>
                                        <td class="price-display">
                                            <fmt:formatNumber value="${bill.totalAmount}" type="currency" currencySymbol="Rs. " maxFractionDigits="2"/>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${bill.status == 'PENDING'}">
                                                    <span class="badge badge-pending">Pending</span>
                                                </c:when>
                                                <c:when test="${bill.status == 'PAID'}">
                                                    <span class="badge badge-paid">Paid</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-cancelled">Cancelled</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="bill-actions">
                                                <button type="button" class="btn btn-primary" 
                                                        onclick="viewBill('${bill.billId}')">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                                <c:if test="${bill.status == 'PENDING'}">
                                                    <button type="button" class="btn btn-warning" 
                                                            onclick="manageBillItems('${bill.billId}')">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <button type="button" class="btn btn-success" 
                                                            onclick="completeBill('${bill.billId}')">
                                                        <i class="fas fa-check"></i>
                                                    </button>
                                                    <button type="button" class="btn btn-secondary" 
                                                            onclick="cancelBill('${bill.billId}')">
                                                        <i class="fas fa-times"></i>
                                                    </button>
                                                </c:if>
                                                <button type="button" class="btn btn-danger" 
                                                        onclick="deleteBill('${bill.billId}')">
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
                        <div class="no-bills">
                            <i class="fas fa-receipt" style="font-size: 48px; color: #dee2e6; margin-bottom: 15px;"></i>
                            <h4>No bills found</h4>
                            <p>Start by creating your first bill or try a different search term.</p>
                            <button type="button" class="btn btn-success" onclick="showCreateBillModal()">
                                <i class="fas fa-plus"></i> Create First Bill
                            </button>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Create Bill Modal -->
    <div id="createBillModal" class="modal">
        <div class="modal-content" style="max-width: 900px; width: 90%; max-height: 90vh; overflow-y: auto;">
            <div class="modal-header">
                <h4><i class="fas fa-plus"></i> Create New Bill</h4>
                <button type="button" class="close" onclick="closeCreateModal()">&times;</button>
            </div>
            <form id="createBillForm" method="post" action="${pageContext.request.contextPath}/billing/create">
                <div class="modal-body">
                    <!-- Customer Selection -->
                    <div class="form-group">
                        <label for="modalCustomerSelect">Select Customer <span style="color: red;">*</span></label>
                        <select class="form-control" id="modalCustomerSelect" name="accountNumber" required>
                            <option value="">-- Select Customer --</option>
                            <c:forEach var="customer" items="${customers}">
                                <option value="${customer.accountNumber}">${customer.name} (${customer.accountNumber})</option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <!-- Items Section -->
                    <div class="form-group" style="margin-top: 30px;">
                        <label><i class="fas fa-shopping-cart"></i> Items <span style="color: red;">*</span></label>
                        <div style="border: 1px solid #dee2e6; border-radius: 8px; padding: 15px; background: #f8f9fa;">
                            <button type="button" class="btn btn-success btn-sm" onclick="addModalItemRow()" style="margin-bottom: 15px;">
                                <i class="fas fa-plus"></i> Add Item
                            </button>
                            
                            <div id="modalItemsContainer">
                                <!-- Initial item row -->
                                <div class="modal-item-row" data-row="0" style="background: white; padding: 15px; margin-bottom: 10px; border-radius: 6px; border: 1px solid #e9ecef;">
                                    <div style="display: flex; gap: 10px; align-items: end; margin-bottom: 10px;">
                                        <div style="flex: 2;">
                                            <label style="font-size: 12px; font-weight: 600;">Item</label>
                                            <select name="items[0].itemId" class="form-control modal-item-dropdown" onchange="updateModalItemPrice(this, 0)" required style="font-size: 14px;">
                                                <option value="">-- Select Item --</option>
                                                <c:forEach var="item" items="${availableItems}">
                                                    <option value="${item.itemId}" 
                                                            data-price="${item.unitPrice}" 
                                                            data-stock="${item.stockQuantity}">
                                                        ${item.itemName} - Rs. <fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div style="flex: 1;">
                                            <label style="font-size: 12px; font-weight: 600;">Quantity</label>
                                            <input type="number" name="items[0].quantity" class="form-control" 
                                                   min="1" value="1" onchange="calculateModalRowTotal(0)" required style="font-size: 14px;">
                                        </div>
                                        <div style="flex: 1;">
                                            <label style="font-size: 12px; font-weight: 600;">Unit Price</label>
                                            <input type="text" class="form-control" id="modalUnitPrice0" readonly 
                                                   style="background: #f8f9fa; font-size: 14px; font-weight: bold;">
                                        </div>
                                        <div style="flex: 1;">
                                            <label style="font-size: 12px; font-weight: 600;">Total</label>
                                            <input type="text" class="form-control modal-item-total" id="modalItemTotal0" readonly 
                                                   style="background: #f8f9fa; font-size: 14px; font-weight: bold; color: #28a745;">
                                        </div>
                                        <div>
                                            <label style="font-size: 12px; font-weight: 600;">&nbsp;</label>
                                            <button type="button" class="btn btn-danger btn-sm modal-remove-btn" onclick="removeModalItemRow(this)" style="display: none;">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </div>
                                    <div class="modal-stock-info" id="modalStockInfo0" style="font-size: 12px; color: #6c757d;"></div>
                                    <div class="modal-stock-warning" id="modalStockWarning0" style="font-size: 12px; color: #dc3545;"></div>
                                </div>
                            </div>
                            
                            <!-- Total Section -->
                            <div style="border-top: 2px solid #007bff; padding-top: 15px; margin-top: 15px;">
                                <div style="text-align: right; font-size: 18px; font-weight: bold; color: #28a745;">
                                    Grand Total: Rs. <span id="modalGrandTotal">0.00</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeCreateModal()">Cancel</button>
                    <button type="submit" class="btn btn-success" id="modalSubmitBtn" disabled>
                        <i class="fas fa-save"></i> Create Bill
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- View Bill Modal -->
    <div id="viewBillModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h4><i class="fas fa-receipt"></i> Bill Details</h4>
                <button type="button" class="close" onclick="closeViewModal()">&times;</button>
            </div>
            <div class="modal-body" id="billDetails">
                <!-- Bill details will be loaded here -->
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeViewModal()">Close</button>
            </div>
        </div>
    </div>

    <!-- Manage Bill Items Modal -->
    <div id="manageBillItemsModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h4><i class="fas fa-edit"></i> Manage Bill Items</h4>
                <button type="button" class="close" onclick="closeManageModal()">&times;</button>
            </div>
            <div class="modal-body">
                <div id="currentBillInfo">
                    <!-- Current bill info will be displayed here -->
                </div>
                
                <div class="item-selection">
                    <h5><i class="fas fa-plus"></i> Add Item to Bill</h5>
                    <div class="form-group">
                        <label for="itemSelect">Select Item:</label>
                        <select class="form-control" id="itemSelect">
                            <option value="">-- Select Item --</option>
                            <c:forEach var="item" items="${availableItems}">
                                <option value="${item.itemId}" 
                                        data-price="${item.unitPrice}"
                                        data-stock="${item.stockQuantity}">
                                    ${item.itemName} - Rs. <fmt:formatNumber value="${item.unitPrice}" maxFractionDigits="2"/> 
                                    (Stock: ${item.stockQuantity})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="quantityInput">Quantity:</label>
                        <input type="number" class="form-control" id="quantityInput" min="1" value="1">
                    </div>
                    <button type="button" class="btn btn-success" onclick="addItemToBill()">
                        <i class="fas fa-plus"></i> Add Item
                    </button>
                </div>

                <div id="billItemsList">
                    <!-- Current bill items will be displayed here -->
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeManageModal()">Close</button>
                <button type="button" class="btn btn-success" onclick="refreshBillDisplay()">
                    <i class="fas fa-refresh"></i> Refresh
                </button>
            </div>
        </div>
    </div>

    <script>
        let currentBillId = null;

        function showCreateBillModal() {
            document.getElementById('createBillModal').style.display = 'block';
        }

        function closeCreateModal() {
            document.getElementById('createBillModal').style.display = 'none';
        }

        function viewBill(billId) {
            currentBillId = billId;
            
            fetch('${pageContext.request.contextPath}/billing/view?billId=' + billId)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to fetch bill data');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.error) {
                        throw new Error(data.error);
                    }
                    
                    displayBillDetails(data);
                    document.getElementById('viewBillModal').style.display = 'block';
                })
                .catch(error => {
                    console.error('Error fetching bill data:', error);
                    alert('Error loading bill data. Please try again.');
                });
        }

        function displayBillDetails(bill) {
            let detailsHtml = '<div class="bill-details">';
            
            detailsHtml += '<div class="detail-row">';
            detailsHtml += '<div class="detail-label">Bill ID:</div>';
            detailsHtml += '<div class="detail-value"><strong>' + bill.billId + '</strong></div>';
            detailsHtml += '</div>';
            
            detailsHtml += '<div class="detail-row">';
            detailsHtml += '<div class="detail-label">Customer:</div>';
            detailsHtml += '<div class="detail-value">' + bill.customerName + ' (' + bill.accountNumber + ')</div>';
            detailsHtml += '</div>';
            
            detailsHtml += '<div class="detail-row">';
            detailsHtml += '<div class="detail-label">Date:</div>';
            detailsHtml += '<div class="detail-value">' + bill.billDate + '</div>';
            detailsHtml += '</div>';
            
            detailsHtml += '<div class="detail-row">';
            detailsHtml += '<div class="detail-label">Status:</div>';
            detailsHtml += '<div class="detail-value">';
            if (bill.status === 'PENDING') {
                detailsHtml += '<span class="badge badge-pending">Pending</span>';
            } else if (bill.status === 'PAID') {
                detailsHtml += '<span class="badge badge-paid">Paid</span>';
            } else {
                detailsHtml += '<span class="badge badge-cancelled">Cancelled</span>';
            }
            detailsHtml += '</div>';
            detailsHtml += '</div>';
            
            detailsHtml += '<div class="detail-row">';
            detailsHtml += '<div class="detail-label">Total Amount:</div>';
            detailsHtml += '<div class="detail-value price-display">Rs. ' + parseFloat(bill.totalAmount).toFixed(2) + '</div>';
            detailsHtml += '</div>';
            
            detailsHtml += '</div>';
            
            // Add bill items
            if (bill.billItems && bill.billItems.length > 0) {
                detailsHtml += '<h5><i class="fas fa-list"></i> Items in Bill</h5>';
                detailsHtml += '<table class="bill-items-table">';
                detailsHtml += '<thead>';
                detailsHtml += '<tr><th>Item</th><th>Quantity</th><th>Unit Price</th><th>Total</th></tr>';
                detailsHtml += '</thead>';
                detailsHtml += '<tbody>';
                
                for (let item of bill.billItems) {
                    detailsHtml += '<tr>';
                    detailsHtml += '<td>' + item.itemName + '</td>';
                    detailsHtml += '<td>' + item.quantity + '</td>';
                    detailsHtml += '<td>Rs. ' + parseFloat(item.unitPrice).toFixed(2) + '</td>';
                    detailsHtml += '<td class="price-display">Rs. ' + parseFloat(item.totalPrice).toFixed(2) + '</td>';
                    detailsHtml += '</tr>';
                }
                
                detailsHtml += '</tbody>';
                detailsHtml += '</table>';
            } else {
                detailsHtml += '<p class="text-muted"><i class="fas fa-info-circle"></i> No items in this bill yet.</p>';
            }
            
            document.getElementById('billDetails').innerHTML = detailsHtml;
        }

        function manageBillItems(billId) {
            currentBillId = billId;
            
            fetch('${pageContext.request.contextPath}/billing/view?billId=' + billId)
                .then(response => response.json())
                .then(data => {
                    if (data.error) {
                        throw new Error(data.error);
                    }
                    
                    displayBillForManagement(data);
                    document.getElementById('manageBillItemsModal').style.display = 'block';
                })
                .catch(error => {
                    console.error('Error fetching bill data:', error);
                    alert('Error loading bill data. Please try again.');
                });
        }

        function displayBillForManagement(bill) {
            // Display current bill info
            let billInfoHtml = '<div class="bill-details">';
            billInfoHtml += '<h5>Bill #' + bill.billId + ' - ' + bill.customerName + '</h5>';
            billInfoHtml += '<p class="price-display">Current Total: Rs. ' + parseFloat(bill.totalAmount).toFixed(2) + '</p>';
            billInfoHtml += '</div>';
            
            document.getElementById('currentBillInfo').innerHTML = billInfoHtml;
            
            // Display current bill items
            let itemsHtml = '<h5><i class="fas fa-list"></i> Current Items</h5>';
            
            if (bill.billItems && bill.billItems.length > 0) {
                itemsHtml += '<table class="bill-items-table">';
                itemsHtml += '<thead>';
                itemsHtml += '<tr><th>Item</th><th>Quantity</th><th>Unit Price</th><th>Total</th><th>Actions</th></tr>';
                itemsHtml += '</thead>';
                itemsHtml += '<tbody>';
                
                for (let item of bill.billItems) {
                    itemsHtml += '<tr>';
                    itemsHtml += '<td>' + item.itemName + '</td>';
                    itemsHtml += '<td>';
                    itemsHtml += '<input type="number" class="quantity-input" value="' + item.quantity + '" ';
                    itemsHtml += 'onchange="updateItemQuantity(' + item.itemId + ', this.value)">';
                    itemsHtml += '</td>';
                    itemsHtml += '<td>Rs. ' + parseFloat(item.unitPrice).toFixed(2) + '</td>';
                    itemsHtml += '<td class="price-display">Rs. ' + parseFloat(item.totalPrice).toFixed(2) + '</td>';
                    itemsHtml += '<td>';
                    itemsHtml += '<button type="button" class="btn btn-danger btn-sm" ';
                    itemsHtml += 'onclick="removeItemFromBill(' + item.itemId + ')">';
                    itemsHtml += '<i class="fas fa-trash"></i></button>';
                    itemsHtml += '</td>';
                    itemsHtml += '</tr>';
                }
                
                itemsHtml += '</tbody>';
                itemsHtml += '</table>';
            } else {
                itemsHtml += '<p class="text-muted">No items in this bill yet.</p>';
            }
            
            document.getElementById('billItemsList').innerHTML = itemsHtml;
        }

        function addItemToBill() {
            let itemSelect = document.getElementById('itemSelect');
            let quantityInput = document.getElementById('quantityInput');
            
            let itemId = itemSelect.value;
            let quantity = parseInt(quantityInput.value);
            
            if (!itemId) {
                alert('Please select an item');
                return;
            }
            
            if (quantity <= 0) {
                alert('Please enter a valid quantity');
                return;
            }
            
            // Get selected option to check stock
            let selectedOption = itemSelect.options[itemSelect.selectedIndex];
            let availableStock = parseInt(selectedOption.getAttribute('data-stock'));
            
            if (quantity > availableStock) {
                alert('Insufficient stock. Available: ' + availableStock);
                return;
            }
            
            fetch('${pageContext.request.contextPath}/billing/add-item', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'billId=' + currentBillId + '&itemId=' + itemId + '&quantity=' + quantity
            })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    alert('Error: ' + data.error);
                } else {
                    alert('Item added successfully!');
                    refreshBillDisplay();
                    quantityInput.value = 1;
                    itemSelect.value = '';
                }
            })
            .catch(error => {
                console.error('Error adding item:', error);
                alert('Error adding item to bill');
            });
        }

        function removeItemFromBill(itemId) {
            if (!confirm('Are you sure you want to remove this item from the bill?')) {
                return;
            }
            
            fetch('${pageContext.request.contextPath}/billing/remove-item', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'billId=' + currentBillId + '&itemId=' + itemId
            })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    alert('Error: ' + data.error);
                } else {
                    alert('Item removed successfully!');
                    refreshBillDisplay();
                }
            })
            .catch(error => {
                console.error('Error removing item:', error);
                alert('Error removing item from bill');
            });
        }

        function updateItemQuantity(itemId, newQuantity) {
            if (newQuantity <= 0) {
                alert('Quantity must be greater than 0');
                refreshBillDisplay(); // Reset the input
                return;
            }
            
            fetch('${pageContext.request.contextPath}/billing/update-quantity', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'billId=' + currentBillId + '&itemId=' + itemId + '&quantity=' + newQuantity
            })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    alert('Error: ' + data.error);
                    refreshBillDisplay(); // Reset the input
                } else {
                    refreshBillDisplay();
                }
            })
            .catch(error => {
                console.error('Error updating quantity:', error);
                alert('Error updating quantity');
                refreshBillDisplay(); // Reset the input
            });
        }

        function refreshBillDisplay() {
            if (currentBillId) {
                fetch('${pageContext.request.contextPath}/billing/view?billId=' + currentBillId)
                    .then(response => response.json())
                    .then(data => {
                        if (!data.error) {
                            displayBillForManagement(data);
                        }
                    })
                    .catch(error => {
                        console.error('Error refreshing bill data:', error);
                    });
            }
        }

        function completeBill(billId) {
            // Prevent multiple submissions
            if (window.submittingBill === billId) {
                return;
            }

            if (confirm('Are you sure you want to mark this bill as paid? This action cannot be undone.')) {
                window.submittingBill = billId;

                // Disable the approve button to prevent multiple clicks
                const approveButton = document.querySelector(`button[onclick="completeBill('${billId}')"]`);
                if (approveButton) {
                    approveButton.disabled = true;
                    approveButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
                }

                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/billing/complete';
                
                const billIdInput = document.createElement('input');
                billIdInput.type = 'hidden';
                billIdInput.name = 'billId';
                billIdInput.value = billId;
                
                form.appendChild(billIdInput);
                document.body.appendChild(form);
                form.submit();
            }
        }

        function cancelBill(billId) {
            // Prevent multiple submissions
            if (window.submittingBill === billId) {
                return;
            }

            if (confirm('Are you sure you want to cancel this bill? Stock will be restored.')) {
                window.submittingBill = billId;

                // Disable the cancel button to prevent multiple clicks
                const cancelButton = document.querySelector(`button[onclick="cancelBill('${billId}')"]`);
                if (cancelButton) {
                    cancelButton.disabled = true;
                    cancelButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
                }

                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/billing/cancel';
                
                const billIdInput = document.createElement('input');
                billIdInput.type = 'hidden';
                billIdInput.name = 'billId';
                billIdInput.value = billId;
                
                form.appendChild(billIdInput);
                document.body.appendChild(form);
                form.submit();
            }
        }

        function deleteBill(billId) {
            // Prevent multiple submissions
            if (window.submittingBill === billId) {
                return;
            }

            if (confirm('Are you sure you want to delete this bill? This action cannot be undone.')) {
                window.submittingBill = billId;

                // Disable the delete button to prevent multiple clicks
                const deleteButton = document.querySelector(`button[onclick="deleteBill('${billId}')"]`);
                if (deleteButton) {
                    deleteButton.disabled = true;
                    deleteButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
                }

                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/billing/delete';
                
                const billIdInput = document.createElement('input');
                billIdInput.type = 'hidden';
                billIdInput.name = 'billId';
                billIdInput.value = billId;
                
                form.appendChild(billIdInput);
                document.body.appendChild(form);
                form.submit();
            }
        }

        function closeViewModal() {
            document.getElementById('viewBillModal').style.display = 'none';
        }

        function closeManageModal() {
            document.getElementById('manageBillItemsModal').style.display = 'none';
        }

        // Close modals when clicking outside
        window.onclick = function(event) {
            const createModal = document.getElementById('createBillModal');
            const viewModal = document.getElementById('viewBillModal');
            const manageModal = document.getElementById('manageBillItemsModal');
            
            if (event.target == createModal) {
                closeCreateModal();
            }
            if (event.target == viewModal) {
                closeViewModal();
            }
            if (event.target == manageModal) {
                closeManageModal();
            }
        }
        
        // Modal Create Bill Functions
        let modalItemRowCount = 0;
        const modalAvailableItems = [
            <c:forEach var="item" items="${availableItems}" varStatus="status">
                {
                    id: ${item.itemId},
                    name: "${item.itemName}",
                    price: ${item.unitPrice},
                    stock: ${item.stockQuantity}
                }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        function showCreateBillModal() {
            document.getElementById('createBillModal').style.display = 'block';
            resetModalForm();
        }

        function closeCreateModal() {
            document.getElementById('createBillModal').style.display = 'none';
            resetModalForm();
        }
        
        function resetModalForm() {
            document.getElementById('createBillForm').reset();
            document.getElementById('modalCustomerSelect').value = '';
            
            // Reset items to just one empty row
            const container = document.getElementById('modalItemsContainer');
            const firstRow = container.querySelector('.modal-item-row');
            if (firstRow) {
                firstRow.querySelector('.modal-item-dropdown').value = '';
                firstRow.querySelector('input[type="number"]').value = 1;
                firstRow.querySelector('#modalUnitPrice0').value = '';
                firstRow.querySelector('#modalItemTotal0').value = '';
                firstRow.querySelector('#modalStockInfo0').textContent = '';
                firstRow.querySelector('#modalStockWarning0').textContent = '';
            }
            
            // Remove extra rows
            const rows = container.querySelectorAll('.modal-item-row');
            for (let i = 1; i < rows.length; i++) {
                rows[i].remove();
            }
            
            modalItemRowCount = 0;
            updateModalRemoveButtons();
            calculateModalGrandTotal();
        }

        function addModalItemRow() {
            modalItemRowCount++;
            const container = document.getElementById('modalItemsContainer');
            const newRow = document.createElement('div');
            newRow.className = 'modal-item-row';
            newRow.setAttribute('data-row', modalItemRowCount);
            
            let optionsHtml = '<option value="">-- Select Item --</option>';
            modalAvailableItems.forEach(item => {
                optionsHtml += '<option value="' + item.id + '" data-price="' + item.price + '" data-stock="' + item.stock + '">' +
                              item.name + ' - Rs. ' + item.price.toFixed(2) + '</option>';
            });
            
            // Build HTML with proper string concatenation for dynamic row indices
            const rowIndex = modalItemRowCount;
            newRow.innerHTML = 
                '<div style="background: white; padding: 15px; margin-bottom: 10px; border-radius: 6px; border: 1px solid #e9ecef;">' +
                    '<div style="display: flex; gap: 10px; align-items: end; margin-bottom: 10px;">' +
                        '<div style="flex: 2;">' +
                            '<label style="font-size: 12px; font-weight: 600;">Item</label>' +
                            '<select name="items[' + rowIndex + '].itemId" class="form-control modal-item-dropdown" onchange="updateModalItemPrice(this, ' + rowIndex + ')" required style="font-size: 14px;">' +
                                optionsHtml +
                            '</select>' +
                        '</div>' +
                        '<div style="flex: 1;">' +
                            '<label style="font-size: 12px; font-weight: 600;">Quantity</label>' +
                            '<input type="number" name="items[' + rowIndex + '].quantity" class="form-control" min="1" value="1" onchange="calculateModalRowTotal(' + rowIndex + ')" required style="font-size: 14px;">' +
                        '</div>' +
                        '<div style="flex: 1;">' +
                            '<label style="font-size: 12px; font-weight: 600;">Unit Price</label>' +
                            '<input type="text" class="form-control" id="modalUnitPrice' + rowIndex + '" readonly style="background: #f8f9fa; font-size: 14px; font-weight: bold;">' +
                        '</div>' +
                        '<div style="flex: 1;">' +
                            '<label style="font-size: 12px; font-weight: 600;">Total</label>' +
                            '<input type="text" class="form-control modal-item-total" id="modalItemTotal' + rowIndex + '" readonly style="background: #f8f9fa; font-size: 14px; font-weight: bold; color: #28a745;">' +
                        '</div>' +
                        '<div>' +
                            '<label style="font-size: 12px; font-weight: 600;">&nbsp;</label>' +
                            '<button type="button" class="btn btn-danger btn-sm modal-remove-btn" onclick="removeModalItemRow(this)">' +
                                '<i class="fas fa-trash"></i>' +
                            '</button>' +
                        '</div>' +
                    '</div>' +
                    '<div class="modal-stock-info" id="modalStockInfo' + rowIndex + '" style="font-size: 12px; color: #6c757d;"></div>' +
                    '<div class="modal-stock-warning" id="modalStockWarning' + rowIndex + '" style="font-size: 12px; color: #dc3545;"></div>' +
                '</div>';
            
            container.appendChild(newRow);
            updateModalRemoveButtons();
        }

        function removeModalItemRow(button) {
            const row = button.closest('.modal-item-row');
            row.remove();
            updateModalRemoveButtons();
            calculateModalGrandTotal();
        }

        function updateModalRemoveButtons() {
            const rows = document.querySelectorAll('.modal-item-row');
            rows.forEach((row, index) => {
                const removeBtn = row.querySelector('.modal-remove-btn');
                if (rows.length > 1) {
                    removeBtn.style.display = 'block';
                } else {
                    removeBtn.style.display = 'none';
                }
            });
        }

        function updateModalItemPrice(selectElement, rowIndex) {
            const selectedOption = selectElement.selectedOptions[0];
            const unitPriceField = document.getElementById('modalUnitPrice' + rowIndex);
            const stockInfo = document.getElementById('modalStockInfo' + rowIndex);
            
            if (!unitPriceField || !stockInfo) {
                console.error('Missing elements for row', rowIndex);
                return;
            }
            
            if (selectedOption.value) {
                const price = parseFloat(selectedOption.dataset.price);
                const stock = parseInt(selectedOption.dataset.stock);
                
                unitPriceField.value = 'Rs. ' + price.toFixed(2);
                stockInfo.textContent = 'Available stock: ' + stock + ' units';
                
                calculateModalRowTotal(rowIndex);
            } else {
                unitPriceField.value = '';
                stockInfo.textContent = '';
                const totalField = document.getElementById('modalItemTotal' + rowIndex);
                if (totalField) totalField.value = '';
                calculateModalGrandTotal();
            }
        }

        function calculateModalRowTotal(rowIndex) {
            const selectElement = document.querySelector('[name="items[' + rowIndex + '].itemId"]');
            const quantityInput = document.querySelector('[name="items[' + rowIndex + '].quantity"]');
            const itemTotalField = document.getElementById('modalItemTotal' + rowIndex);
            const stockWarning = document.getElementById('modalStockWarning' + rowIndex);
            
            if (!selectElement || !quantityInput || !itemTotalField || !stockWarning) {
                console.error('Missing elements for row', rowIndex);
                return;
            }
            
            const selectedOption = selectElement.selectedOptions[0];
            const quantity = parseInt(quantityInput.value) || 0;
            
            if (selectedOption.value && quantity > 0) {
                const price = parseFloat(selectedOption.dataset.price);
                const stock = parseInt(selectedOption.dataset.stock);
                const total = price * quantity;
                
                itemTotalField.value = 'Rs. ' + total.toFixed(2);
                
                // Check stock availability
                if (quantity > stock) {
                    stockWarning.textContent = 'Warning: Requested quantity (' + quantity + ') exceeds available stock (' + stock + ')';
                    quantityInput.style.borderColor = '#dc3545';
                } else {
                    stockWarning.textContent = '';
                    quantityInput.style.borderColor = '#ced4da';
                }
            } else {
                itemTotalField.value = '';
                stockWarning.textContent = '';
            }
            
            calculateModalGrandTotal();
        }

        function calculateModalGrandTotal() {
            let grandTotal = 0;
            const itemTotalFields = document.querySelectorAll('.modal-item-total');
            
            itemTotalFields.forEach(field => {
                const value = field.value.replace('Rs. ', '').replace(',', '');
                if (value) {
                    grandTotal += parseFloat(value);
                }
            });
            
            document.getElementById('modalGrandTotal').textContent = grandTotal.toFixed(2);
            
            // Enable/disable submit button based on total
            const submitBtn = document.getElementById('modalSubmitBtn');
            const customerSelect = document.getElementById('modalCustomerSelect');
            
            if (grandTotal > 0 && customerSelect.value) {
                submitBtn.disabled = false;
            } else {
                submitBtn.disabled = true;
            }
        }

        // Add event listener for customer selection change
        document.getElementById('modalCustomerSelect').addEventListener('change', calculateModalGrandTotal);

        // Form validation before submit
        document.getElementById('createBillForm').addEventListener('submit', function(e) {
            const customerSelect = document.getElementById('modalCustomerSelect');
            const itemSelects = document.querySelectorAll('.modal-item-dropdown');
            let hasValidItems = false;
            let hasStockIssues = false;
            
            if (!customerSelect.value) {
                alert('Please select a customer');
                e.preventDefault();
                return;
            }
            
            itemSelects.forEach((select) => {
                if (select.value) {
                    hasValidItems = true;
                    // Get the row index from the select element's name attribute
                    const nameAttr = select.getAttribute('name');
                    const rowIndex = nameAttr.match(/\[(\d+)\]/)[1];
                    const quantityInput = document.querySelector('[name="items[' + rowIndex + '].quantity"]');
                    const selectedOption = select.selectedOptions[0];
                    const quantity = parseInt(quantityInput.value) || 0;
                    const stock = parseInt(selectedOption.dataset.stock);
                    
                    if (quantity > stock) {
                        hasStockIssues = true;
                    }
                }
            });
            
            if (!hasValidItems) {
                alert('Please select at least one item');
                e.preventDefault();
                return;
            }
            
            if (hasStockIssues) {
                if (!confirm('Some items have quantity exceeding available stock. Do you want to continue anyway?')) {
                    e.preventDefault();
                    return;
                }
            }
        });

        // Initialize modal functions
        updateModalRemoveButtons();
        calculateModalGrandTotal();
        
        // Auto-show modal if redirected from create URL
        <c:if test="${not empty sessionScope.showCreateModal}">
            showCreateBillModal();
        </c:if>
        <c:remove var="showCreateModal" scope="session" />
    </script>
</body>
</html>

