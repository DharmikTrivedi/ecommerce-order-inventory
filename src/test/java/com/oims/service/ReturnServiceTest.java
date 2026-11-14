package com.oims.service;

import com.oims.TestData;
import com.oims.exception.BusinessException;
import com.oims.model.*;
import com.oims.repository.OrderItemRepository;
import com.oims.repository.ReturnRequestRepository;
import com.oims.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

/** business rule: returns only for delivered orders, within 7 days, up to the quantity bought */
@ExtendWith(MockitoExtension.class)
class ReturnServiceTest {

    @Mock ReturnRequestRepository returnRepo;
    @Mock OrderItemRepository orderItemRepo;
    @Mock StockMovementRepository movementRepo;
    @InjectMocks ReturnService returnService;

    Product book;
    OrderItem item;
    Order order;

    @BeforeEach
    void setUp() {
        book = TestData.product("BK-3003", "Spring in Action", "999.00", 6, 4, null, null);
        order = new Order();
        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveredOn(LocalDate.of(2026, 11, 10));
        item = new OrderItem(book, 2);
        item.setId(26);
        order.addItem(item);
    }

    @Test
    void returnWithinSevenDays_isAllowed() {
        assertThatCode(() -> returnService.checkEligible(item, LocalDate.of(2026, 11, 17))).doesNotThrowAnyException();
    }

    @Test
    void returnAfterSevenDays_isRejected() {
        assertThatThrownBy(() -> returnService.checkEligible(item, LocalDate.of(2026, 11, 18)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Return window");
    }

    @Test
    void returnBeforeDelivery_isRejected() {
        order.setStatus(OrderStatus.SHIPPED);
        assertThatThrownBy(() -> returnService.checkEligible(item, LocalDate.of(2026, 11, 11)))
                .hasMessageContaining("only after the order is delivered");
    }

    @Test
    void returnableQty_subtractsEarlierRequests() {
        when(returnRepo.sumActiveReturnQty(26)).thenReturn(1);
        assertThat(returnService.returnableQty(item)).isEqualTo(1);
    }

    @Test
    void approve_refundsAndRestocks() {
        ReturnRequest r = new ReturnRequest();
        r.setId(3);
        r.setOrderItem(item);
        r.setQuantity(2);
        r.setStatus(ReturnStatus.REQUESTED);
        when(returnRepo.findById(3)).thenReturn(Optional.of(r));

        returnService.approve(3);

        assertThat(r.getStatus()).isEqualTo(ReturnStatus.APPROVED);
        assertThat(r.getRefundAmount()).isEqualByComparingTo("1998.00");
        assertThat(book.getStockQty()).isEqualTo(8);
    }
}
