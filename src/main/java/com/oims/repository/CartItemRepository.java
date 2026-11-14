package com.oims.repository;

import com.oims.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByCustomerIdOrderByAddedAtAsc(Integer customerId);

    Optional<CartItem> findByCustomerIdAndProductId(Integer customerId, Integer productId);

    @Query("select coalesce(sum(c.quantity), 0) from CartItem c where c.customer.id = :cid")
    int countItemsInCart(@Param("cid") Integer customerId);

    @Modifying
    @Query("delete from CartItem c where c.customer.id = :cid")
    void clearCart(@Param("cid") Integer customerId);
}
