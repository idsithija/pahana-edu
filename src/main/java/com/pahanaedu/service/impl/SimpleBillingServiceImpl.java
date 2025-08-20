package com.pahanaedu.service.impl;

import com.pahanaedu.dao.BillDAO;
import com.pahanaedu.dao.BillItemDAO;
import com.pahanaedu.dao.CustomerDAO;
import com.pahanaedu.dao.ItemDAO;
import com.pahanaedu.dao.impl.BillDAOImpl;
import com.pahanaedu.dao.impl.BillItemDAOImpl;
import com.pahanaedu.dao.impl.CustomerDAOImpl;
import com.pahanaedu.dao.impl.ItemDAOImpl;
import com.pahanaedu.model.Bill;
import com.pahanaedu.model.BillItem;
import com.pahanaedu.model.Customer;
import com.pahanaedu.model.Item;
import com.pahanaedu.service.BillingService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Simple implementation of BillingService for development environments
 * Uses direct DAO instantiation instead of EJB dependency injection
 */
public class SimpleBillingServiceImpl implements BillingService {
    
    private static final Logger logger = Logger.getLogger(SimpleBillingServiceImpl.class.getName());
    
    private final BillDAO billDAO;
    private final BillItemDAO billItemDAO;
    private final CustomerDAO customerDAO;
    private final ItemDAO itemDAO;
    
    public SimpleBillingServiceImpl() {
        this.billDAO = new BillDAOImpl();
        this.billItemDAO = new BillItemDAOImpl();
        this.customerDAO = new CustomerDAOImpl();
        this.itemDAO = new ItemDAOImpl();
    }
    
    @Override
    public Bill createBill(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        
        Bill bill = new Bill(customer);
        Bill savedBill = billDAO.save(bill);
        logger.info("Bill created successfully: ID " + savedBill.getBillId() + " for customer " + customer.getAccountNumber());
        return savedBill;
    }
    
    @Override
    public Bill createBill(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        
        Optional<Customer> customerOpt = customerDAO.findByAccountNumber(accountNumber.trim());
        if (!customerOpt.isPresent()) {
            throw new IllegalArgumentException("Customer not found: " + accountNumber);
        }
        
        return createBill(customerOpt.get());
    }
    
