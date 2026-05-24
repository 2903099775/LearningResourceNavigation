package com.learning.entity;

import java.time.LocalDateTime;

/**
 * 学习子域实体类
 * 对应5层层次结构中的第二层：领域 → 子域 → 路线 → 阶段 → 单元
 * 例如：编程 → Python、Java
 */
public class LearningSubcategory {
    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private String icon;
    private String coverImage;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联对象
    private LearningCategory category;
    // 子分类下的学习单元数量
    private transient Integer pathCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LearningCategory getCategory() { return category; }
    public void setCategory(LearningCategory category) { this.category = category; }

    public Integer getPathCount() { return pathCount; }
    public void setPathCount(Integer pathCount) { this.pathCount = pathCount; }
}
