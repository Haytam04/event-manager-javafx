package com.example.eventmanager.service;

import com.example.eventmanager.dao.CategoryDAO;
import com.example.eventmanager.entity.Category;
import java.util.List;

public class CategoryService {
    private CategoryDAO categoryDAO = new CategoryDAO();

    public void saveCategory(String name) {
        Category category = new Category();
        category.setName(name);
        categoryDAO.saveOrUpdate(category);
    }

    public void updateCategory(Category category, String newName) {
        category.setName(newName);
        categoryDAO.saveOrUpdate(category);
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public void deleteCategory(Category category) {
        categoryDAO.delete(category);
    }
}