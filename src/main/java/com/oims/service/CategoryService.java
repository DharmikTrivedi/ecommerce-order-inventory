package com.oims.service;

import com.oims.exception.BusinessException;
import com.oims.exception.NotFoundException;
import com.oims.model.Category;
import com.oims.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepo;

    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public List<Category> findAll() {
        return categoryRepo.findAllByOrderByNameAsc();
    }

    public List<Object[]> findAllWithProductCount() {
        return categoryRepo.findAllWithProductCount();
    }

    public Category get(Integer id) {
        return categoryRepo.findById(id).orElseThrow(() -> new NotFoundException("Category", id));
    }

    public boolean isNameTaken(Category c) {
        if (c.getId() == null) {
            return categoryRepo.existsByNameIgnoreCase(c.getName());
        }
        return categoryRepo.existsByNameIgnoreCaseAndIdNot(c.getName(), c.getId());
    }

    @Transactional
    public Category save(Category c) {
        if (isNameTaken(c)) {
            throw new BusinessException("A category named '" + c.getName() + "' already exists");
        }
        return categoryRepo.save(c);
    }

    @Transactional
    public void delete(Integer id) {
        Category c = get(id);
        if (!c.getProducts().isEmpty()) {
            throw new BusinessException("Category '" + c.getName() + "' still has "
                    + c.getProducts().size() + " product(s). Move or delete them first.");
        }
        categoryRepo.delete(c);
    }
}
