package com.learning.mapper;

import com.learning.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学习资源数据访问接口
 * 负责学习资源相关的数据库操作，包括查询、插入、更新和删除等
 * 引用文件：com.learning.entity.Resource
 */
@Mapper
public interface ResourceMapper {
    List<Resource> findByUnitId(@Param("unitId") Long unitId);
    
    Resource findById(@Param("id") Long id);
    
    void insert(Resource resource);
    
    void update(Resource resource);
    
    void delete(@Param("id") Long id);
    
    void deleteByUnitId(@Param("unitId") Long unitId);
    
    List<Resource> findByResourceType(@Param("resourceType") String resourceType);
    
    List<Resource> findLatest(@Param("limit") int limit);
}