package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.service.LearningCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学习资源类别控制器
 * 处理学习资源类别的查询、添加、修改、删除等请求
 */
@RestController
@RequestMapping("/api/categories")
public class LearningCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(LearningCategoryController.class);

    @Autowired
    private LearningCategoryService learningCategoryService;

    /**
     * 获取所有学习资源类别
     */
    @GetMapping("/all")
    public ResponseResult getAllCategories() {
        try {
            logger.debug("获取所有学习资源类别");
            return ResponseResult.success(learningCategoryService.getAllCategories());
        } catch (Exception e) {
            logger.error("获取所有学习资源类别失败", e);
            return ResponseResult.error("获取所有学习资源类别失败");
        }
    }

    /**
     * 获取所有激活的学习资源类别
     */
    @GetMapping("/active")
    public ResponseResult getActiveCategories() {
        try {
            logger.debug("获取所有激活的学习资源类别");
            return ResponseResult.success(learningCategoryService.getActiveCategories());
        } catch (Exception e) {
            logger.error("获取所有激活的学习资源类别失败", e);
            return ResponseResult.error("获取所有激活的学习资源类别失败");
        }
    }

    /**
     * 根据ID获取学习资源类别
     */
    @GetMapping("/{id}")
    public ResponseResult getCategoryById(@PathVariable Long id) {
        try {
            logger.debug("根据ID获取学习资源类别，ID: {}", id);
            return ResponseResult.success(learningCategoryService.getCategoryById(id));
        } catch (Exception e) {
            logger.error("根据ID获取学习资源类别失败，ID: {}", id, e);
            return ResponseResult.error("根据ID获取学习资源类别失败");
        }
    }
}