package com.learning.entity;

import lombok.Data;

/**
 * 标签实体类
 * 表示系统中的标签，包含标签的名称、分类和使用次数等属性
 */
@Data
public class Tag {
    private Long id;
    private String name;
    private String category;
    private Integer usageCount;
}