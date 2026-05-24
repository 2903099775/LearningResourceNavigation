package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.entity.Notification;
import com.learning.mapper.NotificationMapper;
import com.learning.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public ResponseResult<String> sendNotification(Long userId, String type, String title, String content, String relatedType, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedType(relatedType);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
        return ResponseResult.success("通知已发送");
    }

    @Override
    public ResponseResult<Object> getNotifications(Long userId) {
        List<Notification> list = notificationMapper.findByUserId(userId);
        return ResponseResult.success(list);
    }

    @Override
    public ResponseResult<Object> getUnreadNotifications(Long userId) {
        List<Notification> list = notificationMapper.findUnreadByUserId(userId);
        return ResponseResult.success(list);
    }

    @Override
    public ResponseResult<Object> getUnreadCount(Long userId) {
        int count = notificationMapper.countUnreadByUserId(userId);
        return ResponseResult.success(count);
    }

    @Override
    public ResponseResult<String> markAsRead(Long notificationId, Long userId) {
        notificationMapper.markAsRead(notificationId, userId);
        return ResponseResult.success("标记已读成功");
    }

    @Override
    public ResponseResult<String> markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
        return ResponseResult.success("全部标记已读成功");
    }

    @Override
    public ResponseResult<String> deleteNotification(Long notificationId) {
        notificationMapper.delete(notificationId);
        return ResponseResult.success("删除成功");
    }
}