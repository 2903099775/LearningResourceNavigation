package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 反馈实体类
 * 表示用户的反馈信息，包含反馈的类型、标题、内容、状态和回复等属性
 */
@Data
public class Feedback {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private String contact;
    private String status;
    private String reply;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}