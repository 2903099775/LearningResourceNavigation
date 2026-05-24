package com.learning.service.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningSubcategory;

public interface AdminSubcategoryService {
    ResponseResult listAll();
    ResponseResult listByCategory(Long categoryId);
    ResponseResult getById(Long id);
    ResponseResult create(LearningSubcategory subcategory);
    ResponseResult update(LearningSubcategory subcategory);
    ResponseResult delete(Long id);
}
