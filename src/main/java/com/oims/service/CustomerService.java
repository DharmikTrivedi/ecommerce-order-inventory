package com.oims.service;

import com.oims.exception.BusinessException;
import com.oims.exception.NotFoundException;
import com.oims.model.Customer;
import com.oims.model.Product;
import com.oims.repository.CustomerRepository;
import com.oims.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;
    private final ProductService productService;

    public CustomerService(CustomerRepository customerRepo, OrderRepository orderRepo, ProductService productService) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
        this.productService = productService;
    }

    public Page<Customer> search(String keyword, String city, Pageable pageable) {
        return customerRepo.search(blankToNull(keyword), blankToNull(city), pageable);
    }

    public List<Customer> findAll() {
        return customerRepo.findAllByOrderByFirstNameAsc();
    }

    public List<String> cities() {
        return customerRepo.findDistinctCities();
    }

    public Customer get(Integer id) {
        return customerRepo.findById(id).orElseThrow(() -> new NotFoundException("Customer", id));
    }

    public boolean isEmailTaken(Customer c) {
        return c.getId() == null
                ? customerRepo.existsByEmailIgnoreCase(c.getEmail())
                : customerRepo.existsByEmailIgnoreCaseAndIdNot(c.getEmail(), c.getId());
    }

    @Transactional
    public Customer save(Customer c) {
        if (isEmailTaken(c)) {
            throw new BusinessException("Email " + c.getEmail() + " is already used by another customer");
        }
        if (c.getId() == null) {
            c.setRegisteredOn(LocalDate.now());
        } else {
            // keep fields that are not on the form
            Customer existing = get(c.getId());
            c.setRegisteredOn(existing.getRegisteredOn());
            c.setWishlist(existing.getWishlist());
        }
        return customerRepo.save(c);
    }

    @Transactional
    public void delete(Integer id) {
        Customer c = get(id);
        if (orderRepo.existsByCustomerId(id)) {
            throw new BusinessException(c.getFullName() + " has placed orders, so the record is kept for order history");
        }
        customerRepo.delete(c);
    }

    /** adds the product if it is not in the wishlist, removes it otherwise. returns true if added */
    @Transactional
    public boolean toggleWishlist(Integer customerId, Integer productId) {
        Customer c = get(customerId);
        Product p = productService.get(productId);
        if (c.getWishlist().contains(p)) {
            c.getWishlist().remove(p);
            return false;
        }
        c.getWishlist().add(p);
        return true;
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
