package com.oims.controller;

import com.oims.model.Product;
import com.oims.service.CategoryService;
import com.oims.service.ProductService;
import com.oims.service.SellerService;
import com.oims.web.PageUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/products")
public class ProductController {

    private static final Set<String> SORT_FIELDS = Set.of("name", "sku", "price", "stockQty");

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SellerService sellerService;

    @Value("${app.page-size:8}")
    private int pageSize;

    public ProductController(ProductService productService, CategoryService categoryService,
                             SellerService sellerService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.sellerService = sellerService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) Integer category,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "name") String sort,
                       @RequestParam(defaultValue = "asc") String dir,
                       Model model) {
        model.addAttribute("page", productService.search(q, category, false,
                PageUtil.build(page, pageSize, sort, dir, SORT_FIELDS, "name")));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        return "products/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        Product p = new Product();
        p.setReorderLevel(5);
        p.setStockQty(0);
        model.addAttribute("product", p);
        return formView(model);
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("product", productService.get(id));
        return formView(model);
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("product") Product product, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (!result.hasFieldErrors("sku") && productService.isSkuTaken(product)) {
            result.rejectValue("sku", "duplicate", "SKU already exists");
        }
        if (result.hasErrors()) {
            return formView(model);
        }
        boolean isNew = product.getId() == null;
        productService.save(product);
        ra.addFlashAttribute("success", product.getName() + (isNew ? " added" : " updated"));
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        String name = productService.get(id).getName();
        if (productService.delete(id)) {
            ra.addFlashAttribute("success", name + " deleted");
        } else {
            ra.addFlashAttribute("info", name + " has order history, so it was marked inactive instead of deleted");
        }
        return "redirect:/products";
    }

    private String formView(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("sellers", sellerService.findAll());
        return "products/form";
    }
}
