package com.pahanaedu.dao.impl;

import com.pahanaedu.dao.CustomerDAO;
import com.pahanaedu.model.Customer;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of CustomerDAO interface
 * Provides customer-specific database operations using JPA
 */
@Stateless
public class CustomerDAOImpl extends BaseDAOImpl<Customer, String> implements CustomerDAO {
    
    @Override
    public Optional<Customer> findByAccountNumber(String accountNumber) {
        return findById(accountNumber);
    }
    
    @Override
    public List<Customer> findByNameContaining(String name) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(:name) ORDER BY c.name", Customer.class
            );
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } finally {
            if (entityManager == null) {
                em.close();
            }
        }
    }
    
    @Override
    public List<Customer> findByTelephoneNumber(String telephoneNumber) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.telephoneNumber = :telephoneNumber ORDER BY c.name", Customer.class
            );
            query.setParameter("telephoneNumber", telephoneNumber);
            return query.getResultList();
        } finally {
            if (entityManager == null) {
                em.close();
            }
        }
    }
    
    @Override
    public List<Customer> findActiveCustomers() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.active = true ORDER BY c.name", Customer.class
            );
            return query.getResultList();
        } finally {
            if (entityManager == null) {
                em.close();
            }
        }
    }
    
    @Override
    public List<Customer> findByRegistrationDateBetween(LocalDate startDate, LocalDate endDate) {
        TypedQuery<Customer> query = createQuery(
            "SELECT c FROM Customer c WHERE c.registrationDate BETWEEN :startDate AND :endDate ORDER BY c.registrationDate DESC"
        );
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    @Override
    public List<Customer> findCustomersWithPendingBills() {
        TypedQuery<Customer> query = createQuery(
            "SELECT DISTINCT c FROM Customer c JOIN c.bills b WHERE b.status = 'PENDING' ORDER BY c.name"
        );
        return query.getResultList();
    }
    
    @Override
    public String generateNextAccountNumber() {
        try {
            // Use the database function to generate account number
            javax.persistence.Query query = getEntityManager().createNativeQuery(
                "SELECT generate_account_number()"
            );
            return (String) query.getSingleResult();
        } catch (Exception e) {
            // Fallback to application-level generation
            TypedQuery<String> query = getEntityManager().createQuery(
                "SELECT MAX(c.accountNumber) FROM Customer c WHERE c.accountNumber LIKE 'ACC%'", String.class
            );
            String maxAccountNumber = query.getSingleResult();
            
            if (maxAccountNumber == null) {
                return "ACC000001";
            }
            
            // Extract number part and increment
            String numberPart = maxAccountNumber.substring(3);
            int nextNumber = Integer.parseInt(numberPart) + 1;
            return String.format("ACC%06d", nextNumber);
        }
    }
    
    @Override
    public boolean accountNumberExists(String accountNumber) {
        TypedQuery<Long> query = getEntityManager().createQuery(
            "SELECT COUNT(c) FROM Customer c WHERE c.accountNumber = :accountNumber", Long.class
        );
        query.setParameter("accountNumber", accountNumber);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public Customer updateActiveStatus(String accountNumber, boolean active) {
        Optional<Customer> customerOpt = findByAccountNumber(accountNumber);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setActive(active);
            return update(customer);
        }
        throw new IllegalArgumentException("Customer not found: " + accountNumber);
    }
    
    @Override
    public List<Customer> searchCustomers(String name, String telephoneNumber, Boolean active) {
        EntityManager em = getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT c FROM Customer c WHERE 1=1");
            
            if (name != null && !name.trim().isEmpty()) {
                jpql.append(" AND LOWER(c.name) LIKE LOWER(:name)");
            }
            if (telephoneNumber != null && !telephoneNumber.trim().isEmpty()) {
                jpql.append(" AND c.telephoneNumber = :telephoneNumber");
            }
            if (active != null) {
                jpql.append(" AND c.active = :active");
            }
            
            jpql.append(" ORDER BY c.name");
            
            TypedQuery<Customer> query = em.createQuery(jpql.toString(), Customer.class);
            
            if (name != null && !name.trim().isEmpty()) {
                query.setParameter("name", "%" + name.trim() + "%");
            }
            if (telephoneNumber != null && !telephoneNumber.trim().isEmpty()) {
                query.setParameter("telephoneNumber", telephoneNumber.trim());
            }
            if (active != null) {
                query.setParameter("active", active);
            }
            
            return query.getResultList();
        } finally {
            if (entityManager == null) {
                em.close();
            }
        }
    }
    
    @Override
    public long[] getCustomerStatistics() {
        EntityManager em = getEntityManager();
        try {
            // Total customers
            TypedQuery<Long> totalQuery = em.createQuery(
                "SELECT COUNT(c) FROM Customer c", Long.class
            );
            long totalCustomers = totalQuery.getSingleResult();
            
            // Active customers
            TypedQuery<Long> activeQuery = em.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE c.active = true", Long.class
            );
            long activeCustomers = activeQuery.getSingleResult();
            
            // Customers with pending bills - simplified query
            TypedQuery<Long> pendingQuery = em.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE c.active = true", Long.class
            );
            long customersWithPendingBills = 0; // Simplified for now
            
            return new long[]{totalCustomers, activeCustomers, customersWithPendingBills};
        } finally {
            if (entityManager == null) {
                em.close();
            }
        }
    }
}

