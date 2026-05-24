package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.service.LearningPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学习路线控制器
 * 负责处理学习路线相关的API请求，包括获取学习路线列表、详情、阶段和进度等
 */
@RestController
@RequestMapping("/api/paths")
public class LearningPathController {

    @Autowired
    private LearningPathService learningPathService;

    @GetMapping
    public ResponseResult<Object> getPaths() {
        return learningPathService.getPaths();
    }

    @GetMapping("/{id}")
    public ResponseResult<Object> getPathById(@PathVariable Long id) {
        return learningPathService.getPathById(id);
    }

    @GetMapping("/{id}/stages")
    public ResponseResult<Object> getPathStages(@PathVariable Long id) {
        return learningPathService.getPathStages(id);
    }

    @GetMapping("/{id}/progress")
    public ResponseResult<Object> getPathProgress(@PathVariable Long id) {
        return learningPathService.getPathProgress(id);
    }
    
    @GetMapping("/search")
    public ResponseResult<Object> searchPaths(@RequestParam String keyword) {
        return learningPathService.searchPaths(keyword);
    }
    
    @GetMapping("/hot")
    public ResponseResult<Object> getHotPaths(@RequestParam(defaultValue = "5") int limit) {
        return learningPathService.getHotPaths(limit);
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseResult<Object> getPathsByCategory(@PathVariable Long categoryId) {
        return learningPathService.getPathsByCategory(categoryId);
    }

    @GetMapping("/subcategory/{subcategoryId}")
    public ResponseResult<Object> getPathsBySubcategory(@PathVariable Long subcategoryId) {
        return learningPathService.getPathsBySubcategory(subcategoryId);
    }
}