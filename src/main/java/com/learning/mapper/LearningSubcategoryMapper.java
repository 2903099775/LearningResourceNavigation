package com.learning.mapper;

import com.learning.entity.LearningSubcategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LearningSubcategoryMapper {
    LearningSubcategory selectById(Long id);

    List<LearningSubcategory> selectAll();

    List<LearningSubcategory> selectByStatus(@Param("status") Integer status);

    List<LearningSubcategory> selectByCategoryId(@Param("categoryId") Long categoryId);

    List<LearningSubcategory> selectActiveByCategoryId(@Param("categoryId") Long categoryId);

    int insert(LearningSubcategory subcategory);

    int update(LearningSubcategory subcategory);

    int deleteById(Long id);
}
