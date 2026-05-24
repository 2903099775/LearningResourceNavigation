package com.learning.service;

import com.learning.common.ResponseResult;

/**
 * 学习路线服务接口
 * 定义学习路线的查询、获取详情、获取阶段和进度等方法
 * 引用文件：c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\common\ResponseResult.java
 */
public interface LearningPathService {
    ResponseResult getPaths();
    
    ResponseResult getPathById(Long id);
    
    ResponseResult getPathStages(Long id);
    
    ResponseResult getPathProgress(Long id);
    
    ResponseResult searchPaths(String keyword);
    
    ResponseResult getHotPaths(int limit);
    
    ResponseResult getPathsByCategory(Long categoryId);

    ResponseResult getPathsBySubcategory(Long subcategoryId);
}