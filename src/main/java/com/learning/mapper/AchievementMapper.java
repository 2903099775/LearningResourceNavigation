package com.learning.mapper;

import com.learning.entity.Achievement;
import com.learning.entity.UserAchievement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AchievementMapper {
    List<Achievement> findAll();

    Achievement findById(@Param("id") Long id);

    Achievement findByCode(@Param("code") String code);

    List<UserAchievement> findByUserId(@Param("userId") Long userId);

    UserAchievement findByUserAndAchievement(@Param("userId") Long userId, @Param("achievementId") Long achievementId);

    void insertUserAchievement(@Param("userId") Long userId, @Param("achievementId") Long achievementId);

    int countUnlockedByUser(@Param("userId") Long userId);
}