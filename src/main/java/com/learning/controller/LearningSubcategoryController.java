package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.service.LearningSubcategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学习子域控制器
 * 对应5层层次结构的第二层：领域→子域→路线→阶段→单元
 */
@RestController
@RequestMapping("/api/subcategories")
public class LearningSubcategoryController {

    private static final Logger logger = LoggerFactory.getLogger(LearningSubcategoryController.class);

    @Autowired
    private LearningSubcategoryService learningSubcategoryService;

    /**
     * 获取所有子分类
     */
    @GetMapping("/all")
    public ResponseResult getAll() {
        try {
            return ResponseResult.success(learningSubcategoryService.getAll());
        } catch (Exception e) {
            logger.error("获取子分类列表失败", e);
            return ResponseResult.error("获取子分类列表失败");
        }
    }

    /**
     * 获取所有激活的子分类
     */
    @GetMapping("/active")
    public ResponseResult getActive() {
        try {
            return ResponseResult.success(learningSubcategoryService.getActive());
        } catch (Exception e) {
            logger.error("获取激活子分类失败", e);
            return ResponseResult.error("获取激活子分类失败");
        }
    }

    /**
     * 根据ID获取子分类
     */
    @GetMapping("/{id}")
    public ResponseResult getById(@PathVariable Long id) {
        try {
            return ResponseResult.success(learningSubcategoryService.getById(id));
        } catch (Exception e) {
            logger.error("获取子分类失败，ID: {}", id, e);
            return ResponseResult.error("获取子分类失败");
        }
    }

    /**
     * 根据学习领域ID获取子分类列表
     */
    @GetMapping("/category/{categoryId}")
    public ResponseResult getByCategoryId(@PathVariable Long categoryId) {
        try {
            return ResponseResult.success(learningSubcategoryService.getActiveByCategoryId(categoryId));
        } catch (Exception e) {
            logger.error("根据领域获取子分类失败，categoryId: {}", categoryId, e);
            return ResponseResult.error("根据领域获取子分类失败");
        }
    }
}
