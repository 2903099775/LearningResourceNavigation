package com.learning.mapper;

import com.learning.entity.LearningCategory;
import java.util.List;

public interface LearningCategoryMapper {
    int insert(LearningCategory category);
    int update(LearningCategory category);
    int deleteById(Long id);
    LearningCategory selectById(Long id);
    List<LearningCategory> selectAll();
    List<LearningCategory> selectByStatus(Integer status);
}