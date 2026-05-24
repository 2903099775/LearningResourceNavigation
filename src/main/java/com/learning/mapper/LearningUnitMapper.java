package com.learning.mapper;

import com.learning.entity.LearningUnit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 学习单元数据访问接口
 * 负责学习单元相关的数据库操作，包括CRUD操作、高级查询和统计等
 * 引用文件：c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\LearningUnit.java
 */
@Mapper
public interface LearningUnitMapper {
    // 基本CRUD操作
    List<LearningUnit> findByStageId(@Param("stageId") Long stageId);
    List<LearningUnit> findByPathId(@Param("pathId") Long pathId);
    LearningUnit findById(@Param("id") Long id);
    void insert(LearningUnit learningUnit);
    void update(LearningUnit learningUnit);
    void delete(@Param("id") Long id);
    void deleteByStageId(@Param("stageId") Long stageId);
    void deleteByPathId(@Param("pathId") Long pathId);
    
    // 高级查询
    List<LearningUnit> findByType(@Param("type") String type);
    List<LearningUnit> findByPlatform(@Param("platform") String platform);
    List<LearningUnit> findVipOnly();
    List<LearningUnit> searchByTitle(@Param("keyword") String keyword);
    
    // 统计相关
    Integer countByStageId(@Param("stageId") Long stageId);
    Integer countByPathId(@Param("pathId") Long pathId);
    Integer countTotal();
    
    // 其他操作
    void incrementViewCount(@Param("id") Long id);
    
    // 分页查询（管理端）
    List<LearningUnit> selectList(@Param("offset") Integer offset, 
                                  @Param("size") Integer size,
                                  @Param("keyword") String keyword,
                                  @Param("type") String type,
                                  @Param("platform") String platform);
    
    Integer countWithFilters(@Param("keyword") String keyword,
                             @Param("type") String type,
                             @Param("platform") String platform);
    
    List<LearningUnit> findAll();
    List<Map<String, Object>> findAllWithDetails();
}
