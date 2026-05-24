package com.learning.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 退款申请实体类
 * 表示用户的退款申请，包含用户ID、支付订单号、退款原因、申请状态等属性
 */
@Data
public class RefundRequest {
    private Long id;
    private Long userId;
    private String orderNo;
    private String reason;
    private String status; // PENDING: 待审核, APPROVED: 已批准, REJECTED: 已拒绝
    private String adminRemark;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}