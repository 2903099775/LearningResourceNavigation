package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体类
 * 表示用户对学习单元或其他内容的评论，包含评论的内容、目标、用户信息等属性
 */
@Data
public class Comment {
    private Long id;
    private Long userId;
    private String username;
    private String targetType;
    private Long targetId;
    private String content;
    private Long parentId;
    private Integer likesCount;
    private Integer status;
    private Integer isTop;
    private LocalDateTime createdAt;
    
    // 非数据库字段，用于存储回复列表
    private List<Comment> replies;
}