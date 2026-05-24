package com.learning.mapper;

import com.learning.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论数据访问接口
 * 负责评论相关的数据库操作，包括查询、插入、更新和删除等
 * 引用文件：com.learning.entity.Comment
 */
@Mapper
public interface CommentMapper {
    List<Comment> findByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    Comment findById(@Param("id") Long id);

    void insert(Comment comment);

    void updateLikesCount(@Param("id") Long id);

    void delete(@Param("id") Long id);

    List<Comment> findByStatus(@Param("status") Integer status);

    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    void updateContent(@Param("id") Long id, @Param("content") String content);

    void updateTop(@Param("id") Long id, @Param("isTop") Integer isTop);

    List<Comment> findAll();
    
    int countAll();
    
    int countCommentsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
