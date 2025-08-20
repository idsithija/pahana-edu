<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Bill - Pahana Edu Bookshop</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .bill-form {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            padding: 30px;
            margin-bottom: 30px;
        }
        .form-section {
            margin-bottom: 30px;
        }
        .form-section h3 {
            color: #2c3e50;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #3498db;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
            color: #2c3e50;
        }
        .form-control {
            width: 100%;
            padding: 12px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        .form-control:focus {
            outline: none;
            border-color: #3498db;
        }
        .items-section {
            border: 2px solid #e9ecef;
            border-radius: 8px;
            padding: 20px;
            background: #f8f9fa;
        }
        .item-row {
            display: flex;
            gap: 15px;
            margin-bottom: 15px;
            align-items: end;
            padding: 15px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .item-select {
            flex: 2;
        }
        .item-quantity {
            flex: 1;
        }
        .item-price {
            flex: 1;
            background: #f8f9fa;
            border: none;
            font-weight: bold;
        }
        .item-total {
            flex: 1;
            background: #f8f9fa;
            border: none;
            font-weight: bold;
            color: #27ae60;
        }
        .btn-remove {
            background: #e74c3c;
            color: white;
            border: none;
            padding: 12px 15px;
            border-radius: 6px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .btn-remove:hover {
            background: #c0392b;
        }
        .btn-add-item {
            background: #27ae60;
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 6px;
            cursor: pointer;
            margin-bottom: 20px;
        }
        .btn-add-item:hover {
            background: #229954;
        }
        .total-section {
            border-top: 2px solid #3498db;
            padding-top: 20px;
            margin-top: 20px;
        }
        .total-amount {
            font-size: 24px;
            font-weight: bold;
            color: #27ae60;
            text-align: right;
        }
        .btn-create {
            background: #3498db;
            color: white;
            border: none;
            padding: 15px 30px;
            font-size: 16px;
            border-radius: 8px;
            cursor: pointer;
            width: 100%;
            margin-top: 20px;
        }
        .btn-create:hover {
            background: #2980b9;
        }
        .btn-create:disabled {
            background: #bdc3c7;
            cursor: not-allowed;
        }
        .stock-warning {
            color: #e74c3c;
            font-size: 12px;
            margin-top: 5px;
        }
        .stock-info {
            color: #7f8c8d;
            font-size: 12px;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <!-- Include Navigation -->
    <%@ include file="../../includes/navigation.jsp" %>

    <div class="main-content">
        <div class="container-fluid">
            <div class="page-header">
                <h1><i class="fas fa-plus-circle"></i> Create New Bill</h1>
                <p class="page-description">Create a new bill by selecting a customer and adding items</p>
            </div>

            <!-- Display Messages -->
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                </div>
                <c:remove var="successMessage" scope="session" />
            </c:if>
            
            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i> ${sessionScope.errorMessage}
                </div>
                <c:remove var="errorMessage" scope="session" />
            </c:if>

            <form id="createBillForm" method="post" action="${pageContext.request.contextPath}/billing/create" class="bill-form">
                <!-- Customer Selection Section -->
                <div class="form-section">
                    <h3><i class="fas fa-user"></i> Customer Information</h3>
                    <div class="form-group">
                        <label for="customerSelect">Select Customer *</label>
                        <select id="customerSelect" name="accountNumber" class="form-control" required>
                            <option value="">-- Select a Customer --</option>
                            <c:forEach var="customer" items="${customers}">
                                <option value="${customer.accountNumber}">
                                    ${customer.name} (${customer.accountNumber})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <!-- Items Selection Section -->
                <div class="form-section">
                    <h3><i class="fas fa-shopping-cart"></i> Items</h3>
                    <div class="items-section">
                        <button type="button" class="btn-add-item" onclick="addItemRow()">
                            <i class="fas fa-plus"></i> Add Item
                        </button>
                        
                        <div id="itemsContainer">
                            <!-- Initial item row -->
                            <div class="item-row" data-row="0">
                                <div class="item-select">
                                    <label>Item *</label>
                                    <select name="items[0].itemId" class="form-control item-dropdown" onchange="updateItemPrice(this, 0)" required>
                                        <option value="">-- Select an Item --</option>
                                        <c:forEach var="item" items="${availableItems}">
                                            <option value="${item.itemId}" 
                                                    data-price="${item.unitPrice}" 
                                                    data-stock="${item.stockQuantity}">
                                                ${item.itemName} - Rs. <fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <div class="stock-info" id="stockInfo0"></div>
                                </div>
                                <div class="item-quantity">
                                    <label>Quantity *</label>
                                    <input type="number" name="items[0].quantity" class="form-control" 
                                           min="1" value="1" onchange="calculateRowTotal(0)" required>
                                    <div class="stock-warning" id="stockWarning0"></div>
                                </div>
                                <div class="item-price">
                                    <label>Unit Price</label>
                                    <input type="text" class="form-control item-price" id="unitPrice0" readonly>
                                </div>
                                <div class="item-total">
                                    <label>Total</label>
                                    <input type="text" class="form-control item-total" id="itemTotal0" readonly>
                                </div>
                                <div>
                                    <label>&nbsp;</label>
                                    <button type="button" class="btn-remove" onclick="removeItemRow(this)" style="display:none;">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Total Section -->
                        <div class="total-section">
                            <div class="total-amount">
                                Grand Total: Rs. <span id="grandTotal">0.00</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Submit Button -->
                <button type="submit" class="btn-create" id="submitBtn">
                    <i class="fas fa-check"></i> Create Bill
                </button>
            </form>
        </div>
    </div>

    <script>
        let itemRowCount = 0;
        const availableItems = [
            <c:forEach var="item" items="${availableItems}" varStatus="status">
                {
                    id: ${item.itemId},
                    name: "${item.itemName}",
                    price: ${item.unitPrice},
                    stock: ${item.stockQuantity}
                }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        function addItemRow() {
            itemRowCount++;
            const container = document.getElementById('itemsContainer');
            const newRow = document.createElement('div');
            newRow.className = 'item-row';
            newRow.setAttribute('data-row', itemRowCount);
            
            let optionsHtml = '<option value="">-- Select an Item --</option>';
            availableItems.forEach(item => {
                optionsHtml += '<option value="' + item.id + '" data-price="' + item.price + '" data-stock="' + item.stock + '">' +
                              item.name + ' - Rs. ' + item.price.toFixed(2) + '</option>';
            });
            
            newRow.innerHTML = `
                <div class="item-select">
                    <label>Item *</label>
                    <select name="items[${itemRowCount}].itemId" class="form-control item-dropdown" onchange="updateItemPrice(this, ${itemRowCount})" required>
                        ${optionsHtml}
                    </select>
                    <div class="stock-info" id="stockInfo${itemRowCount}"></div>
                </div>
                <div class="item-quantity">
                    <label>Quantity *</label>
                    <input type="number" name="items[${itemRowCount}].quantity" class="form-control" 
                           min="1" value="1" onchange="calculateRowTotal(${itemRowCount})" required>
                    <div class="stock-warning" id="stockWarning${itemRowCount}"></div>
                </div>
                <div class="item-price">
                    <label>Unit Price</label>
                    <input type="text" class="form-control item-price" id="unitPrice${itemRowCount}" readonly>
                </div>
                <div class="item-total">
                    <label>Total</label>
                    <input type="text" class="form-control item-total" id="itemTotal${itemRowCount}" readonly>
                </div>
                <div>
                    <label>&nbsp;</label>
                    <button type="button" class="btn-remove" onclick="removeItemRow(this)">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            `;
            
            container.appendChild(newRow);
            updateRemoveButtons();
        }

        function removeItemRow(button) {
            const row = button.closest('.item-row');
            row.remove();
            updateRemoveButtons();
            calculateGrandTotal();
        }

        function updateRemoveButtons() {
            const rows = document.querySelectorAll('.item-row');
            rows.forEach((row, index) => {
                const removeBtn = row.querySelector('.btn-remove');
                if (rows.length > 1) {
                    removeBtn.style.display = 'block';
                } else {
                    removeBtn.style.display = 'none';
                }
            });
        }

        function updateItemPrice(selectElement, rowIndex) {
            const selectedOption = selectElement.selectedOptions[0];
            const unitPriceField = document.getElementById('unitPrice' + rowIndex);
            const stockInfo = document.getElementById('stockInfo' + rowIndex);
            
            if (selectedOption.value) {
                const price = parseFloat(selectedOption.dataset.price);
                const stock = parseInt(selectedOption.dataset.stock);
                
                unitPriceField.value = 'Rs. ' + price.toFixed(2);
                stockInfo.textContent = 'Available stock: ' + stock + ' units';
                stockInfo.className = 'stock-info';
                
                calculateRowTotal(rowIndex);
            } else {
                unitPriceField.value = '';
                stockInfo.textContent = '';
                document.getElementById('itemTotal' + rowIndex).value = '';
                calculateGrandTotal();
            }
        }

        function calculateRowTotal(rowIndex) {
            const selectElement = document.querySelector('[name="items[' + rowIndex + '].itemId"]');
            const quantityInput = document.querySelector('[name="items[' + rowIndex + '].quantity"]');
            const itemTotalField = document.getElementById('itemTotal' + rowIndex);
            const stockWarning = document.getElementById('stockWarning' + rowIndex);
            
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
                    stockWarning.className = 'stock-warning';
                    quantityInput.style.borderColor = '#e74c3c';
                } else {
                    stockWarning.textContent = '';
                    quantityInput.style.borderColor = '#e0e0e0';
                }
            } else {
                itemTotalField.value = '';
                stockWarning.textContent = '';
            }
            
            calculateGrandTotal();
        }

        function calculateGrandTotal() {
            let grandTotal = 0;
            const itemTotalFields = document.querySelectorAll('.item-total');
            
            itemTotalFields.forEach(field => {
                const value = field.value.replace('Rs. ', '').replace(',', '');
                if (value) {
                    grandTotal += parseFloat(value);
                }
            });
            
            document.getElementById('grandTotal').textContent = grandTotal.toFixed(2);
            
            // Enable/disable submit button based on total
            const submitBtn = document.getElementById('submitBtn');
            if (grandTotal > 0) {
                submitBtn.disabled = false;
            } else {
                submitBtn.disabled = true;
            }
        }

        // Form validation before submit
        document.getElementById('createBillForm').addEventListener('submit', function(e) {
            const customerSelect = document.getElementById('customerSelect');
            const itemSelects = document.querySelectorAll('.item-dropdown');
            let hasValidItems = false;
            let hasStockIssues = false;
            
            // Check if customer is selected
            if (!customerSelect.value) {
                alert('Please select a customer');
                e.preventDefault();
                return;
            }
            
            // Check if at least one item is selected with valid quantity
            itemSelects.forEach((select, index) => {
                if (select.value) {
                    hasValidItems = true;
                    const quantityInput = document.querySelector('[name="items[' + index + '].quantity"]');
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

        // Initialize
        updateRemoveButtons();
        calculateGrandTotal();
    </script>
</body>
</html>