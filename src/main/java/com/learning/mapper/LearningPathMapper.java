package com.learning.mapper;

import com.learning.entity.LearningPath;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 学习路线数据访问接口
 * 负责学习路线相关的数据库操作，包括查询、插入、更新和删除等
 */
@Mapper
public interface LearningPathMapper {
    List<LearningPath> findAll();

    LearningPath findById(@Param("id") Long id);

    void insert(LearningPath learningPath);

    void update(LearningPath learningPath);

    void delete(@Param("id") Long id);

    List<LearningPath> findByCategory(@Param("categoryId") Long categoryId);

    List<LearningPath> findBySubcategory(@Param("subcategoryId") Long subcategoryId);

    List<LearningPath> findByStatus(@Param("status") String status);

    List<LearningPath> findByCreatedBy(@Param("createdBy") Long createdBy);
    
    List<LearningPath> selectList(@Param("offset") int offset, @Param("limit") int limit, 
                                 @Param("keyword") String keyword, @Param("status") String status, 
                                 @Param("category") Long category);
    
    int count(@Param("keyword") String keyword, @Param("status") String status, 
             @Param("category") Long category);
    
    LearningPath selectById(@Param("id") Long id);
    
    List<LearningPath> searchByKeyword(@Param("keyword") String keyword);

    List<LearningPath> findHotPaths(@Param("limit") int limit);
    
    List<Map<String, Object>> findAllWithDetails();

    int countPublished();
}
