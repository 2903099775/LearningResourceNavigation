package com.learning.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 支付实体类
 * 表示用户的支付订单，包含订单号、金额、支付状态、过期日期等属性
 */
@Data
public class Payment {
    private Long id;
    private Long userId;
    private String orderNo;
    private BigDecimal amount;
    private Integer months;
    private String status;
    private LocalDateTime payTime;
    private LocalDate expireDate;
    private LocalDateTime createdAt;
}