package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户期待实体类
 * 用户发布希望平台增加的课程或功能建议，其他用户可以点赞
 */
@Data
public class Wish {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String description;
    private String category;    // course: 课程期待, feature: 功能建议
    private Integer likeCount;
    private Integer status;     // 1: 待处理, 2: 已采纳, 3: 已上线
    private String adminReply;
    private LocalDateTime createdAt;
}
