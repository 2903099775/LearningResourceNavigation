package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习进度实体类
 * 表示用户的学习进度，包含学习状态、开始时间、完成时间和学习时长等属性
 */
@Data
public class LearningProgress {
    private Long id;
    private Long userId;
    private Long unitId;
    private Long pathId;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private LocalDateTime lastStudyTime;
    private Integer studyDuration;
    private Integer visitCount;
    private Integer rating;
    private Integer isFavorite;
}