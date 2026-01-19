package com.mekong.smart_service_booking.service;

import com.mekong.smart_service_booking.entity.Category;
import com.mekong.smart_service_booking.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Logic: Find all
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Logic: Create (Check if name exists first)
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category '" + category.getName() + "' already exists.");
        }
        return categoryRepository.save(category);
    }

    // Logic: Update
    public Category updateCategory(Long id, Category details) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        category.setName(details.getName());
        category.setDescription(details.getDescription());
        return categoryRepository.save(category);
    }

    // Logic: Delete
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Category not found.");
        }
        categoryRepository.deleteById(id);
    }
}