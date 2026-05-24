package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.entity.User;
import com.learning.mapper.UserMapper;
import com.learning.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public ResponseResult<Object> getNotifications() {
        Long userId = getCurrentUserId();
        return notificationService.getNotifications(userId);
    }

    @GetMapping("/unread")
    public ResponseResult<Object> getUnreadNotifications() {
        Long userId = getCurrentUserId();
        return notificationService.getUnreadNotifications(userId);
    }

    @GetMapping("/unread-count")
    public ResponseResult<Object> getUnreadCount() {
        Long userId = getCurrentUserId();
        return notificationService.getUnreadCount(userId);
    }

    @PostMapping("/{id}/read")
    public ResponseResult<String> markAsRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return notificationService.markAsRead(id, userId);
    }

    @PostMapping("/read-all")
    public ResponseResult<String> markAllAsRead() {
        Long userId = getCurrentUserId();
        return notificationService.markAllAsRead(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<String> deleteNotification(@PathVariable Long id) {
        return notificationService.deleteNotification(id);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            String username = (String) auth.getPrincipal();
            User user = userMapper.findByUsername(username);
            if (user != null) return user.getId();
        }
        return 1L;
    }
}