package com.learning.entity;

import lombok.Data;

/**
 * 学习路线标签关联实体类
 * 表示学习路线和标签之间的多对多关联关系
 */
@Data
public class PathTag {
    private Long pathId;
    private Long tagId;
}