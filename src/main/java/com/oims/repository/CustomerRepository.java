package com.oims.repository;

import com.oims.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("select c from Customer c where " +
           "(:kw is null or lower(concat(c.firstName, ' ', c.lastName)) like lower(concat('%', :kw, '%')) " +
           " or lower(c.email) like lower(concat('%', :kw, '%'))) " +
           "and (:city is null or c.city = :city)")
    Page<Customer> search(@Param("kw") String keyword, @Param("city") String city, Pageable pageable);

    @Query("select distinct c.city from Customer c order by c.city")
    List<String> findDistinctCities();

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);

    List<Customer> findAllByOrderByFirstNameAsc();
}
