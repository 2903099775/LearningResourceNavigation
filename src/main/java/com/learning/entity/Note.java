package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习笔记实体类
 * 表示用户的学习笔记，包含笔记的标题、内容、关联的学习单元和路线等属性
 */
@Data
public class Note {
    private Long id;
    private Long userId;
    private Long unitId;
    private Long pathId;
    private String title;
    private String content;
    private Integer isFavorite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}