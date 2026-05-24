package com.learning.service;

import com.learning.common.ResponseResult;

public interface NotificationService {
    ResponseResult<String> sendNotification(Long userId, String type, String title, String content, String relatedType, Long relatedId);

    ResponseResult<Object> getNotifications(Long userId);

    ResponseResult<Object> getUnreadNotifications(Long userId);

    ResponseResult<Object> getUnreadCount(Long userId);

    ResponseResult<String> markAsRead(Long notificationId, Long userId);

    ResponseResult<String> markAllAsRead(Long userId);

    ResponseResult<String> deleteNotification(Long notificationId);
}