    @Override
    public Bill addItemToBill(Long billId, Long itemId, int quantity) {
        if (billId == null || itemId == null) {
            throw new IllegalArgumentException("Bill ID and Item ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        Optional<Bill> billOpt = billDAO.findByBillId(billId);
        if (!billOpt.isPresent()) {
            throw new IllegalArgumentException("Bill not found: " + billId);
        }
        
        Optional<Item> itemOpt = itemDAO.findByItemId(itemId);
        if (!itemOpt.isPresent()) {
            throw new IllegalArgumentException("Item not found: " + itemId);
        }
        
        Bill bill = billOpt.get();
        Item item = itemOpt.get();
        
        if (!bill.isPending()) {
            throw new IllegalStateException("Cannot modify non-pending bill");
        }
        
        // Check stock availability
        if (!item.hasStock(quantity)) {
            throw new IllegalArgumentException("Insufficient stock for item: " + item.getItemName() + ". Available: " + item.getStockQuantity());
        }
        
        // First check if item already exists in the bill using database query
        Optional<BillItem> existingBillItemOpt = billItemDAO.findByBillAndItem(bill, item);

        if (existingBillItemOpt.isPresent()) {
            // Item already exists - update quantity instead of adding new
            BillItem existingBillItem = existingBillItemOpt.get();
            int newQuantity = existingBillItem.getQuantity() + quantity;

            // Check total stock availability for the new quantity
            int additionalStock = quantity; // Only need additional quantity since we already have some
            if (!item.hasStock(additionalStock)) {
                throw new IllegalArgumentException("Insufficient stock for item: " + item.getItemName() + ". Available: " + item.getStockQuantity());
            }

            // Update the existing bill item
            existingBillItem.setQuantity(newQuantity);
            existingBillItem.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
            billItemDAO.update(existingBillItem);

            // Update stock for the additional quantity only
            itemDAO.decreaseStock(itemId, quantity);

            logger.info("Updated existing item in bill " + billId + ": " + item.getItemName() + " (new qty: " + newQuantity + ")");
        } else {
            // Item doesn't exist - create new bill item
            BillItem billItem = new BillItem(bill, item, quantity, item.getUnitPrice());
            bill.addBillItem(billItem);

            // Update stock
            itemDAO.decreaseStock(itemId, quantity);

            logger.info("Added new item to bill " + billId + ": " + item.getItemName() + " (qty: " + quantity + ")");
        }

        // Recalculate total and save
        bill.recalculateTotal();
        Bill updatedBill = billDAO.update(bill);

        // Return fresh data from database to ensure UI sync
        return billDAO.findByBillId(billId).orElse(updatedBill);
    }
    
    @Override
    public Bill removeItemFromBill(Long billId, Long itemId) {
        if (billId == null || itemId == null) {
            throw new IllegalArgumentException("Bill ID and Item ID cannot be null");
        }
        
        Optional<Bill> billOpt = billDAO.findByBillId(billId);
        if (!billOpt.isPresent()) {
            throw new IllegalArgumentException("Bill not found: " + billId);
        }
        
        Optional<Item> itemOpt = itemDAO.findByItemId(itemId);
        if (!itemOpt.isPresent()) {
            throw new IllegalArgumentException("Item not found: " + itemId);
        }
        
        Bill bill = billOpt.get();
        Item item = itemOpt.get();
        
        if (!bill.isPending()) {
            throw new IllegalStateException("Cannot modify non-pending bill");
        }
        
        // Find bill item
        Optional<BillItem> billItemOpt = billItemDAO.findByBillAndItem(bill, item);
        if (!billItemOpt.isPresent()) {
            throw new IllegalArgumentException("Item not found in bill");
        }
        
        BillItem billItem = billItemOpt.get();
        int quantity = billItem.getQuantity();
        
        // Remove item from bill
        bill.removeBillItem(billItem);
        
        // Restore stock
        itemDAO.increaseStock(itemId, quantity);
        
        Bill updatedBill = billDAO.update(bill);
        logger.info("Item removed from bill " + billId + ": " + item.getItemName() + " (qty: " + quantity + ")");

        // Return fresh data from database to ensure UI sync
        return billDAO.findByBillId(billId).orElse(updatedBill);
    }
    
    @Override
    public Bill updateItemQuantityInBill(Long billId, Long itemId, int newQuantity) {
        if (billId == null || itemId == null) {
            throw new IllegalArgumentException("Bill ID and Item ID cannot be null");
        }
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        Optional<Bill> billOpt = billDAO.findByBillId(billId);
        if (!billOpt.isPresent()) {
            throw new IllegalArgumentException("Bill not found: " + billId);
        }
        
        Optional<Item> itemOpt = itemDAO.findByItemId(itemId);
        if (!itemOpt.isPresent()) {
            throw new IllegalArgumentException("Item not found: " + itemId);
        }
        
        Bill bill = billOpt.get();
        Item item = itemOpt.get();
        
        if (!bill.isPending()) {
            throw new IllegalStateException("Cannot modify non-pending bill");
        }
        
        // Find bill item
        Optional<BillItem> billItemOpt = billItemDAO.findByBillAndItem(bill, item);
        if (!billItemOpt.isPresent()) {
            throw new IllegalArgumentException("Item not found in bill");
        }
        
        BillItem billItem = billItemOpt.get();
        int oldQuantity = billItem.getQuantity();
        int quantityDifference = newQuantity - oldQuantity;
        
        // Check stock availability if increasing quantity
        if (quantityDifference > 0 && !item.hasStock(quantityDifference)) {
            throw new IllegalArgumentException("Insufficient stock for item: " + item.getItemName() + ". Available: " + item.getStockQuantity());
        }
        
        // Update bill item quantity
        billItem.updateQuantity(newQuantity);
        billItemDAO.update(billItem);
        
        // Update stock
        if (quantityDifference > 0) {
            itemDAO.decreaseStock(itemId, quantityDifference);
        } else if (quantityDifference < 0) {
            itemDAO.increaseStock(itemId, Math.abs(quantityDifference));
        }
        
        // Recalculate bill total
        bill.recalculateTotal();
        Bill updatedBill = billDAO.update(bill);
        
        logger.info("Item quantity updated in bill " + billId + ": " + item.getItemName() + " (old: " + oldQuantity + ", new: " + newQuantity + ")");
        return updatedBill;
    }
    
    @Override
    public Bill calculateBillTotal(Long billId) {
        if (billId == null) {
            throw new IllegalArgumentException("Bill ID cannot be null");
        }
        
        Optional<Bill> billOpt = billDAO.findByBillId(billId);
        if (!billOpt.isPresent()) {
            throw new IllegalArgumentException("Bill not found: " + billId);
        }
        
        Bill bill = billOpt.get();
        bill.recalculateTotal();
        
        return billDAO.update(bill);
    }
    
    @Override
    public Bill markBillAsPaid(Long billId) {
        if (billId == null) {
            throw new IllegalArgumentException("Bill ID cannot be null");
        }
        
        Optional<Bill> billOpt = billDAO.findByBillId(billId);
        if (!billOpt.isPresent()) {
            throw new IllegalArgumentException("Bill not found: " + billId);
        }
        
        Bill bill = billOpt.get();
        if (!bill.isPending()) {
            throw new IllegalStateException("Only pending bills can be marked as paid");
        }
        
        bill.markAsPaid();
        Bill updatedBill = billDAO.update(bill);
        logger.info("Bill marked as paid: ID " + billId);
        return updatedBill;
    }
    
    @Override
    public Bill cancelBill(Long billId) {
        if (billId == null) {
            throw new IllegalArgumentException("Bill ID cannot be null");
        }
        
        Optional<Bill> billOpt = billDAO.findByBillId(billId);
        if (!billOpt.isPresent()) {
            throw new IllegalArgumentException("Bill not found: " + billId);
        }
        
        Bill bill = billOpt.get();
        if (!bill.isPending()) {
            throw new IllegalStateException("Only pending bills can be cancelled");
        }
        
        // Restore stock for all items in the bill
        for (BillItem billItem : bill.getBillItems()) {
            itemDAO.increaseStock(billItem.getItem().getItemId(), billItem.getQuantity());
        }
        
        bill.markAsCancelled();
        Bill updatedBill = billDAO.update(bill);
        logger.info("Bill cancelled: ID " + billId);
        return updatedBill;
    }
    
    @Override
    public Optional<Bill> findById(Long billId) {
        if (billId == null) {
            return Optional.empty();
        }
        return billDAO.findByBillId(billId);
    }
    
    @Override
    public List<Bill> getBillsByCustomer(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        return billDAO.findByCustomerAccountNumber(accountNumber.trim());
    }
    
    @Override
    public List<Bill> getPendingBills() {
        return billDAO.findPendingBills();
    }
    
    @Override
    public List<Bill> getPaidBills() {
        return billDAO.findPaidBills();
    }
    
    @Override
    public List<Bill> getCancelledBills() {
        return billDAO.findCancelledBills();
    }
    
    @Override
    public List<Bill> getTodaysBills() {
        return billDAO.findTodaysBills();
    }
    
    @Override
    public List<Bill> getCurrentMonthBills() {
        return billDAO.findCurrentMonthBills();
    }
    
    @Override
    public List<Bill> getBillsInDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        return billDAO.findByBillDateBetween(startDate, endDate);
    }
    
    @Override
    public BigDecimal calculateRevenueForPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        return billDAO.calculateRevenueForPeriod(startDate, endDate);
    }
    
