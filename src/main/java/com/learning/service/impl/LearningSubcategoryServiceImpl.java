package com.learning.service.impl;

import com.learning.entity.LearningSubcategory;
import com.learning.mapper.LearningSubcategoryMapper;
import com.learning.service.LearningSubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningSubcategoryServiceImpl implements LearningSubcategoryService {

    @Autowired
    private LearningSubcategoryMapper learningSubcategoryMapper;

    @Override
    @Cacheable(value = "subcategories", key = "#id")
    public LearningSubcategory getById(Long id) {
        return learningSubcategoryMapper.selectById(id);
    }

    @Override
    @Cacheable(value = "subcategories", key = "'all'")
    public List<LearningSubcategory> getAll() {
        return learningSubcategoryMapper.selectAll();
    }

    @Override
    public List<LearningSubcategory> getActive() {
        return learningSubcategoryMapper.selectByStatus(1);
    }

    @Override
    public List<LearningSubcategory> getByCategoryId(Long categoryId) {
        return learningSubcategoryMapper.selectByCategoryId(categoryId);
    }

    @Override
    @Cacheable(value = "subcategories", key = "'category_' + #categoryId")
    public List<LearningSubcategory> getActiveByCategoryId(Long categoryId) {
        return learningSubcategoryMapper.selectActiveByCategoryId(categoryId);
    }

    @Override
    public int add(LearningSubcategory subcategory) {
        return learningSubcategoryMapper.insert(subcategory);
    }

    @Override
    public int update(LearningSubcategory subcategory) {
        return learningSubcategoryMapper.update(subcategory);
    }

    @Override
    public int delete(Long id) {
        return learningSubcategoryMapper.deleteById(id);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "subcategories", key = "'all'"),
        @CacheEvict(value = "subcategories", key = "'active'"),
        @CacheEvict(value = "subcategories", allEntries = true)
    })
    public void clearCache() {
    }
}
