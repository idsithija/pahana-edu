package com.pahanaedu.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Customer entity representing bookshop customers
 * Each customer has a unique account number and can have multiple bills
 */
@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @Column(name = "account_number", length = 20)
    private String accountNumber;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "telephone_number", length = 20)
    private String telephoneNumber;
    
    @Column(name = "registration_date")
    private LocalDate registrationDate;
    
    @Column(name = "active")
    private Boolean active = true;
    
    // One-to-many relationship with bills
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bill> bills = new ArrayList<>();
    
    // Constructors
    public Customer() {
        this.registrationDate = LocalDate.now();
    }
    
    public Customer(String accountNumber, String name) {
        this();
        this.accountNumber = accountNumber;
        this.name = name;
    }
    
    public Customer(String accountNumber, String name, String address, String telephoneNumber) {
        this(accountNumber, name);
        this.address = address;
        this.telephoneNumber = telephoneNumber;
    }
    
    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getTelephoneNumber() {
        return telephoneNumber;
    }
    
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
    
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    /**
     * Get registration date as java.util.Date for JSP compatibility
     * @return Date object for use with JSTL fmt:formatDate
     */
    public Date getRegistrationDateAsDate() {
        if (registrationDate == null) {
            return null;
        }
        return Date.from(registrationDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public List<Bill> getBills() {
        return bills;
    }
    
    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }
    
    // Business methods
    public boolean isActive() {
        return Boolean.TRUE.equals(this.active);
    }
    
    public void addBill(Bill bill) {
        bills.add(bill);
        bill.setCustomer(this);
    }
    
    public void removeBill(Bill bill) {
        bills.remove(bill);
        bill.setCustomer(null);
    }
    
    /**
     * Get total amount of all bills for this customer
     */
    public double getTotalBillAmount() {
        return bills.stream()
                .mapToDouble(bill -> bill.getTotalAmount().doubleValue())
                .sum();
    }
    
    /**
     * Get count of pending bills
     */
    public long getPendingBillsCount() {
        return bills.stream()
                .filter(bill -> Bill.BillStatus.PENDING.equals(bill.getStatus()))
                .count();
    }
    
    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(accountNumber, customer.accountNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
    
    // ToString
    @Override
    public String toString() {
        return "Customer{" +
                "accountNumber='" + accountNumber + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", telephoneNumber='" + telephoneNumber + '\'' +
                ", registrationDate=" + registrationDate +
                ", active=" + active +
                '}';
    }
}

