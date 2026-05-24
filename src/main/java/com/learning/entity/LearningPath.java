package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习路线实体类
 * 表示系统中的学习路线，包含路线的标题、描述、难度、时长等属性
 */
@Data
public class LearningPath {
    private Long id;
    private String title;
    private Long categoryId;
    private LearningCategory category;
    private Long subcategoryId;
    private LearningSubcategory subcategory;
    private Long unitId;
    private String description;
    private String coverImage;
    private String difficulty;
    private Integer durationWeeks;
    private Integer isVipOnly;
    private String status;
    private Integer sortOrder;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private transient Integer totalViewCount;
    private List<PathStage> stages;
}