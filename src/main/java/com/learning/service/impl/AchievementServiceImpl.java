package com.learning.service.impl;

import com.learning.entity.Achievement;
import com.learning.entity.Comment;
import com.learning.entity.LearningProgress;
import com.learning.entity.User;
import com.learning.entity.UserAchievement;
import com.learning.mapper.AchievementMapper;
import com.learning.mapper.CommentMapper;
import com.learning.mapper.LearningProgressMapper;
import com.learning.mapper.NoteMapper;
import com.learning.mapper.UserMapper;
import com.learning.service.AchievementService;
import com.learning.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AchievementServiceImpl implements AchievementService {

    private static final Logger log = LoggerFactory.getLogger(AchievementServiceImpl.class);

    @Autowired
    private AchievementMapper achievementMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private LearningProgressMapper learningProgressMapper;

    @Override
    public List<Achievement> getAllAchievements() {
        return achievementMapper.findAll();
    }

    @Override
    public List<UserAchievement> getUserAchievements(Long userId) {
        return achievementMapper.findByUserId(userId);
    }

    @Override
    public Map<String, Object> getAchievementProgress(Long userId) {
        Map<String, Object> progress = new HashMap<>();

        try {
            List<Achievement> allAchievements = achievementMapper.findAll();
            if (allAchievements == null || allAchievements.isEmpty()) {
                progress.put("achievements", new ArrayList<>());
                progress.put("totalPoints", 0);
                progress.put("unlockedCount", 0);
                progress.put("totalCount", 0);
                return progress;
            }

            List<UserAchievement> unlockedAchievements = achievementMapper.findByUserId(userId);
            if (unlockedAchievements == null) {
                unlockedAchievements = new ArrayList<>();
            }

        Set<Long> unlockedIds = new HashSet<>();
        for (UserAchievement ua : unlockedAchievements) {
            unlockedIds.add(ua.getAchievementId());
        }

        int totalPoints = calculateTotalPoints(userId);

        List<Map<String, Object>> achievementList = new ArrayList<>();
        for (Achievement a : allAchievements) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", a.getId());
            item.put("code", a.getCode());
            item.put("name", a.getName());
            item.put("description", a.getDescription());
            item.put("icon", a.getIcon());
            item.put("points", a.getPoints());
            item.put("unlocked", unlockedIds.contains(a.getId()));
            item.put("conditionType", a.getConditionType());
            item.put("conditionValue", a.getConditionValue());
            achievementList.add(item);
        }

        progress.put("achievements", achievementList);
        progress.put("totalPoints", totalPoints);
        progress.put("unlockedCount", unlockedIds.size());
        progress.put("totalCount", allAchievements.size());

        return progress;
        } catch (Exception e) {
            log.error("获取成就进度失败, userId={}: {}", userId, e.getMessage(), e);
            progress.put("achievements", new ArrayList<>());
            progress.put("totalPoints", 0);
            progress.put("unlockedCount", 0);
            progress.put("totalCount", 0);
            return progress;
        }
    }

    @Override
    public void checkAndUnlockAchievements(Long userId) {
        User user = userService.findById(userId);
        if (user == null) return;

        List<Achievement> allAchievements = achievementMapper.findAll();
        Set<Long> unlockedIds = new HashSet<>();
        for (UserAchievement ua : achievementMapper.findByUserId(userId)) {
            unlockedIds.add(ua.getAchievementId());
        }

        Map<String, Integer> userStats = getUserStats(userId);

        for (Achievement achievement : allAchievements) {
            if (unlockedIds.contains(achievement.getId())) continue;

            boolean shouldUnlock = false;

            switch (achievement.getConditionType()) {
                case "FIRST_LEARNING":
                    shouldUnlock = userStats.get("completedUnits") >= 1;
                    break;
                case "CONSECUTIVE_DAYS":
                    shouldUnlock = userStats.get("consecutiveDays") >= achievement.getConditionValue();
                    break;
                case "COMPLETED_UNITS":
                    shouldUnlock = userStats.get("completedUnits") >= achievement.getConditionValue();
                    break;
                case "NOTE_COUNT":
                    shouldUnlock = userStats.get("noteCount") >= achievement.getConditionValue();
                    break;
                case "COMPLETED_PATHS":
                    shouldUnlock = userStats.get("completedPaths") >= achievement.getConditionValue();
                    break;
                case "COMMENT_COUNT":
                    shouldUnlock = userStats.get("commentCount") >= achievement.getConditionValue();
                    break;
                case "VIP_MEMBER":
                    shouldUnlock = userService.isVip(user.getUsername());
                    break;
                case "HELP_LEARNERS":
                    shouldUnlock = userStats.get("helpLearners") >= achievement.getConditionValue();
                    break;
            }

            if (shouldUnlock) {
                achievementMapper.insertUserAchievement(userId, achievement.getId());
            }
        }
    }

    @Override
    public int calculateTotalPoints(Long userId) {
        List<UserAchievement> achievements = achievementMapper.findByUserId(userId);
        int total = 0;
        for (UserAchievement ua : achievements) {
            if (ua.getAchievement() != null) {
                total += ua.getAchievement().getPoints();
            }
        }
        return total;
    }

    private Map<String, Integer> getUserStats(Long userId) {
        Map<String, Integer> stats = new HashMap<>();

        List<LearningProgress> progressList = learningProgressMapper.findByUserId(userId);

        int completedUnits = 0;
        int completedPaths = 0;
        Set<Long> pathIdsWithProgress = new HashSet<>();

        LocalDate today = LocalDate.now();
        int consecutiveDays = 0;
        LocalDate checkDate = today;

        for (LearningProgress progress : progressList) {
            if ("COMPLETED".equals(progress.getStatus())) {
                completedUnits++;
                if (progress.getPathId() != null) {
                    pathIdsWithProgress.add(progress.getPathId());
                }
            }

            if (progress.getLastStudyTime() != null) {
                LocalDate studyDate = progress.getLastStudyTime().toLocalDate();
                if (studyDate.equals(checkDate) || studyDate.equals(checkDate.minusDays(1))) {
                    if (studyDate.equals(checkDate.minusDays(1))) {
                        consecutiveDays++;
                        checkDate = studyDate;
                    } else if (consecutiveDays == 0) {
                        consecutiveDays = 1;
                    }
                }
            }
        }

        completedPaths = countCompletedPaths(userId, pathIdsWithProgress);

        int noteCount = noteMapper.findByUserId(userId).size();
        int commentCount = countUserComments(userId);
        int helpLearners = countHelpLearners(userId);

        stats.put("completedUnits", completedUnits);
        stats.put("completedPaths", completedPaths);
        stats.put("consecutiveDays", consecutiveDays);
        stats.put("noteCount", noteCount);
        stats.put("commentCount", commentCount);
        stats.put("helpLearners", helpLearners);

        return stats;
    }

    private int countCompletedPaths(Long userId, Set<Long> pathIdsWithProgress) {
        if (pathIdsWithProgress.isEmpty()) return 0;

        int completedPaths = 0;
        for (Long pathId : pathIdsWithProgress) {
            List<LearningProgress> pathProgress = learningProgressMapper.findByUserAndPath(userId, pathId);
            if (!pathProgress.isEmpty()) {
                int totalUnits = learningProgressMapper.countTotalByPath(pathId);
                long completedCount = pathProgress.stream()
                        .filter(p -> "COMPLETED".equals(p.getStatus()))
                        .count();
                if (totalUnits > 0 && completedCount >= totalUnits) {
                    completedPaths++;
                }
            }
        }
        return completedPaths;
    }

    private int countUserComments(Long userId) {
        List<Comment> comments = commentMapper.findByStatus(1);
        return (int) comments.stream()
                .filter(c -> userId.equals(c.getUserId()))
                .count();
    }

    private int countHelpLearners(Long userId) {
        return 0;
    }
}