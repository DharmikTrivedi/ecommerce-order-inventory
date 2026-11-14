package com.oims.repository;

import com.oims.model.ReturnRequest;
import com.oims.model.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Integer> {

    // quantity already returned (or waiting) for one order line - rejected ones don't count
    @Query("select coalesce(sum(r.quantity), 0) from ReturnRequest r " +
           "where r.orderItem.id = :itemId and r.status <> com.oims.model.ReturnStatus.REJECTED")
    int sumActiveReturnQty(@Param("itemId") Integer orderItemId);

    List<ReturnRequest> findByStatusOrderByRequestedOnDesc(ReturnStatus status);

    List<ReturnRequest> findAllByOrderByRequestedOnDesc();

    long countByStatus(ReturnStatus status);
}
