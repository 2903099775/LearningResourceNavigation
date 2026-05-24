package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 月度活跃用户统计实体类
 * 用于记录和展示用户的月度活跃度数据，包括发帖数、评论数、排名等信息
 */
@Data
public class MonthlyActiveUser {
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private String statMonth;      // 年月，格式：YYYY-MM
    private Integer postCount;     // 本月发帖数量
    private Integer commentCount;  // 本月评论数量
    private Integer totalScore;    // 综合得分（帖子数*2 + 评论数）
    private Integer rankPosition;  // 排名位置
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}