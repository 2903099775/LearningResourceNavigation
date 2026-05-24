package com.learning.service.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningUnit;

/**
 * 管理员学习单元服务接口
 * 定义管理员对学习单元的创建、更新、删除和查询等操作方法
 */
public interface AdminUnitService {
    
    ResponseResult getUnitList(Integer page, Integer size, String keyword, String type, String platform);
    
    ResponseResult getUnitById(Long id);
    
    ResponseResult createUnit(LearningUnit unit);
    
    ResponseResult updateUnit(LearningUnit unit);
    
    ResponseResult deleteUnit(Long id);
}
