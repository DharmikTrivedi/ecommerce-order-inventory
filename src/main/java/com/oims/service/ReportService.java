package com.oims.service;

import com.oims.dto.MonthlySales;
import com.oims.dto.TopProduct;
import com.oims.model.*;
import com.oims.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final ReturnRequestRepository returnRepo;
    private final StockMovementRepository movementRepo;

    public ReportService(OrderRepository orderRepo, OrderItemRepository orderItemRepo, ProductRepository productRepo,
                         CustomerRepository customerRepo, ReturnRequestRepository returnRepo,
                         StockMovementRepository movementRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.returnRepo = returnRepo;
        this.movementRepo = movementRepo;
    }

    public List<MonthlySales> monthlySales() {
        return orderRepo.monthlySales();
    }

    public List<TopProduct> topSelling(int n) {
        return orderItemRepo.findTopSelling(PageRequest.of(0, n));
    }

    public List<Product> lowStock() {
        return productRepo.findLowStock();
    }

    public Map<String, Object> dashboardCounts() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("products", productRepo.countByActiveTrue());
        m.put("customers", customerRepo.count());
        m.put("orders", orderRepo.count());
        m.put("pendingOrders", orderRepo.countByStatus(OrderStatus.PLACED));
        m.put("lowStock", productRepo.countLowStock());
        m.put("pendingReturns", returnRepo.countByStatus(ReturnStatus.REQUESTED));
        BigDecimal revenue = orderRepo.totalRevenue();
        m.put("revenue", revenue);
        return m;
    }

    public List<Order> recentOrders() {
        return orderRepo.findTop5ByOrderByOrderDateDesc();
    }

    public List<StockMovement> recentStockMovements() {
        return movementRepo.findTop8ByOrderByCreatedAtDesc();
    }
}
