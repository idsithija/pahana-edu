package com.pahanaedu.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Item entity representing bookshop inventory items
 * Each item has stock quantity and can be included in multiple bills
 */
@Entity
@Table(name = "items")
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;
    
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;
    
    @Column(name = "category", length = 50)
    private String category;
    
    // One-to-many relationship with bill items
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BillItem> billItems = new ArrayList<>();
    
    // Constructors
    public Item() {}
    
    public Item(String itemName, BigDecimal unitPrice) {
        this.itemName = itemName;
        this.unitPrice = unitPrice;
    }
    
    public Item(String itemName, String description, BigDecimal unitPrice, Integer stockQuantity, String category) {
        this.itemName = itemName;
        this.description = description;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public List<BillItem> getBillItems() {
        return billItems;
    }
    
    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }
    
    // Business methods
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
    
    public boolean hasStock(int requiredQuantity) {
        return stockQuantity != null && stockQuantity >= requiredQuantity;
    }
    
    public void reduceStock(int quantity) {
        if (stockQuantity == null) {
            stockQuantity = 0;
        }
        if (stockQuantity < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + stockQuantity + ", Required: " + quantity);
        }
        stockQuantity -= quantity;
    }
    
    public void increaseStock(int quantity) {
        if (stockQuantity == null) {
            stockQuantity = 0;
        }
        stockQuantity += quantity;
    }
    
    /**
     * Calculate total price for given quantity
     */
    public BigDecimal calculateTotalPrice(int quantity) {
        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    /**
     * Get total quantity sold across all bills
     */
    public int getTotalQuantitySold() {
        return billItems.stream()
                .mapToInt(BillItem::getQuantity)
                .sum();
    }
    
    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(itemId, item.itemId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }
    
    // ToString
    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", description='" + description + '\'' +
                ", unitPrice=" + unitPrice +
                ", stockQuantity=" + stockQuantity +
                ", category='" + category + '\'' +
                '}';
    }
}

