package com.learning.service.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningCategory;

public interface AdminCategoryService {
    ResponseResult listAll();
    ResponseResult getById(Long id);
    ResponseResult create(LearningCategory category);
    ResponseResult update(LearningCategory category);
    ResponseResult delete(Long id);
}
