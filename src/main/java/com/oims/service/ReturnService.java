package com.oims.service;

import com.oims.exception.BusinessException;
import com.oims.exception.NotFoundException;
import com.oims.model.*;
import com.oims.repository.OrderItemRepository;
import com.oims.repository.ReturnRequestRepository;
import com.oims.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReturnService {

    public static final int RETURN_WINDOW_DAYS = 7;

    private final ReturnRequestRepository returnRepo;
    private final OrderItemRepository orderItemRepo;
    private final StockMovementRepository movementRepo;

    public ReturnService(ReturnRequestRepository returnRepo, OrderItemRepository orderItemRepo,
                         StockMovementRepository movementRepo) {
        this.returnRepo = returnRepo;
        this.orderItemRepo = orderItemRepo;
        this.movementRepo = movementRepo;
    }

    public List<ReturnRequest> findAll(ReturnStatus status) {
        return status == null ? returnRepo.findAllByOrderByRequestedOnDesc()
                              : returnRepo.findByStatusOrderByRequestedOnDesc(status);
    }

    public ReturnRequest get(Integer id) {
        return returnRepo.findById(id).orElseThrow(() -> new NotFoundException("Return request", id));
    }

    public OrderItem getOrderItem(Integer itemId) {
        return orderItemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Order item", itemId));
    }

    /** how many more units of this line can still be returned */
    public int returnableQty(OrderItem item) {
        return item.getQuantity() - returnRepo.sumActiveReturnQty(item.getId());
    }

    public void checkEligible(OrderItem item, LocalDate today) {
        Order order = item.getOrder();
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("Returns are allowed only after the order is delivered");
        }
        long days = ChronoUnit.DAYS.between(order.getDeliveredOn(), today);
        if (days > RETURN_WINDOW_DAYS) {
            throw new BusinessException("Return window of " + RETURN_WINDOW_DAYS + " days ended on "
                    + order.getDeliveredOn().plusDays(RETURN_WINDOW_DAYS));
        }
    }

    @Transactional
    public ReturnRequest requestReturn(Integer orderItemId, int qty, String reason) {
        OrderItem item = getOrderItem(orderItemId);
        checkEligible(item, LocalDate.now());
        int allowed = returnableQty(item);
        if (qty < 1 || qty > allowed) {
            throw new BusinessException("You can return between 1 and " + allowed + " unit(s) of this item");
        }
        if (reason == null || reason.trim().length() < 10) {
            throw new BusinessException("Please describe the reason in at least 10 characters");
        }
        ReturnRequest r = new ReturnRequest();
        r.setOrderItem(item);
        r.setQuantity(qty);
        r.setReason(reason.trim());
        r.setStatus(ReturnStatus.REQUESTED);
        r.setRequestedOn(LocalDateTime.now());
        return returnRepo.save(r);
    }

    /** approve: calculate refund, put the units back in stock and log it - all in one transaction */
    @Transactional
    public ReturnRequest approve(Integer returnId) {
        ReturnRequest r = getPending(returnId);
        OrderItem item = r.getOrderItem();
        BigDecimal refund = item.getUnitPrice().multiply(BigDecimal.valueOf(r.getQuantity()));

        Product p = item.getProduct();
        p.setStockQty(p.getStockQty() + r.getQuantity());
        movementRepo.save(new StockMovement(p, r.getQuantity(), MovementType.RETURN, "RET-" + r.getId()));

        r.setRefundAmount(refund);
        r.setStatus(ReturnStatus.APPROVED);
        r.setProcessedOn(LocalDateTime.now());
        return r;
    }

    @Transactional
    public ReturnRequest reject(Integer returnId) {
        ReturnRequest r = getPending(returnId);
        r.setStatus(ReturnStatus.REJECTED);
        r.setProcessedOn(LocalDateTime.now());
        return r;
    }

    private ReturnRequest getPending(Integer id) {
        ReturnRequest r = get(id);
        if (r.getStatus() != ReturnStatus.REQUESTED) {
            throw new BusinessException("Return #" + id + " was already " + r.getStatus());
        }
        return r;
    }
}
