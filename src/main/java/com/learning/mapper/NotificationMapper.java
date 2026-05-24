package com.learning.mapper;

import com.learning.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void insert(Notification notification);

    void markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    void markAllAsRead(@Param("userId") Long userId);

    List<Notification> findByUserId(@Param("userId") Long userId);

    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    int countUnreadByUserId(@Param("userId") Long userId);

    void delete(@Param("id") Long id);

    Notification findById(@Param("id") Long id);
}