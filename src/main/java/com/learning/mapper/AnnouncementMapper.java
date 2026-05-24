package com.learning.mapper;

import com.learning.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnnouncementMapper {
    List<Announcement> findPublished(@Param("type") String type);
    Announcement findById(@Param("id") Long id);
    void insert(Announcement announcement);
    void update(Announcement announcement);
    void delete(@Param("id") Long id);
    List<Announcement> findAdminList(@Param("keyword") String keyword, @Param("type") String type, @Param("status") Integer status);
    Integer countAdminList(@Param("keyword") String keyword, @Param("type") String type, @Param("status") Integer status);
    List<Announcement> findPinned();
    void updateLikesCount(@Param("id") Long id, @Param("delta") Integer delta);
    void updatePinned(@Param("id") Long id, @Param("isPinned") Integer isPinned);
}
