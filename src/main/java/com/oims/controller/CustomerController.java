package com.oims.controller;

import com.oims.exception.BusinessException;
import com.oims.model.Customer;
import com.oims.service.CustomerService;
import com.oims.service.OrderService;
import com.oims.web.PageUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private static final Set<String> SORT_FIELDS = Set.of("firstName", "email", "city", "registeredOn");

    private final CustomerService customerService;
    private final OrderService orderService;

    @Value("${app.page-size:8}")
    private int pageSize;

    public CustomerController(CustomerService customerService, OrderService orderService) {
        this.customerService = customerService;
        this.orderService = orderService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String city,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "firstName") String sort,
                       @RequestParam(defaultValue = "asc") String dir,
                       Model model) {
        Page<Customer> result = customerService.search(q, city,
                PageUtil.build(page, pageSize, sort, dir, SORT_FIELDS, "firstName"));
        model.addAttribute("page", result);
        model.addAttribute("cities", customerService.cities());
        model.addAttribute("q", q);
        model.addAttribute("city", city);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        return "customers/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Integer id, Model model) {
        model.addAttribute("customer", customerService.get(id));
        model.addAttribute("orders", orderService.historyOf(id));
        return "customers/view";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customers/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("customer", customerService.get(id));
        return "customers/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("customer") Customer customer, BindingResult result,
                       RedirectAttributes ra) {
        if (!result.hasFieldErrors("email") && customerService.isEmailTaken(customer)) {
            result.rejectValue("email", "duplicate", "This email is already registered");
        }
        if (result.hasErrors()) {
            return "customers/form";
        }
        boolean isNew = customer.getId() == null;
        Customer saved = customerService.save(customer);
        ra.addFlashAttribute("success", "Customer " + saved.getFullName() + (isNew ? " registered" : " updated"));
        return "redirect:/customers/" + saved.getId();
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            customerService.delete(id);
            ra.addFlashAttribute("success", "Customer deleted");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customers";
    }
}
