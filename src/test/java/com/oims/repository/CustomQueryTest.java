package com.oims.repository;

import com.oims.TestData;
import com.oims.dto.MonthlySales;
import com.oims.dto.TopProduct;
import com.oims.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomQueryTest {

    @Autowired TestEntityManager em;
    @Autowired ProductRepository productRepo;
    @Autowired OrderRepository orderRepo;
    @Autowired OrderItemRepository orderItemRepo;
    @Autowired ReturnRequestRepository returnRepo;

    Product mouse, keyboard, charger;
    Customer cust;
    Order firstOrder;

    @BeforeEach
    void setUp() {
        Category el = em.persist(TestData.category("Electronics"));
        Seller s = em.persist(TestData.seller("Gupta Electronics", "g@test.com"));
        mouse = em.persist(TestData.product("EL-1001", "Wireless Mouse", "599.00", 45, 10, el, s));
        keyboard = em.persist(TestData.product("EL-1002", "Mechanical Keyboard", "2499.00", 18, 5, el, s));
        charger = em.persist(TestData.product("EL-1003", "USB-C Charger", "899.00", 4, 8, el, s));
        cust = em.persist(TestData.customer("Aarav", "aarav@test.com"));

        firstOrder = saveOrder("ORD-1", LocalDateTime.of(2026, 9, 3, 10, 0), OrderStatus.DELIVERED, mouse, 3);
        saveOrder("ORD-2", LocalDateTime.of(2026, 10, 5, 10, 0), OrderStatus.DELIVERED, keyboard, 1);
        saveOrder("ORD-3", LocalDateTime.of(2026, 10, 20, 10, 0), OrderStatus.PLACED, mouse, 2);
        saveOrder("ORD-4", LocalDateTime.of(2026, 10, 21, 10, 0), OrderStatus.CANCELLED, charger, 4);
        em.flush();
    }

    @Test
    void findLowStock_returnsOnlyItemsAtOrBelowReorderLevel() {
        List<Product> low = productRepo.findLowStock();
        assertThat(low).extracting(Product::getSku).containsExactly("EL-1003");
        assertThat(productRepo.countLowStock()).isEqualTo(1);
    }

    @Test
    void search_filtersByKeywordAndPages() {
        Page<Product> page = productRepo.search("key", null, false, PageRequest.of(0, 5, Sort.by("name")));
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getName()).isEqualTo("Mechanical Keyboard");

        Page<Product> all = productRepo.search(null, null, false, PageRequest.of(0, 2, Sort.by("price")));
        assertThat(all.getTotalPages()).isEqualTo(2);
        assertThat(all.getContent().get(0).getSku()).isEqualTo("EL-1001");
    }

    @Test
    void monthlySales_groupsByMonthAndSkipsCancelled() {
        List<MonthlySales> rows = orderRepo.monthlySales();
        assertThat(rows).hasSize(2);
        MonthlySales oct = rows.get(0);   // newest first
        assertThat(oct.month()).isEqualTo(10);
        assertThat(oct.orderCount()).isEqualTo(2);                       // cancelled ORD-4 not counted
        assertThat(oct.revenue()).isEqualByComparingTo("3697.00");       // 2499 + 2*599
    }

    @Test
    void topSelling_ordersByQuantity() {
        List<TopProduct> top = orderItemRepo.findTopSelling(PageRequest.of(0, 5));
        assertThat(top.get(0).sku()).isEqualTo("EL-1001");
        assertThat(top.get(0).qtySold()).isEqualTo(5L);
        assertThat(top).extracting(TopProduct::sku).doesNotContain("EL-1003"); // only sold in a cancelled order
    }

    @Test
    void orderSearch_byStatusAndCustomerName() {
        Page<Order> placed = orderRepo.search(OrderStatus.PLACED, null, PageRequest.of(0, 10));
        assertThat(placed.getContent()).extracting(Order::getOrderNumber).containsExactly("ORD-3");

        Page<Order> byName = orderRepo.search(null, "aarav", PageRequest.of(0, 10));
        assertThat(byName.getTotalElements()).isEqualTo(4);
    }

    @Test
    void sumActiveReturnQty_ignoresRejected() {
        OrderItem line = firstOrder.getItems().get(0);   // ORD-1, 3 mice
        saveReturn(line, 1, ReturnStatus.APPROVED);
        saveReturn(line, 1, ReturnStatus.REJECTED);
        em.flush();
        assertThat(returnRepo.sumActiveReturnQty(line.getId())).isEqualTo(1);
    }

    private Order saveOrder(String no, LocalDateTime when, OrderStatus status, Product p, int qty) {
        Order o = new Order();
        o.setOrderNumber(no);
        o.setCustomer(cust);
        o.setOrderDate(when);
        o.setStatus(status);
        o.setPaymentMode(PaymentMode.UPI);
        o.setShippingAddress("1 Test Street, Pune");
        OrderItem item = new OrderItem(p, qty);
        o.addItem(item);
        o.setTotalAmount(item.getLineTotal());
        return em.persist(o);
    }

    private void saveReturn(OrderItem line, int qty, ReturnStatus status) {
        ReturnRequest r = new ReturnRequest();
        r.setOrderItem(line);
        r.setQuantity(qty);
        r.setReason("test reason text");
        r.setStatus(status);
        r.setRequestedOn(LocalDateTime.now());
        em.persist(r);
    }
}
