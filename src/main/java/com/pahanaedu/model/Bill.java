package com.pahanaedu.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Bill entity representing customer bills
 * Each bill belongs to a customer and contains multiple bill items
 */
@Entity
@Table(name = "bills")
public class Bill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private Long billId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_number", referencedColumnName = "account_number")
    private Customer customer;
    
    @Column(name = "bill_date")
    private LocalDate billDate;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private BillStatus status = BillStatus.PENDING;
    
    // One-to-many relationship with bill items
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<BillItem> billItems = new ArrayList<>();
    
    // Constructors
    public Bill() {
        this.billDate = LocalDate.now();
        this.totalAmount = BigDecimal.ZERO;
    }
    
    public Bill(Customer customer) {
        this();
        this.customer = customer;
    }
    
    public Bill(Customer customer, BigDecimal totalAmount) {
        this(customer);
        this.totalAmount = totalAmount;
    }
    
    // Getters and Setters
    public Long getBillId() {
        return billId;
    }
    
    public void setBillId(Long billId) {
        this.billId = billId;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public LocalDate getBillDate() {
        return billDate;
    }
    
    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BillStatus getStatus() {
        return status;
    }
    
    public void setStatus(BillStatus status) {
        this.status = status;
    }
    
    public List<BillItem> getBillItems() {
        return billItems;
    }
    
    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }
    
    // Business methods
    public void addBillItem(BillItem billItem) {
        billItems.add(billItem);
        billItem.setBill(this);
        recalculateTotal();
    }
    
    public void removeBillItem(BillItem billItem) {
        billItems.remove(billItem);
        billItem.setBill(null);
        recalculateTotal();
    }
    
    public void addItem(Item item, int quantity) {
        if (!item.hasStock(quantity)) {
            throw new IllegalArgumentException("Insufficient stock for item: " + item.getItemName());
        }
        
        // Check if item already exists in bill
        BillItem existingBillItem = findBillItemByItem(item);
        if (existingBillItem != null) {
            // Update existing bill item
            int newQuantity = existingBillItem.getQuantity() + quantity;
            existingBillItem.setQuantity(newQuantity);
            existingBillItem.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
        } else {
            // Create new bill item
            BillItem billItem = new BillItem(this, item, quantity, item.getUnitPrice());
            addBillItem(billItem);
        }
    }
    
    private BillItem findBillItemByItem(Item item) {
        return billItems.stream()
                .filter(billItem -> billItem.getItem().equals(item))
                .findFirst()
                .orElse(null);
    }
    
    public void recalculateTotal() {
        this.totalAmount = billItems.stream()
                .map(BillItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public boolean isPending() {
        return BillStatus.PENDING.equals(this.status);
    }
    
    public boolean isPaid() {
        return BillStatus.PAID.equals(this.status);
    }
    
    public boolean isCancelled() {
        return BillStatus.CANCELLED.equals(this.status);
    }
    
    public void markAsPaid() {
        this.status = BillStatus.PAID;
    }
    
    public void markAsCancelled() {
        this.status = BillStatus.CANCELLED;
    }
    
    public int getTotalItemCount() {
        return billItems.stream()
                .mapToInt(BillItem::getQuantity)
                .sum();
    }
    
    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return Objects.equals(billId, bill.billId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(billId);
    }
    
    // ToString
    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", customer=" + (customer != null ? customer.getAccountNumber() : null) +
                ", billDate=" + billDate +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", itemCount=" + billItems.size() +
                '}';
    }
    
    /**
     * Bill status enumeration
     */
    public enum BillStatus {
        PENDING, PAID, CANCELLED
    }
}

