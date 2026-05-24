package com.learning.service.impl;

import com.learning.entity.Resource;
import com.learning.mapper.ResourceMapper;
import com.learning.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学习资源服务实现类
 * 负责处理学习资源的查询、创建、更新和删除等方法的具体实现
 * 引用文件：com.learning.entity.Resource, com.learning.mapper.ResourceMapper, com.learning.service.ResourceService
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    @Cacheable(value = "resources", key = "#unitId")
    public List<Resource> findByUnitId(Long unitId) {
        return resourceMapper.findByUnitId(unitId);
    }

    @Override
    @Cacheable(value = "resources", key = "#id")
    public Resource findById(Long id) {
        return resourceMapper.findById(id);
    }

    @Override
    @CacheEvict(value = "resources", allEntries = true)
    public void create(Resource resource) {
        resource.setStatus("ACTIVE");
        resourceMapper.insert(resource);
    }

    @Override
    @CacheEvict(value = "resources", key = "#resource.id")
    public void update(Resource resource) {
        resourceMapper.update(resource);
    }

    @Override
    @CacheEvict(value = "resources", key = "#id")
    public void delete(Long id) {
        resourceMapper.delete(id);
    }

    @Override
    @Cacheable(value = "resourcesByType", key = "#resourceType")
    public List<Resource> findByResourceType(String resourceType) {
        return resourceMapper.findByResourceType(resourceType);
    }
    
    @Override
    @Cacheable(value = "latestResources", key = "#limit")
    public List<Resource> findLatest(int limit) {
        return resourceMapper.findLatest(limit);
    }
}