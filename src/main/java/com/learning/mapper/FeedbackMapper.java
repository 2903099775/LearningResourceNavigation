package com.learning.mapper;

import com.learning.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeedbackMapper {
    void insert(Feedback feedback);
    List<Feedback> findAll(@Param("status") String status);
    Feedback findById(@Param("id") Long id);
    void updateReply(@Param("id") Long id, @Param("reply") String reply, @Param("status") String status);
    void delete(@Param("id") Long id);
    List<Feedback> findByUserId(@Param("userId") Long userId);
    int countByStatus(@Param("status") String status);
}
