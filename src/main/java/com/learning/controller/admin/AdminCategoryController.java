package com.learning.controller.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningCategory;
import com.learning.service.admin.AdminCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService adminCategoryService;

    @GetMapping
    public ResponseResult list() {
        return adminCategoryService.listAll();
    }

    @GetMapping("/{id}")
    public ResponseResult getById(@PathVariable Long id) {
        return adminCategoryService.getById(id);
    }

    @PostMapping
    public ResponseResult create(@RequestBody LearningCategory category) {
        return adminCategoryService.create(category);
    }

    @PutMapping("/{id}")
    public ResponseResult update(@PathVariable Long id, @RequestBody LearningCategory category) {
        category.setId(id);
        return adminCategoryService.update(category);
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id) {
        return adminCategoryService.delete(id);
    }
}
