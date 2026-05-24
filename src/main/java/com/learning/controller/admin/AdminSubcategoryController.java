package com.learning.controller.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningSubcategory;
import com.learning.service.admin.AdminSubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/subcategories")
public class AdminSubcategoryController {

    @Autowired
    private AdminSubcategoryService adminSubcategoryService;

    @GetMapping
    public ResponseResult list(@RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return adminSubcategoryService.listByCategory(categoryId);
        }
        return adminSubcategoryService.listAll();
    }

    @GetMapping("/{id}")
    public ResponseResult getById(@PathVariable Long id) {
        return adminSubcategoryService.getById(id);
    }

    @PostMapping
    public ResponseResult create(@RequestBody LearningSubcategory subcategory) {
        return adminSubcategoryService.create(subcategory);
    }

    @PutMapping("/{id}")
    public ResponseResult update(@PathVariable Long id, @RequestBody LearningSubcategory subcategory) {
        subcategory.setId(id);
        return adminSubcategoryService.update(subcategory);
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id) {
        return adminSubcategoryService.delete(id);
    }
}
