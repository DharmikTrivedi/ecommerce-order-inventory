package com.oims.controller;

import com.oims.exception.BusinessException;
import com.oims.model.OrderItem;
import com.oims.model.ReturnRequest;
import com.oims.model.ReturnStatus;
import com.oims.service.ReturnService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) ReturnStatus status, Model model) {
        model.addAttribute("returns", returnService.findAll(status));
        model.addAttribute("statuses", ReturnStatus.values());
        model.addAttribute("status", status);
        return "returns/list";
    }

    @GetMapping("/new")
    public String form(@RequestParam Integer itemId, Model model, RedirectAttributes ra) {
        OrderItem item = returnService.getOrderItem(itemId);
        try {
            returnService.checkEligible(item, LocalDate.now());
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders/" + item.getOrder().getId();
        }
        int maxQty = returnService.returnableQty(item);
        if (maxQty <= 0) {
            ra.addFlashAttribute("error", "All units of this item already have a return request");
            return "redirect:/orders/" + item.getOrder().getId();
        }
        model.addAttribute("item", item);
        model.addAttribute("maxQty", maxQty);
        return "returns/form";
    }

    @PostMapping
    public String submit(@RequestParam Integer itemId, @RequestParam int quantity, @RequestParam String reason,
                         Model model, RedirectAttributes ra) {
        try {
            ReturnRequest r = returnService.requestReturn(itemId, quantity, reason);
            ra.addFlashAttribute("success", "Return request #" + r.getId() + " submitted");
            return "redirect:/orders/" + r.getOrderItem().getOrder().getId();
        } catch (BusinessException e) {
            // show the form again with what the user typed
            OrderItem item = returnService.getOrderItem(itemId);
            model.addAttribute("item", item);
            model.addAttribute("maxQty", returnService.returnableQty(item));
            model.addAttribute("error", e.getMessage());
            model.addAttribute("quantity", quantity);
            model.addAttribute("reason", reason);
            return "returns/form";
        }
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            ReturnRequest r = returnService.approve(id);
            ra.addFlashAttribute("success", "Return #" + id + " approved, refund of Rs. " + r.getRefundAmount()
                    + " issued and stock updated");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/returns";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            returnService.reject(id);
            ra.addFlashAttribute("success", "Return #" + id + " rejected");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/returns";
    }
}
