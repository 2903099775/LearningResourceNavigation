package com.learning.mapper;

import com.learning.entity.Wish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WishMapper {
    List<Wish> findAll(@Param("category") String category, @Param("sort") String sort);
    Wish findById(@Param("id") Long id);
    void insert(Wish wish);
    void updateLikeCount(@Param("id") Long id, @Param("delta") Integer delta);
    Integer hasUserLiked(@Param("wishId") Long wishId, @Param("userId") Long userId);
    void addUserLike(@Param("wishId") Long wishId, @Param("userId") Long userId);
    void removeUserLike(@Param("wishId") Long wishId, @Param("userId") Long userId);
    void updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("adminReply") String adminReply);
    void delete(@Param("id") Long id);
    List<Wish> findAdminList(@Param("status") Integer status);
    Integer countPending();
}
