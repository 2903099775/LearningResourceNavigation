package com.learning.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 表示系统中的用户信息，包含用户的基本信息、角色、VIP过期日期等属性
 * 引用文件：c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\User.java
 */
@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String avatar;
    private String role;
    private LocalDate vipExpireDate;
    private Integer status;
    private Integer muteStatus;     // 禁言状态：0-未禁言, 1-被禁言
    private LocalDateTime muteEndDate; // 禁言结束时间
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}