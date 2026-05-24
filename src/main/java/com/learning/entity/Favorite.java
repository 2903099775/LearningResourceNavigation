package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收藏实体类
 * 表示用户的收藏信息，包含收藏的目标类型、目标ID和创建时间等属性
 */
@Data
public class Favorite {
    private Long id;
    private Long userId;
    private String targetType;
    private Long targetId;
    private LocalDateTime createdAt;
    
    // 关联的学习路径信息
    private LearningPath path;
    // 关联的学习单元信息
    private LearningUnit unit;
}