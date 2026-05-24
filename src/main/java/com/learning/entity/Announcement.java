package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 公告实体类
 * 管理员发布的课程预告、功能更新等公告信息
 */
@Data
public class Announcement {
    private Long id;
    private Long adminId;
    private String title;
    private String content;
    private String type;        // announcement: 课程预告, feature: 功能更新, notice: 系统通知
    private Integer status;     // 1: 已发布, 0: 草稿
    private Integer priority;   // 优先级：1-普通, 2-置顶, 3-紧急
    private Integer likesCount; // 点赞数
    private Integer isPinned;   // 是否置顶：1-是, 0-否
    private LocalDateTime publishAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
