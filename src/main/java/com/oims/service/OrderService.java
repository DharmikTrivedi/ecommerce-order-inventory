package com.oims.service;

import com.oims.exception.BusinessException;
import com.oims.exception.NotFoundException;
import com.oims.model.*;
import com.oims.repository.CartItemRepository;
import com.oims.repository.OrderRepository;
import com.oims.repository.ProductRepository;
import com.oims.repository.StockMovementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderService {

    private static final DateTimeFormatter NUM_FMT = DateTimeFormatter.ofPattern("yyMMdd");

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final CartItemRepository cartRepo;
    private final StockMovementRepository movementRepo;
    private final CustomerService customerService;

    public OrderService(OrderRepository orderRepo, ProductRepository productRepo, CartItemRepository cartRepo,
                        StockMovementRepository movementRepo, CustomerService customerService) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.cartRepo = cartRepo;
        this.movementRepo = movementRepo;
        this.customerService = customerService;
    }

    public Page<Order> search(OrderStatus status, String keyword, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return orderRepo.search(status, kw, pageable);
    }

    public Order get(Integer id) {
        return orderRepo.findById(id).orElseThrow(() -> new NotFoundException("Order", id));
    }

    public List<Order> historyOf(Integer customerId) {
        return orderRepo.findByCustomerIdOrderByOrderDateDesc(customerId);
    }

    /**
     * Converts the customer's cart into an order.
     * Steps: lock + check stock of every item, reduce stock, log movement, save order, empty cart.
     * Everything runs in one transaction - if any item is short the whole thing is rolled back.
     */
    @Transactional
    public Order placeOrder(Integer customerId, PaymentMode paymentMode, String shippingAddress) {
        Customer customer = customerService.get(customerId);
        List<CartItem> cart = cartRepo.findByCustomerIdOrderByAddedAtAsc(customerId);
        if (cart.isEmpty()) {
            throw new BusinessException("Your cart is empty");
        }
        if (shippingAddress == null || shippingAddress.isBlank()) {
            shippingAddress = customer.getAddress() + ", " + customer.getCity() + " " + customer.getPincode();
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentMode(paymentMode);
        order.setShippingAddress(shippingAddress.trim());
        order.setOrderNumber(nextOrderNumber());

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem line : cart) {
            Product p = productRepo.findByIdForUpdate(line.getProduct().getId())
                    .orElseThrow(() -> new NotFoundException("Product", line.getProduct().getId()));
            if (!p.isActive()) {
                throw new BusinessException(p.getName() + " is no longer sold. Remove it from the cart.");
            }
            if (line.getQuantity() > p.getStockQty()) {
                throw new BusinessException("Not enough stock for " + p.getName() + ": requested "
                        + line.getQuantity() + ", available " + p.getStockQty());
            }
            p.setStockQty(p.getStockQty() - line.getQuantity());

            OrderItem item = new OrderItem(p, line.getQuantity());
            order.addItem(item);
            total = total.add(item.getLineTotal());

            movementRepo.save(new StockMovement(p, -line.getQuantity(), MovementType.SALE, order.getOrderNumber()));
        }
        order.setTotalAmount(total);
        orderRepo.save(order);
        cartRepo.clearCart(customerId);
        return order;
    }

    /** only orders that have not been shipped can be cancelled; the stock goes back */
    @Transactional
    public Order cancelOrder(Integer orderId) {
        Order order = get(orderId);
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new BusinessException("Order " + order.getOrderNumber() + " is " + order.getStatus()
                    + " and can no longer be cancelled");
        }
        for (OrderItem item : order.getItems()) {
            Product p = item.getProduct();
            p.setStockQty(p.getStockQty() + item.getQuantity());
            movementRepo.save(new StockMovement(p, item.getQuantity(), MovementType.CANCEL, order.getOrderNumber()));
        }
        order.setStatus(OrderStatus.CANCELLED);
        return order;
    }

    /** PLACED -> SHIPPED -> DELIVERED, nothing else is allowed */
    @Transactional
    public Order advanceStatus(Integer orderId) {
        Order order = get(orderId);
        switch (order.getStatus()) {
            case PLACED -> order.setStatus(OrderStatus.SHIPPED);
            case SHIPPED -> {
                order.setStatus(OrderStatus.DELIVERED);
                order.setDeliveredOn(LocalDate.now());
            }
            default -> throw new BusinessException("Order " + order.getOrderNumber() + " is already "
                    + order.getStatus());
        }
        return order;
    }

    private String nextOrderNumber() {
        long next = orderRepo.count() + 1;
        return "ORD-" + LocalDate.now().format(NUM_FMT) + "-" + String.format("%03d", next);
    }
}