    @Override
    public BigDecimal calculateCustomerRevenue(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        return billDAO.calculateCustomerRevenue(accountNumber.trim());
    }
    
    @Override
    public long[] getBillStatistics() {
        return billDAO.getBillStatistics();
    }
    
    @Override
    public List<Object[]> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        return billDAO.getDailyRevenue(startDate, endDate);
    }
    
    @Override
    public List<Object[]> getMonthlyRevenue(int year) {
        if (year < 1900 || year > 2100) {
            throw new IllegalArgumentException("Invalid year: " + year);
        }
        return billDAO.getMonthlyRevenue(year);
    }
    
    @Override
    public List<Object[]> getTopCustomersByRevenue(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        return billDAO.getTopCustomersByRevenue(limit);
    }
    
    @Override
    public List<Bill> searchBills(String customerName, Bill.BillStatus status,
                                 LocalDate startDate, LocalDate endDate,
                                 BigDecimal minAmount, BigDecimal maxAmount) {
        return billDAO.searchBills(customerName, status, startDate, endDate, minAmount, maxAmount);
    }
    
    @Override
    public List<Bill> getAllBills() {
        return billDAO.findAll();
    }
    
    @Override
    public void deleteBill(Long billId) {
        if (billId == null) {
            throw new IllegalArgumentException("Bill ID cannot be null");
        }
        
        Optional<Bill> billOpt = billDAO.findByBillId(billId);
        if (billOpt.isPresent()) {
            Bill bill = billOpt.get();
            
            // Restore stock if bill is pending
            if (bill.isPending()) {
                for (BillItem billItem : bill.getBillItems()) {
                    itemDAO.increaseStock(billItem.getItem().getItemId(), billItem.getQuantity());
                }
            }
        }
        
        billDAO.deleteById(billId);
        logger.info("Bill deleted successfully: ID " + billId);
    }
    
    @Override
    public List<Bill> getBillsWithPagination(int offset, int limit) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        
        return billDAO.findWithPagination(offset, limit);
    }
    
    @Override
    public long getTotalBillCount() {
        return billDAO.count();
    }
}
