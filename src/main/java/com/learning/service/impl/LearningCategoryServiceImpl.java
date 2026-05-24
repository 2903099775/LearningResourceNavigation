package com.learning.service.impl;

import com.learning.entity.LearningCategory;
import com.learning.mapper.LearningCategoryMapper;
import com.learning.service.LearningCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningCategoryServiceImpl implements LearningCategoryService {

    @Autowired
    private LearningCategoryMapper learningCategoryMapper;

    @Override
    public int addCategory(LearningCategory category) {
        return learningCategoryMapper.insert(category);
    }

    @Override
    public int updateCategory(LearningCategory category) {
        return learningCategoryMapper.update(category);
    }

    @Override
    public int deleteCategory(Long id) {
        return learningCategoryMapper.deleteById(id);
    }

    @Override
    public LearningCategory getCategoryById(Long id) {
        return learningCategoryMapper.selectById(id);
    }

    @Override
    public List<LearningCategory> getAllCategories() {
        return learningCategoryMapper.selectAll();
    }

    @Override
    public List<LearningCategory> getActiveCategories() {
        return learningCategoryMapper.selectByStatus(1);
    }
}