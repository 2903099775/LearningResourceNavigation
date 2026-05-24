package com.learning.service.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningPath;

/**
 * 管理员学习路线服务接口
 * 定义管理员对学习路线的创建、更新、删除和查询等操作方法
 */
public interface AdminPathService {
    ResponseResult createPath(LearningPath path);
    
    ResponseResult updatePath(LearningPath path);
    
    ResponseResult deletePath(Long id);
    
    ResponseResult getPathList(Integer page, Integer size, String keyword, String status, String category);
    
    ResponseResult getPathById(Long id);
}