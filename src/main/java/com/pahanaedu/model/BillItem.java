package com.pahanaedu.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * BillItem entity representing individual items within a bill
 * Junction entity between Bill and Item with quantity and pricing information
 */
@Entity
@Table(name = "bill_items")
public class BillItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_item_id")
    private Long billItemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    // Constructors
    public BillItem() {}
    
    public BillItem(Bill bill, Item item, Integer quantity, BigDecimal unitPrice) {
        this.bill = bill;
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Getters and Setters
    public Long getBillItemId() {
        return billItemId;
    }
    
    public void setBillItemId(Long billItemId) {
        this.billItemId = billItemId;
    }
    
    public Bill getBill() {
        return bill;
    }
    
    public void setBill(Bill bill) {
        this.bill = bill;
    }
    
    public Item getItem() {
        return item;
    }
    
    public void setItem(Item item) {
        this.item = item;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        // Recalculate total price when quantity changes
        if (this.unitPrice != null && quantity != null) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        // Recalculate total price when unit price changes
        if (this.quantity != null && unitPrice != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    // Business methods
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        // Check if item has sufficient stock
        if (item != null && !item.hasStock(newQuantity)) {
            throw new IllegalArgumentException("Insufficient stock for item: " + item.getItemName());
        }
        
        setQuantity(newQuantity);
    }
    
    public void updateUnitPrice(BigDecimal newUnitPrice) {
        if (newUnitPrice == null || newUnitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price must be non-negative");
        }
        
        setUnitPrice(newUnitPrice);
    }
    
    /**
     * Calculate total price based on current quantity and unit price
     */
    public void recalculateTotalPrice() {
        if (quantity != null && unitPrice != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }
    
    /**
     * Get item name for display purposes
     */
    public String getItemName() {
        return item != null ? item.getItemName() : "";
    }
    
    /**
     * Get item category for display purposes
     */
    public String getItemCategory() {
        return item != null ? item.getCategory() : "";
    }
    
    /**
     * Check if this bill item represents the same item as another bill item
     */
    public boolean isSameItem(BillItem other) {
        return other != null && 
               this.item != null && 
               other.item != null && 
               this.item.equals(other.item);
    }
    
    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillItem billItem = (BillItem) o;
        return Objects.equals(billItemId, billItem.billItemId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(billItemId);
    }
    
    // ToString
    @Override
    public String toString() {
        return "BillItem{" +
                "billItemId=" + billItemId +
                ", bill=" + (bill != null ? bill.getBillId() : null) +
                ", item=" + (item != null ? item.getItemName() : null) +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}

