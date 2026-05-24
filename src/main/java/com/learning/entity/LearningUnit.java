package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LearningUnit {
    private Long id;
    private Long stageId;
    private String title;
    private String type;
    private String contentType;
    private String externalUrl;
    private String platform;
    private String author;
    private Integer durationMinutes;
    private String description;
    private Integer sortOrder;
    private Integer isVipOnly;
    private Integer status;
    private LocalDateTime createdAt;
    private Integer viewCount;

    private transient boolean favorite;
    private transient String progressStatus;
    private transient Integer studyDuration;
    private transient String stageName;
    private transient Long pathId;
    private transient String pathName;
}
