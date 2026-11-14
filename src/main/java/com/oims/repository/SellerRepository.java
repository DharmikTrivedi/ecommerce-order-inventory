package com.oims.repository;

import com.oims.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerRepository extends JpaRepository<Seller, Integer> {

    List<Seller> findAllByOrderByNameAsc();

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);

    List<Seller> findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrderByName(String name, String city);
}
