package com.learning.service;

import com.learning.entity.LearningSubcategory;

import java.util.List;

public interface LearningSubcategoryService {
    LearningSubcategory getById(Long id);

    List<LearningSubcategory> getAll();

    List<LearningSubcategory> getActive();

    List<LearningSubcategory> getByCategoryId(Long categoryId);

    List<LearningSubcategory> getActiveByCategoryId(Long categoryId);

    int add(LearningSubcategory subcategory);

    int update(LearningSubcategory subcategory);

    int delete(Long id);

    void clearCache();
}
