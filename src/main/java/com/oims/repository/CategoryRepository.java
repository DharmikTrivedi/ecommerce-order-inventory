package com.oims.repository;

import com.oims.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);

    boolean existsByNameIgnoreCase(String name);

    // category name + number of products in it
    @Query("select c.id, c.name, c.description, count(p) from Category c left join c.products p " +
           "group by c.id, c.name, c.description order by c.name")
    List<Object[]> findAllWithProductCount();
}
