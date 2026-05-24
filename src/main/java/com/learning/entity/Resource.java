package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习资源实体类
 * 表示学习单元中的学习资源，包含资源的标题、描述、类型、URL等属性
 */
@Data
public class Resource {
    private Long id;
    private Long unitId;
    private String title;
    private String description;
    private String resourceType;
    private String resourceUrl;
    private String sourcePlatform;
    private String author;
    private String duration;
    private Integer orderNum;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}