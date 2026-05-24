package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Achievement {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String icon;
    private Integer points;
    private String conditionType;
    private Integer conditionValue;
    private LocalDateTime createdAt;
}