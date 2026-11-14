package com.oims.repository;

import com.oims.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // used by the product listing page (search + category filter + paging/sorting)
    @Query(value = "select p from Product p join fetch p.category join fetch p.seller where " +
                   "(:kw is null or lower(p.name) like lower(concat('%', :kw, '%')) or lower(p.sku) like lower(concat('%', :kw, '%'))) " +
                   "and (:catId is null or p.category.id = :catId) " +
                   "and (:activeOnly = false or p.active = true)",
           countQuery = "select count(p) from Product p where " +
                   "(:kw is null or lower(p.name) like lower(concat('%', :kw, '%')) or lower(p.sku) like lower(concat('%', :kw, '%'))) " +
                   "and (:catId is null or p.category.id = :catId) " +
                   "and (:activeOnly = false or p.active = true)")
    Page<Product> search(@Param("kw") String keyword, @Param("catId") Integer categoryId,
                         @Param("activeOnly") boolean activeOnly, Pageable pageable);

    // products at or below their reorder level
    @Query("select p from Product p join fetch p.seller where p.active = true and p.stockQty <= p.reorderLevel " +
           "order by p.stockQty asc")
    List<Product> findLowStock();

    @Query("select count(p) from Product p where p.active = true and p.stockQty <= p.reorderLevel")
    long countLowStock();

    // row lock while an order is being placed so two orders can't oversell the same item
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Integer id);

    List<Product> findTop10ByActiveTrueAndNameContainingIgnoreCaseOrderByName(String name);

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Integer id);

    long countByActiveTrue();
}
