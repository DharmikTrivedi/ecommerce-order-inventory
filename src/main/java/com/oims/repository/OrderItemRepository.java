package com.oims.repository;

import com.oims.dto.TopProduct;
import com.oims.model.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    // best sellers by quantity, cancelled orders not counted
    @Query("select new com.oims.dto.TopProduct(p.id, p.name, p.sku, sum(i.quantity), sum(i.lineTotal)) " +
           "from OrderItem i join i.product p join i.order o " +
           "where o.status <> com.oims.model.OrderStatus.CANCELLED " +
           "group by p.id, p.name, p.sku order by sum(i.quantity) desc, sum(i.lineTotal) desc")
    List<TopProduct> findTopSelling(Pageable pageable);

    boolean existsByProductId(Integer productId);
}
