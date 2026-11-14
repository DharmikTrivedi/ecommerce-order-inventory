package com.oims.controller;

import com.oims.exception.BusinessException;
import com.oims.model.Order;
import com.oims.model.OrderItem;
import com.oims.model.OrderStatus;
import com.oims.service.OrderService;
import com.oims.service.ReturnService;
import com.oims.web.PageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private static final Set<String> SORT_FIELDS = Set.of("orderDate", "orderNumber", "totalAmount", "status");

    private final OrderService orderService;
    private final ReturnService returnService;

    @Value("${app.page-size:8}")
    private int pageSize;

    public OrderController(OrderService orderService, ReturnService returnService) {
        this.orderService = orderService;
        this.returnService = returnService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) OrderStatus status,
                       @RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "orderDate") String sort,
                       @RequestParam(defaultValue = "desc") String dir,
                       Model model) {
        model.addAttribute("page", orderService.search(status, q,
                PageUtil.build(page, pageSize, sort, dir, SORT_FIELDS, "orderDate")));
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("status", status);
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Integer id, Model model) {
        Order order = orderService.get(id);
        // which lines can still be returned (used to show/hide the Return button)
        Map<Integer, Integer> returnable = new HashMap<>();
        if (order.getStatus() == OrderStatus.DELIVERED) {
            for (OrderItem item : order.getItems()) {
                try {
                    returnService.checkEligible(item, LocalDate.now());
                    returnable.put(item.getId(), returnService.returnableQty(item));
                } catch (BusinessException ignored) {
                    // outside return window
                }
            }
        }
        model.addAttribute("order", order);
        model.addAttribute("returnable", returnable);
        return "orders/view";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            Order o = orderService.cancelOrder(id);
            ra.addFlashAttribute("success", "Order " + o.getOrderNumber() + " cancelled and stock restored");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/advance")
    public String advance(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            Order o = orderService.advanceStatus(id);
            ra.addFlashAttribute("success", "Order " + o.getOrderNumber() + " marked " + o.getStatus());
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }
}
