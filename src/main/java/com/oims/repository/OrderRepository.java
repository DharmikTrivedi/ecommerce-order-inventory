package com.oims.repository;

import com.oims.dto.MonthlySales;
import com.oims.model.Order;
import com.oims.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query(value = "select o from Order o join fetch o.customer c where " +
                   "(:status is null or o.status = :status) and " +
                   "(:kw is null or o.orderNumber like concat('%', :kw, '%') " +
                   " or lower(concat(c.firstName, ' ', c.lastName)) like lower(concat('%', :kw, '%')))",
           countQuery = "select count(o) from Order o join o.customer c where " +
                   "(:status is null or o.status = :status) and " +
                   "(:kw is null or o.orderNumber like concat('%', :kw, '%') " +
                   " or lower(concat(c.firstName, ' ', c.lastName)) like lower(concat('%', :kw, '%')))")
    Page<Order> search(@Param("status") OrderStatus status, @Param("kw") String keyword, Pageable pageable);

    List<Order> findByCustomerIdOrderByOrderDateDesc(Integer customerId);

    @Query("select new com.oims.dto.MonthlySales(year(o.orderDate), month(o.orderDate), count(o), sum(o.totalAmount)) " +
           "from Order o where o.status <> com.oims.model.OrderStatus.CANCELLED " +
           "group by year(o.orderDate), month(o.orderDate) " +
           "order by year(o.orderDate) desc, month(o.orderDate) desc")
    List<MonthlySales> monthlySales();

    @Query("select coalesce(sum(o.totalAmount), 0) from Order o where o.status <> com.oims.model.OrderStatus.CANCELLED")
    BigDecimal totalRevenue();

    long countByStatus(OrderStatus status);

    List<Order> findTop5ByOrderByOrderDateDesc();

    boolean existsByCustomerId(Integer customerId);
}
