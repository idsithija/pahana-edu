package com.pahanaedu.dao.impl;

import com.pahanaedu.BaseTestCase;
import com.pahanaedu.model.Bill;
import com.pahanaedu.model.Customer;
import com.pahanaedu.model.Item;
import com.pahanaedu.model.BillItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bill DAO Implementation Tests")
class BillDAOImplTest extends BaseTestCase {
    
    private BillDAOImpl billDAO;
    private Customer testCustomer;
    private Item testItem;
    private Bill testBill1;
    private Bill testBill2;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        if (isTestEnvironmentReady()) {
            billDAO = new BillDAOImpl();
            billDAO.entityManager = entityManager;
            
            testCustomer = new Customer("ACC001", "Test Customer");
            testCustomer.setAddress("123 Test St");
            testCustomer.setTelephoneNumber("555-0001");
            
            testItem = new Item("Test Book", new BigDecimal("25.00"));
            testItem.setStockQuantity(10);
            testItem.setCategory("Books");
            
            testBill1 = new Bill(testCustomer, new BigDecimal("75.00"));
            testBill1.setBillDate(LocalDate.of(2023, 6, 15));
            testBill1.setStatus(Bill.BillStatus.PENDING);
            
            testBill2 = new Bill(testCustomer, new BigDecimal("150.00"));
            testBill2.setBillDate(LocalDate.of(2023, 6, 20));
            testBill2.setStatus(Bill.BillStatus.PAID);
        }
    }
    
    @Nested
    @DisplayName("Base CRUD Operation Tests")
    class BaseCrudTests {
        
        @Test
        @DisplayName("Should save bill successfully")
        void testSaveBill() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            entityManager.persist(testCustomer);
            Bill savedBill = billDAO.save(testBill1);
            commitTransaction();
            
            assertNotNull(savedBill);
            assertNotNull(savedBill.getBillId());
            assertEquals(testBill1.getTotalAmount(), savedBill.getTotalAmount());
            assertEquals(testBill1.getBillDate(), savedBill.getBillDate());
            assertEquals(testBill1.getStatus(), savedBill.getStatus());
        }
        
        @Test
        @DisplayName("Should update bill successfully")
        void testUpdateBill() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            entityManager.persist(testCustomer);
            Bill savedBill = billDAO.save(testBill1);
            commitTransaction();
            
            savedBill.setTotalAmount(new BigDecimal("100.00"));
            savedBill.setStatus(Bill.BillStatus.PAID);
            
            beginTransaction();
            Bill updatedBill = billDAO.update(savedBill);
            commitTransaction();
            
            assertEquals(new BigDecimal("100.00"), updatedBill.getTotalAmount());
            assertEquals(Bill.BillStatus.PAID, updatedBill.getStatus());
        }
        
        @Test
        @DisplayName("Should find bill by ID")
        void testFindById() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            entityManager.persist(testCustomer);
            Bill savedBill = billDAO.save(testBill1);
            commitTransaction();
            clearEntityManager();
            
            Optional<Bill> foundBill = billDAO.findById(savedBill.getBillId());
            
            assertTrue(foundBill.isPresent());
            assertEquals(savedBill.getBillId(), foundBill.get().getBillId());
            assertEquals(savedBill.getTotalAmount(), foundBill.get().getTotalAmount());
        }
        
        @Test
        @DisplayName("Should delete bill by ID")
        void testDeleteById() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            entityManager.persist(testCustomer);
            Bill savedBill = billDAO.save(testBill1);
            commitTransaction();
            
            Long billId = savedBill.getBillId();
            
            beginTransaction();
            billDAO.deleteById(billId);
            commitTransaction();
            clearEntityManager();
            
            Optional<Bill> foundBill = billDAO.findById(billId);
            assertFalse(foundBill.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Bill with Items Tests")
    class BillWithItemsTests {
        
        @Test
        @DisplayName("Should save bill with bill items")
        void testSaveBillWithItems() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            entityManager.persist(testCustomer);
            entityManager.persist(testItem);
            
            Bill bill = new Bill(testCustomer);
            BillItem billItem = new BillItem(bill, testItem, 2, testItem.getUnitPrice());
            bill.addBillItem(billItem);
            
            Bill savedBill = billDAO.save(bill);
            commitTransaction();
            
            assertNotNull(savedBill);
            assertEquals(1, savedBill.getBillItems().size());
            assertEquals(new BigDecimal("50.00"), savedBill.getTotalAmount());
        }
        
        @Test
        @DisplayName("Should update bill with modified items")
        void testUpdateBillWithModifiedItems() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            entityManager.persist(testCustomer);
            entityManager.persist(testItem);
            
            Bill bill = new Bill(testCustomer);
            BillItem billItem = new BillItem(bill, testItem, 2, testItem.getUnitPrice());
            bill.addBillItem(billItem);
            
            Bill savedBill = billDAO.save(bill);
            commitTransaction();
            
            // Modify the bill item quantity
            BillItem existingBillItem = savedBill.getBillItems().get(0);
            existingBillItem.setQuantity(3);
            savedBill.recalculateTotal();
            
            beginTransaction();
            Bill updatedBill = billDAO.update(savedBill);
            commitTransaction();
            
            assertEquals(Integer.valueOf(3), updatedBill.getBillItems().get(0).getQuantity());
            assertEquals(new BigDecimal("75.00"), updatedBill.getTotalAmount());
        }
    }
    
    @Nested
    @DisplayName("Query Operation Tests")
    class QueryOperationTests {
        
        @BeforeEach
        void setUpBills() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            entityManager.persist(testCustomer);
            billDAO.save(testBill1);
            billDAO.save(testBill2);
            commitTransaction();
            clearEntityManager();
        }
        
        @Test
        @DisplayName("Should find all bills")
        void testFindAll() {
            if (!isTestEnvironmentReady()) return;
            
            List<Bill> allBills = billDAO.findAll();
            
            assertNotNull(allBills);
            assertTrue(allBills.size() >= 2);
        }
        
        @Test
        @DisplayName("Should count bills correctly")
        void testCount() {
            if (!isTestEnvironmentReady()) return;
            
            long billCount = billDAO.count();
            
            assertTrue(billCount >= 2);
        }
        
        @Test
        @DisplayName("Should check if bill exists by ID")
        void testExistsById() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            entityManager.persist(new Customer("ACC999", "Temp Customer"));
            Bill tempBill = billDAO.save(new Bill());
            commitTransaction();
            
            assertTrue(billDAO.existsById(tempBill.getBillId()));
            assertFalse(billDAO.existsById(999999L));
        }
        
        @Test
        @DisplayName("Should find bills with pagination")
        void testFindWithPagination() {
            if (!isTestEnvironmentReady()) return;
            
            List<Bill> paginatedBills = billDAO.findWithPagination(0, 1);
            
            assertNotNull(paginatedBills);
            assertTrue(paginatedBills.size() <= 1);
        }
    }
    
    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {
        
        @Test
        @DisplayName("Should maintain bill status consistency")
        void testBillStatusConsistency() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            entityManager.persist(testCustomer);
            Bill savedBill = billDAO.save(testBill1);
            commitTransaction();
            
            assertEquals(Bill.BillStatus.PENDING, savedBill.getStatus());
            assertTrue(savedBill.isPending());
            assertFalse(savedBill.isPaid());
            assertFalse(savedBill.isCancelled());
            
            savedBill.markAsPaid();
            
            beginTransaction();
            Bill updatedBill = billDAO.update(savedBill);
            commitTransaction();
            
            assertEquals(Bill.BillStatus.PAID, updatedBill.getStatus());
            assertFalse(updatedBill.isPending());
            assertTrue(updatedBill.isPaid());
            assertFalse(updatedBill.isCancelled());
        }
        
        @Test
        @DisplayName("Should handle bill total amount recalculation")
        void testBillTotalRecalculation() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            entityManager.persist(testCustomer);
            entityManager.persist(testItem);
            
            Bill bill = new Bill(testCustomer);
            BillItem billItem1 = new BillItem(bill, testItem, 2, new BigDecimal("10.00"));
            BillItem billItem2 = new BillItem(bill, testItem, 1, new BigDecimal("15.00"));
            
            bill.getBillItems().add(billItem1);
            bill.getBillItems().add(billItem2);
            bill.recalculateTotal();
            
            Bill savedBill = billDAO.save(bill);
            commitTransaction();
            
            assertEquals(new BigDecimal("35.00"), savedBill.getTotalAmount());
            assertEquals(2, savedBill.getBillItems().size());
            assertEquals(3, savedBill.getTotalItemCount());
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle saving null bill")
        void testSaveNullBill() {
            if (!isTestEnvironmentReady()) return;
            
            assertThrows(RuntimeException.class, () -> {
                billDAO.save(null);
            });
        }
        
        @Test
        @DisplayName("Should handle updating null bill")
        void testUpdateNullBill() {
            if (!isTestEnvironmentReady()) return;
            
            assertThrows(RuntimeException.class, () -> {
                billDAO.update(null);
            });
        }
        
        @Test
        @DisplayName("Should handle finding bill with null ID")
        void testFindByNullId() {
            if (!isTestEnvironmentReady()) return;
            
            Optional<Bill> result = billDAO.findById(null);
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should handle deleting bill with null ID")
        void testDeleteByNullId() {
            if (!isTestEnvironmentReady()) return;
            
            assertDoesNotThrow(() -> {
                beginTransaction();
                billDAO.deleteById(null);
                commitTransaction();
            });
        }
        
        @Test
        @DisplayName("Should handle deleting non-existent bill")
        void testDeleteNonExistentBill() {
            if (!isTestEnvironmentReady()) return;
            
            assertDoesNotThrow(() -> {
                beginTransaction();
                billDAO.deleteById(999999L);
                commitTransaction();
            });
        }
    }
    
    @Nested
    @DisplayName("Relationship Integrity Tests")
    class RelationshipIntegrityTests {
        
        @Test
        @DisplayName("Should maintain customer-bill relationship")
        void testCustomerBillRelationship() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            entityManager.persist(testCustomer);
            Bill savedBill = billDAO.save(testBill1);
            commitTransaction();
            clearEntityManager();
            
            Optional<Bill> foundBill = billDAO.findById(savedBill.getBillId());
            
            assertTrue(foundBill.isPresent());
            assertNotNull(foundBill.get().getCustomer());
            assertEquals(testCustomer.getAccountNumber(), 
                foundBill.get().getCustomer().getAccountNumber());
        }
        
        @Test
        @DisplayName("Should cascade delete bill items when bill is deleted")
        void testCascadeDeleteBillItems() {
            if (!isTestEnvironmentReady()) return;
            
            beginTransaction();
            entityManager.persist(testCustomer);
            entityManager.persist(testItem);
            
            Bill bill = new Bill(testCustomer);
            BillItem billItem = new BillItem(bill, testItem, 1, testItem.getUnitPrice());
            bill.addBillItem(billItem);
            
            Bill savedBill = billDAO.save(bill);
            Long billId = savedBill.getBillId();
            commitTransaction();
            
            beginTransaction();
            billDAO.deleteById(billId);
            commitTransaction();
            
            // Verify bill items are also deleted (orphan removal)
            Long billItemCount = entityManager.createQuery(
                "SELECT COUNT(bi) FROM BillItem bi WHERE bi.bill.billId = :billId", Long.class)
                .setParameter("billId", billId)
                .getSingleResult();
            
            assertEquals(0L, billItemCount.longValue());
        }
    }
}