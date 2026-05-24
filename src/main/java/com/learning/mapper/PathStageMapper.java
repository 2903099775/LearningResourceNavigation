package com.learning.mapper;

import com.learning.entity.PathStage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学习路线阶段数据访问接口
 * 负责学习路线阶段相关的数据库操作，包括查询、插入、更新和删除等
 * 引用文件：com.learning.entity.PathStage
 */
@Mapper
public interface PathStageMapper {
    List<PathStage> findByPathId(@Param("pathId") Long pathId);

    PathStage findById(@Param("id") Long id);

    void insert(PathStage pathStage);

    void update(PathStage pathStage);

    void delete(@Param("id") Long id);

    void deleteByPathId(@Param("pathId") Long pathId);
}