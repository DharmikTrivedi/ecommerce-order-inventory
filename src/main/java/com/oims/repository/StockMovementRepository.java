package com.oims.repository;

import com.oims.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Integer> {

    List<StockMovement> findTop8ByOrderByCreatedAtDesc();
}
