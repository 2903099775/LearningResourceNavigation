package com.learning.service.impl.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningCategory;
import com.learning.mapper.LearningCategoryMapper;
import com.learning.service.admin.AdminCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {

    @Autowired
    private LearningCategoryMapper categoryMapper;

    @Override
    public ResponseResult listAll() {
        List<LearningCategory> list = categoryMapper.selectAll();
        return ResponseResult.success(list);
    }

    @Override
    public ResponseResult getById(Long id) {
        LearningCategory category = categoryMapper.selectById(id);
        if (category == null) {
            return ResponseResult.error(404, "分类不存在");
        }
        return ResponseResult.success(category);
    }

    @Override
    public ResponseResult create(LearningCategory category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return ResponseResult.error("分类名称不能为空");
        }
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        if (category.getStatus() == null) category.setStatus(1);
        if (category.getSortOrder() == null) category.setSortOrder(0);
        categoryMapper.insert(category);
        return ResponseResult.success("创建成功", category);
    }

    @Override
    public ResponseResult update(LearningCategory category) {
        if (category.getId() == null) {
            return ResponseResult.error("分类ID不能为空");
        }
        LearningCategory existing = categoryMapper.selectById(category.getId());
        if (existing == null) {
            return ResponseResult.error(404, "分类不存在");
        }
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.update(category);
        return ResponseResult.success("更新成功", categoryMapper.selectById(category.getId()));
    }

    @Override
    public ResponseResult delete(Long id) {
        LearningCategory existing = categoryMapper.selectById(id);
        if (existing == null) {
            return ResponseResult.error(404, "分类不存在");
        }
        categoryMapper.deleteById(id);
        return ResponseResult.success("删除成功");
    }
}
