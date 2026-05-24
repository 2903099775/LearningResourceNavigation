package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 社区帖子实体类
 * 表示用户在社区中发布的帖子，包含标题、内容、作者信息、点赞数、评论数等属性
 */
@Data
public class Post {
    private Long id;
    private Long userId;
    private String username;
    private String userAvatar;
    private String title;
    private String content;
    private String images;         // 图片URLs，逗号分隔
    private Integer likesCount;     // 点赞数
    private Integer commentsCount;  // 评论数
    private Integer status;         // 状态：1-正常, 0-已删除
    private Integer isPinned;       // 是否置顶：1-是, 0-否
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}