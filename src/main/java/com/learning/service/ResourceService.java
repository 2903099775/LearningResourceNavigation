package com.learning.service;

import com.learning.entity.Resource;

import java.util.List;

/**
 * 学习资源服务接口
 * 定义学习资源的查询、创建、更新和删除等方法
 * 引用文件：com.learning.entity.Resource
 */
public interface ResourceService {
    List<Resource> findByUnitId(Long unitId);
    
    Resource findById(Long id);
    
    void create(Resource resource);
    
    void update(Resource resource);
    
    void delete(Long id);
    
    List<Resource> findByResourceType(String resourceType);
    
    List<Resource> findLatest(int limit);
}