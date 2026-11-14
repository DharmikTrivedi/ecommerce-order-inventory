package com.oims.controller;

import com.oims.exception.BusinessException;
import com.oims.model.Seller;
import com.oims.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("sellers", sellerService.search(q));
        model.addAttribute("q", q);
        return "sellers/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("seller", new Seller());
        return "sellers/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("seller", sellerService.get(id));
        return "sellers/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("seller") Seller seller, BindingResult result, RedirectAttributes ra) {
        if (!result.hasFieldErrors("email") && sellerService.isEmailTaken(seller)) {
            result.rejectValue("email", "duplicate", "Another seller already uses this email");
        }
        if (result.hasErrors()) {
            return "sellers/form";
        }
        boolean isNew = seller.getId() == null;
        sellerService.save(seller);
        ra.addFlashAttribute("success", "Seller '" + seller.getName() + "' " + (isNew ? "added" : "updated"));
        return "redirect:/sellers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            sellerService.delete(id);
            ra.addFlashAttribute("success", "Seller deleted");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sellers";
    }
}
