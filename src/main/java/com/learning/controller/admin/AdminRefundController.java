package com.learning.controller.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.Payment;
import com.learning.entity.RefundRequest;
import com.learning.entity.User;
import com.learning.entity.Notification;
import com.learning.mapper.PaymentMapper;
import com.learning.mapper.RefundRequestMapper;
import com.learning.mapper.NotificationMapper;
import com.learning.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 退款申请管理控制器
 */
@RestController
@RequestMapping("/api/admin/refund")
public class AdminRefundController {

    private static final Logger logger = LoggerFactory.getLogger(AdminRefundController.class);

    @Autowired
    private RefundRequestMapper refundRequestMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 获取所有退款申请
     */
    @GetMapping("/list")
    public ResponseResult<List<RefundRequest>> getAllRefundRequests() {
        List<RefundRequest> requests = refundRequestMapper.findAll();
        return ResponseResult.success(requests);
    }

    /**
     * 获取待审核的退款申请
     */
    @GetMapping("/pending")
    public ResponseResult<List<RefundRequest>> getPendingRequests() {
        List<RefundRequest> requests = refundRequestMapper.findPendingRequests();
        return ResponseResult.success(requests);
    }

    /**
     * 获取待审核数量
     */
    @GetMapping("/count")
    public ResponseResult<Integer> getPendingCount() {
        int count = refundRequestMapper.countPending();
        return ResponseResult.success(count);
    }

    /**
     * 审核退款申请
     */
    @PostMapping("/review")
    public ResponseResult<Object> reviewRefundRequest(@RequestBody Map<String, Object> body) {
        Long requestId = ((Number) body.get("id")).longValue();
        String status = (String) body.get("status");
        String remark = (String) body.get("remark");

        RefundRequest request = refundRequestMapper.findById(requestId);
        if (request == null) {
            return ResponseResult.error(404, "退款申请不存在");
        }

        if (!"PENDING".equals(request.getStatus())) {
            return ResponseResult.error(400, "该申请已处理");
        }

        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            return ResponseResult.error(400, "无效的状态值");
        }

        request.setStatus(status);
        request.setAdminRemark(remark);
        request.setProcessedAt(LocalDateTime.now());
        refundRequestMapper.updateStatus(request);

        if ("APPROVED".equals(status)) {
            // 批准退款：更新支付记录状态和用户VIP到期日期
            Payment payment = paymentMapper.findByOrderNo(request.getOrderNo());
            if (payment != null) {
                payment.setStatus("CANCELLED");
                paymentMapper.update(payment);

                User user = userService.findById(request.getUserId());
                if (user != null) {
                    // 设置VIP到期日期为昨天，确保用户不再是VIP
                    user.setVipExpireDate(LocalDate.now().minusDays(1));
                    userService.update(user);

                    // 发送退款成功通知给用户
                    Notification notification = new Notification();
                    notification.setUserId(user.getId());
                    notification.setType("REFUND_APPROVED");
                    notification.setTitle("退款申请已通过");
                    notification.setContent("您的退款申请已批准，VIP会员资格已取消。如有疑问请联系客服。");
                    notification.setIsRead(0);
                    notification.setCreatedAt(LocalDateTime.now());
                    notificationMapper.insert(notification);
                }
            }
            logger.info("管理员批准退款申请，ID: {}, 用户ID: {}, 订单号: {}", requestId, request.getUserId(), request.getOrderNo());
        } else {
            logger.info("管理员拒绝退款申请，ID: {}, 用户ID: {}, 订单号: {}", requestId, request.getUserId(), request.getOrderNo());
        }

        return ResponseResult.success("处理成功", null);
    }

    /**
     * 获取退款申请详情
     */
    @GetMapping("/{id}")
    public ResponseResult<RefundRequest> getRefundRequest(@PathVariable Long id) {
        RefundRequest request = refundRequestMapper.findById(id);
        if (request == null) {
            return ResponseResult.error(404, "退款申请不存在");
        }
        return ResponseResult.success(request);
    }
}