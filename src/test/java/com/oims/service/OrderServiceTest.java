package com.oims.service;

import com.oims.TestData;
import com.oims.exception.BusinessException;
import com.oims.model.*;
import com.oims.repository.CartItemRepository;
import com.oims.repository.OrderRepository;
import com.oims.repository.ProductRepository;
import com.oims.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepo;
    @Mock ProductRepository productRepo;
    @Mock CartItemRepository cartRepo;
    @Mock StockMovementRepository movementRepo;
    @Mock CustomerService customerService;
    @InjectMocks OrderService orderService;

    Customer customer;
    Product kurta;

    @BeforeEach
    void setUp() {
        customer = TestData.customer("Priya", "p@test.com");
        customer.setId(2);
        kurta = TestData.product("FS-4001", "Cotton Kurta", "899.00", 5, 2, null, null);
        kurta.setId(15);
    }

    @Test
    void placeOrder_reducesStockAndCalculatesTotal() {
        when(customerService.get(2)).thenReturn(customer);
        when(cartRepo.findByCustomerIdOrderByAddedAtAsc(2)).thenReturn(List.of(new CartItem(customer, kurta, 2)));
        when(productRepo.findByIdForUpdate(15)).thenReturn(Optional.of(kurta));
        when(orderRepo.count()).thenReturn(18L);

        Order o = orderService.placeOrder(2, PaymentMode.UPI, "45 Marine Drive, Mumbai");

        assertThat(kurta.getStockQty()).isEqualTo(3);
        assertThat(o.getTotalAmount()).isEqualByComparingTo("1798.00");
        assertThat(o.getStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(o.getOrderNumber()).endsWith("-019");
        verify(cartRepo).clearCart(2);
        verify(movementRepo).save(any(StockMovement.class));
    }

    @Test
    void placeOrder_notEnoughStock_throwsAndSavesNothing() {
        when(customerService.get(2)).thenReturn(customer);
        when(cartRepo.findByCustomerIdOrderByAddedAtAsc(2)).thenReturn(List.of(new CartItem(customer, kurta, 6)));
        when(productRepo.findByIdForUpdate(15)).thenReturn(Optional.of(kurta));

        assertThatThrownBy(() -> orderService.placeOrder(2, PaymentMode.COD, "45 Marine Drive, Mumbai"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Not enough stock");
        verify(orderRepo, never()).save(any());
        verify(cartRepo, never()).clearCart(any());
    }

    @Test
    void placeOrder_emptyCart_isRejected() {
        when(customerService.get(2)).thenReturn(customer);
        when(cartRepo.findByCustomerIdOrderByAddedAtAsc(2)).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.placeOrder(2, PaymentMode.UPI, "x"))
                .hasMessage("Your cart is empty");
    }

    @Test
    void cancel_placedOrder_restoresStock() {
        Order o = orderWith(OrderStatus.PLACED, 2);
        when(orderRepo.findById(1)).thenReturn(Optional.of(o));

        orderService.cancelOrder(1);

        assertThat(o.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(kurta.getStockQty()).isEqualTo(7);
    }

    @Test
    void cancel_shippedOrder_isNotAllowed() {
        Order o = orderWith(OrderStatus.SHIPPED, 2);
        when(orderRepo.findById(1)).thenReturn(Optional.of(o));

        assertThatThrownBy(() -> orderService.cancelOrder(1))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("can no longer be cancelled");
        assertThat(kurta.getStockQty()).isEqualTo(5);
    }

    @Test
    void status_followsPlacedShippedDelivered() {
        Order o = orderWith(OrderStatus.PLACED, 1);
        when(orderRepo.findById(1)).thenReturn(Optional.of(o));

        orderService.advanceStatus(1);
        assertThat(o.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        orderService.advanceStatus(1);
        assertThat(o.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(o.getDeliveredOn()).isNotNull();

        assertThatThrownBy(() -> orderService.advanceStatus(1)).isInstanceOf(BusinessException.class);
    }

    private Order orderWith(OrderStatus status, int qty) {
        Order o = new Order();
        o.setId(1);
        o.setOrderNumber("ORD-TEST-001");
        o.setStatus(status);
        o.addItem(new OrderItem(kurta, qty));
        return o;
    }
}
