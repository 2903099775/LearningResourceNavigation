package com.learning.mapper;

import com.learning.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {
    List<Post> findAll(@Param("keyword") String keyword, @Param("status") Integer status);

    List<Post> findByUserId(@Param("userId") Long userId);

    Post findById(@Param("id") Long id);

    void insert(Post post);

    void update(Post post);

    void delete(@Param("id") Long id);

    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    void updateLikesCount(@Param("id") Long id, @Param("delta") Integer delta);

    void updateCommentsCount(@Param("id") Long id, @Param("delta") Integer delta);

    void updatePinned(@Param("id") Long id, @Param("isPinned") Integer isPinned);

    int countAll(@Param("keyword") String keyword, @Param("status") Integer status);

    List<Map<String, Object>> findMonthlyTopUsers(@Param("limit") int limit);
}