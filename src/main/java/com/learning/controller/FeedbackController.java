package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.entity.Feedback;
import com.learning.entity.Notification;
import com.learning.entity.User;
import com.learning.mapper.FeedbackMapper;
import com.learning.mapper.NotificationMapper;
import com.learning.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private static final Logger log = LoggerFactory.getLogger(FeedbackController.class);

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @PostMapping
    public ResponseResult<String> submitFeedback(@RequestBody Feedback feedback) {
        log.info("收到反馈提交请求: type={}, contentLength={}", 
                feedback != null ? feedback.getType() : "null",
                feedback != null && feedback.getContent() != null ? feedback.getContent().length() : 0);
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                log.warn("反馈提交失败: 用户未登录");
                return ResponseResult.error(401, "请先登录");
            }

            if (feedback.getContent() == null || feedback.getContent().trim().isEmpty()) {
                return ResponseResult.error(400, "反馈内容不能为空");
            }

            if (feedback.getContent().length() > 2000) {
                return ResponseResult.error(400, "反馈内容不能超过2000字");
            }

            feedback.setUserId(userId);
            feedback.setStatus("PENDING");
            if (feedback.getTitle() == null || feedback.getTitle().trim().isEmpty()) {
                feedback.setTitle(getTypeLabel(feedback.getType()) + "反馈");
            }
            log.info("准备插入反馈: userId={}, type={}, status={}", userId, feedback.getType(), feedback.getStatus());
            feedbackMapper.insert(feedback);
            log.info("反馈插入成功: id={}", feedback.getId());

            try {
                notifyAdmins(feedback);
            } catch (Exception e) {
                log.error("通知管理员失败，但反馈已保存: ", e);
            }

            return ResponseResult.success("反馈提交成功，感谢您的意见！");
        } catch (Exception e) {
            log.error("反馈提交异常: ", e);
            return ResponseResult.error(500, "反馈提交失败: " + e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseResult<Object> getMyFeedback() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error(401, "请先登录");
        }
        List<Feedback> list = feedbackMapper.findByUserId(userId);
        return ResponseResult.success(list);
    }

    private void notifyAdmins(Feedback feedback) {
        if (feedback == null || feedback.getUserId() == null) {
            return;
        }

        User submitter = userMapper.findById(feedback.getUserId());
        String username = submitter != null ? submitter.getUsername() : "匿名用户";

        List<User> admins = userMapper.findByRole("ADMIN");
        if (admins == null || admins.isEmpty()) {
            return;
        }

        Feedback savedFeedback = feedbackMapper.findById(feedback.getId());
        if (savedFeedback == null) {
            return;
        }

        String typeLabel = getTypeLabel(savedFeedback.getType());
        for (User admin : admins) {
            Notification notification = new Notification();
            notification.setUserId(admin.getId());
            notification.setType("FEEDBACK");
            notification.setTitle("📬 收到新的" + typeLabel + "反馈");
            notification.setContent(username + "：" + (savedFeedback.getContent().length() > 50
                    ? savedFeedback.getContent().substring(0, 50) + "..."
                    : savedFeedback.getContent()));
            notification.setRelatedType("FEEDBACK");
            notification.setRelatedId(savedFeedback.getId());
            notification.setIsRead(0);
            notificationMapper.insert(notification);
        }
    }

    private String getTypeLabel(String type) {
        if (type == null) return "用户";
        switch (type) {
            case "BUG": return "Bug";
            case "FEATURE": return "功能建议";
            case "RESOURCE": return "资源";
            default: return "用户";
        }
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            String username = (String) auth.getPrincipal();
            User user = userMapper.findByUsername(username);
            if (user != null) return user.getId();
        }
        return null;
    }
}
