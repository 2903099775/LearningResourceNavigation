package com.learning.service;

import com.learning.entity.LearningCategory;
import java.util.List;

public interface LearningCategoryService {
    int addCategory(LearningCategory category);
    int updateCategory(LearningCategory category);
    int deleteCategory(Long id);
    LearningCategory getCategoryById(Long id);
    List<LearningCategory> getAllCategories();
    List<LearningCategory> getActiveCategories();
}