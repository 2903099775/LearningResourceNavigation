package com.learning.service.impl.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningSubcategory;
import com.learning.mapper.LearningSubcategoryMapper;
import com.learning.service.admin.AdminSubcategoryService;
import com.learning.service.LearningSubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminSubcategoryServiceImpl implements AdminSubcategoryService {

    @Autowired
    private LearningSubcategoryMapper subcategoryMapper;

    @Autowired
    private LearningSubcategoryService learningSubcategoryService;

    @Override
    public ResponseResult listAll() {
        List<LearningSubcategory> list = subcategoryMapper.selectAll();
        return ResponseResult.success(list);
    }

    @Override
    public ResponseResult listByCategory(Long categoryId) {
        List<LearningSubcategory> list = subcategoryMapper.selectByCategoryId(categoryId);
        return ResponseResult.success(list);
    }

    @Override
    public ResponseResult getById(Long id) {
        LearningSubcategory sub = subcategoryMapper.selectById(id);
        if (sub == null) {
            return ResponseResult.error(404, "子分类不存在");
        }
        return ResponseResult.success(sub);
    }

    @Override
    public ResponseResult create(LearningSubcategory subcategory) {
        if (subcategory.getName() == null || subcategory.getName().trim().isEmpty()) {
            return ResponseResult.error("子分类名称不能为空");
        }
        if (subcategory.getCategoryId() == null) {
            return ResponseResult.error("所属学习领域不能为空");
        }
        subcategory.setCreatedAt(LocalDateTime.now());
        subcategory.setUpdatedAt(LocalDateTime.now());
        if (subcategory.getStatus() == null) subcategory.setStatus(1);
        if (subcategory.getSortOrder() == null) subcategory.setSortOrder(0);
        subcategoryMapper.insert(subcategory);
        learningSubcategoryService.clearCache();
        return ResponseResult.success("创建成功", subcategory);
    }

    @Override
    public ResponseResult update(LearningSubcategory subcategory) {
        if (subcategory.getId() == null) {
            return ResponseResult.error("子分类ID不能为空");
        }
        LearningSubcategory existing = subcategoryMapper.selectById(subcategory.getId());
        if (existing == null) {
            return ResponseResult.error(404, "子分类不存在");
        }
        subcategory.setUpdatedAt(LocalDateTime.now());
        subcategoryMapper.update(subcategory);
        learningSubcategoryService.clearCache();
        return ResponseResult.success("更新成功", subcategoryMapper.selectById(subcategory.getId()));
    }

    @Override
    public ResponseResult delete(Long id) {
        LearningSubcategory existing = subcategoryMapper.selectById(id);
        if (existing == null) {
            return ResponseResult.error(404, "子分类不存在");
        }
        subcategoryMapper.deleteById(id);
        learningSubcategoryService.clearCache();
        return ResponseResult.success("删除成功");
    }
}
