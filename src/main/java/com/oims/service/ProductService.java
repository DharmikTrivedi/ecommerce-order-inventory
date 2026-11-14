package com.oims.service;

import com.oims.exception.BusinessException;
import com.oims.exception.NotFoundException;
import com.oims.model.Product;
import com.oims.repository.OrderItemRepository;
import com.oims.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepo;
    private final OrderItemRepository orderItemRepo;

    public ProductService(ProductRepository productRepo, OrderItemRepository orderItemRepo) {
        this.productRepo = productRepo;
        this.orderItemRepo = orderItemRepo;
    }

    public Page<Product> search(String keyword, Integer categoryId, boolean activeOnly, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return productRepo.search(kw, categoryId, activeOnly, pageable);
    }

    public List<Product> quickSearch(String text) {
        return productRepo.findTop10ByActiveTrueAndNameContainingIgnoreCaseOrderByName(text.trim());
    }

    public Product get(Integer id) {
        return productRepo.findById(id).orElseThrow(() -> new NotFoundException("Product", id));
    }

    public List<Product> lowStock() {
        return productRepo.findLowStock();
    }

    public boolean isSkuTaken(Product p) {
        return p.getId() == null
                ? productRepo.existsBySkuIgnoreCase(p.getSku())
                : productRepo.existsBySkuIgnoreCaseAndIdNot(p.getSku(), p.getId());
    }

    @Transactional
    public Product save(Product p) {
        if (isSkuTaken(p)) {
            throw new BusinessException("SKU " + p.getSku() + " is already used by another product");
        }
        if (p.getId() == null) {
            p.setCreatedOn(LocalDate.now());
        } else {
            p.setCreatedOn(get(p.getId()).getCreatedOn());
        }
        return productRepo.save(p);
    }

    /**
     * Products that were sold before cannot be removed because order history points to them.
     * In that case the product is only deactivated. Returns true if it was really deleted.
     */
    @Transactional
    public boolean delete(Integer id) {
        Product p = get(id);
        if (orderItemRepo.existsByProductId(id)) {
            p.setActive(false);
            return false;
        }
        productRepo.delete(p);
        return true;
    }

    public void checkSellable(Product p) {
        if (!p.isActive()) {
            throw new BusinessException(p.getName() + " is no longer available");
        }
    }
}
