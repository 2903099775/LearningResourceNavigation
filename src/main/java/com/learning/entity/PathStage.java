package com.learning.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.learning.deserializer.LongOrNullDeserializer;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习路线阶段实体类
 * 表示学习路线中的各个阶段（小节），包含阶段的基本信息、排序和下属学习单元等属性
 * 引用文件：c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\PathStage.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\LearningUnit.java
 */
@Data
public class PathStage {
    @JsonDeserialize(using = LongOrNullDeserializer.class)
    private Long id;
    private Long pathId;
    private String title;
    private String description;
    private Integer durationDays;
    private Integer sortOrder;
    private Integer isLocked;
    private LocalDateTime createdAt;
    private List<LearningUnit> units;
}