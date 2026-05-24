package com.learning.service;

import com.learning.entity.Achievement;
import com.learning.entity.UserAchievement;

import java.util.List;
import java.util.Map;

public interface AchievementService {
    List<Achievement> getAllAchievements();

    List<UserAchievement> getUserAchievements(Long userId);

    Map<String, Object> getAchievementProgress(Long userId);

    void checkAndUnlockAchievements(Long userId);

    int calculateTotalPoints(Long userId);
}