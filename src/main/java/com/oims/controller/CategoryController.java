package com.oims.controller;

import com.oims.exception.BusinessException;
import com.oims.model.Category;
import com.oims.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rows", categoryService.findAllWithProductCount());
        return "categories/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("category", categoryService.get(id));
        return "categories/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("category") Category category, BindingResult result,
                       RedirectAttributes ra) {
        if (!result.hasFieldErrors("name") && categoryService.isNameTaken(category)) {
            result.rejectValue("name", "duplicate", "This category already exists");
        }
        if (result.hasErrors()) {
            return "categories/form";
        }
        boolean isNew = category.getId() == null;
        categoryService.save(category);
        ra.addFlashAttribute("success", "Category '" + category.getName() + "' " + (isNew ? "added" : "updated"));
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            categoryService.delete(id);
            ra.addFlashAttribute("success", "Category deleted");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }
}
