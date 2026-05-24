package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningProgress;
import com.learning.entity.LearningUnit;
import com.learning.entity.LearningPath;
import com.learning.entity.PathStage;
import com.learning.entity.Notification;
import com.learning.entity.User;
import com.learning.mapper.LearningProgressMapper;
import com.learning.mapper.LearningUnitMapper;
import com.learning.mapper.LearningPathMapper;
import com.learning.mapper.PathStageMapper;
import com.learning.mapper.UserMapper;
import com.learning.mapper.NotificationMapper;
import com.learning.mapper.FavoriteMapper;
import com.learning.mapper.NoteMapper;
import com.learning.service.AchievementService;
import com.learning.service.LearningProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LearningProgressServiceImpl implements LearningProgressService {

    @Autowired
    private LearningProgressMapper learningProgressMapper;

    @Autowired
    private LearningUnitMapper learningUnitMapper;

    @Autowired
    private LearningPathMapper pathMapper;

    @Autowired
    private PathStageMapper stageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private AchievementService achievementService;

    private static final Map<Long, Set<Integer>> notifiedMilestones = new ConcurrentHashMap<>();

    @Override
    public ResponseResult<String> startLearning(Long unitId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        LearningProgress progress = learningProgressMapper.findByUserAndUnit(userId, unitId);

        if (progress == null) {
            progress = new LearningProgress();
            progress.setUserId(userId);
            progress.setUnitId(unitId);
            progress.setStatus("IN_PROGRESS");
            progress.setStartTime(LocalDateTime.now());
            progress.setLastStudyTime(LocalDateTime.now());
            learningProgressMapper.insert(progress);
        }

        return ResponseResult.success("开始学习成功");
    }

    @Override
    public ResponseResult<String> completeUnit(Long unitId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        LearningProgress progress = learningProgressMapper.findByUserAndUnit(userId, unitId);
        if (progress != null) {
            progress.setStatus("COMPLETED");
            progress.setCompleteTime(LocalDateTime.now());
            learningProgressMapper.update(progress);
        }

        checkAndSendMilestoneNotification(userId, unitId);

        achievementService.checkAndUnlockAchievements(userId);

        return ResponseResult.success("标记完成成功");
    }

    private void checkAndSendMilestoneNotification(Long userId, Long unitId) {
        LearningUnit unit = learningUnitMapper.findById(unitId);
        if (unit == null) return;

        Long stageId = unit.getStageId();
        if (stageId == null) return;

        PathStage stage = stageMapper.findById(stageId);
        if (stage == null) return;

        Long pathId = stage.getPathId();
        if (pathId == null) return;

        LearningPath path = pathMapper.findById(pathId);
        if (path == null) return;

        int totalUnits = learningUnitMapper.countByPathId(pathId);
        if (totalUnits == 0) return;

        int completedUnits = learningProgressMapper.countCompletedByPath(userId, pathId);

        int percentage = (completedUnits * 100) / totalUnits;

        Set<Integer> userMilestones = notifiedMilestones.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet());

        if (percentage >= 60 && !userMilestones.contains(60)) {
            sendProgressNotification(userId, 60, path.getTitle(), completedUnits, totalUnits);
            userMilestones.add(60);
        }
        if (percentage >= 80 && !userMilestones.contains(80)) {
            sendProgressNotification(userId, 80, path.getTitle(), completedUnits, totalUnits);
            userMilestones.add(80);
        }
        if (percentage >= 100 && !userMilestones.contains(100)) {
            sendProgressNotification(userId, 100, path.getTitle(), completedUnits, totalUnits);
            userMilestones.add(100);
        }
    }

    private void sendProgressNotification(Long userId, int milestone, String pathTitle, int completedUnits, int totalUnits) {
        String title;
        String content;

        if (milestone == 100) {
            title = "🎉 恭喜！学习路线已完成！";
            content = String.format("您已完成「%s」的全部 %d 个学习单元，获得完整知识体系！继续保持！", pathTitle, totalUnits);
        } else {
            title = "📢 学习进度提醒";
            content = String.format("恭喜！您在「%s」的学习进度已达到 %d%%（%d/%d单元），继续加油！", pathTitle, milestone, completedUnits, totalUnits);
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("PROGRESS_MILESTONE");
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedType("PATH");
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }

    @Override
    public ResponseResult<Object> getProgressOverview() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        List<LearningProgress> progressList = learningProgressMapper.findByUserId(userId);

        long total = progressList.size();
        long completed = progressList.stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .count();
        long inProgress = progressList.stream()
                .filter(p -> "IN_PROGRESS".equals(p.getStatus()))
                .count();

        Map<String, Object> overview = new HashMap<>();
        overview.put("total", total);
        overview.put("completed", completed);
        overview.put("inProgress", inProgress);
        overview.put("completionRate", total > 0 ? String.format("%.1f%%", (completed * 100.0 / total)) : "0%");

        return ResponseResult.success(overview);
    }

    @Override
    public ResponseResult<Object> getProgressStats() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        List<LearningProgress> progressList = learningProgressMapper.findByUserId(userId);

        long completedCount = progressList.stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .count();

        int totalDuration = progressList.stream()
                .filter(p -> p.getStudyDuration() != null)
                .mapToInt(LearningProgress::getStudyDuration)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("completedUnits", completedCount);
        stats.put("totalStudyMinutes", totalDuration);
        stats.put("totalStudyHours", totalDuration / 60);

        return ResponseResult.success(stats);
    }

    @Override
    public ResponseResult<String> recordVisit(Long unitId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        LearningProgress progress = learningProgressMapper.findByUserAndUnit(userId, unitId);
        if (progress == null) {
            // 尝试从学习单元中获取pathId
            Long pathId = null;
            LearningUnit unit = learningUnitMapper.findById(unitId);
            if (unit != null && unit.getStageId() != null) {
                PathStage stage = stageMapper.findById(unit.getStageId());
                if (stage != null) {
                    pathId = stage.getPathId();
                }
            }
            
            progress = new LearningProgress();
            progress.setUserId(userId);
            progress.setUnitId(unitId);
            progress.setPathId(pathId != null ? pathId : 0L); // 设置默认值0，避免null
            progress.setStatus("IN_PROGRESS");
            progress.setStartTime(LocalDateTime.now());
            progress.setLastStudyTime(LocalDateTime.now());
            progress.setStudyDuration(0);
            learningProgressMapper.insert(progress);
        } else {
            // 更新最近学习时间、状态，并累加学习时长（每次点击+60秒）
            learningProgressMapper.updateVisitTime(userId, unitId);
            learningProgressMapper.incrementStudyDuration(userId, unitId, 60);
        }
        return ResponseResult.success("学习访问已记录");
    }

    @Override
    public ResponseResult<Object> getUnitStats(Long unitId) {
        Long userId = getCurrentUserId();
        LearningProgress progress = null;
        int visitCount = 0;
        int rating = 0;
        int isFavorite = 0;
        if (userId != null) {
            progress = learningProgressMapper.findByUserAndUnit(userId, unitId);
            visitCount = progress != null && progress.getVisitCount() != null ? progress.getVisitCount() : 0;
            rating = progress != null && progress.getRating() != null ? progress.getRating() : 0;
            isFavorite = progress != null && progress.getIsFavorite() != null ? progress.getIsFavorite() : 0;
        }

        Map<String, Object> stats = new HashMap<>();
        if (progress != null) {
            stats.put("lastStudyTime", progress.getLastStudyTime() != null ? progress.getLastStudyTime() : progress.getStartTime());
        } else {
            stats.put("lastStudyTime", null);
        }
        stats.put("visitCount", visitCount);
        stats.put("status", progress != null ? progress.getStatus() : "NOT_STARTED");
        stats.put("rating", rating);
        stats.put("isFavorite", isFavorite);
        return ResponseResult.success(stats);
    }

    @Override
    public ResponseResult<Object> getRecentVisitors(Long unitId) {
        int count = learningProgressMapper.countRecentVisitors(unitId, 12);
        List<Map<String, Object>> visitors = learningProgressMapper.findRecentVisitors(unitId, 12);

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("visitors", visitors != null ? visitors : List.of());
        return ResponseResult.success(result);
    }

    @Override
    public ResponseResult<Object> getRecentLearningRecords(int limit) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        List<Map<String, Object>> records = learningProgressMapper.findRecentLearningRecords(userId, limit);

        if (records != null) {
            for (Map<String, Object> record : records) {
                Long unitId = (Long) record.get("unitId");
                LearningUnit unit = learningUnitMapper.findById(unitId);
                if (unit != null) {
                    record.put("unitTitle", unit.getTitle());
                    record.put("unitDescription", unit.getDescription());

                    Long stageId = unit.getStageId();
                    if (stageId != null) {
                        PathStage stage = stageMapper.findById(stageId);
                        if (stage != null) {
                            Long pathId = stage.getPathId();
                            if (pathId != null) {
                                LearningPath path = pathMapper.findById(pathId);
                                if (path != null) {
                                    record.put("pathTitle", path.getTitle());
                                }
                            }
                        }
                    }
                }
            }
        }

        return ResponseResult.success(records);
    }

    @Override
    public ResponseResult<String> rateUnit(Long unitId, Integer rating) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        if (rating < 1 || rating > 5) {
            return ResponseResult.error("评分必须在1-5之间");
        }
        
        LearningProgress progress = learningProgressMapper.findByUserAndUnit(userId, unitId);
        if (progress == null) {
            // 如果没有学习记录，先创建一条
            Long pathId = null;
            LearningUnit unit = learningUnitMapper.findById(unitId);
            if (unit != null && unit.getStageId() != null) {
                PathStage stage = stageMapper.findById(unit.getStageId());
                if (stage != null) {
                    pathId = stage.getPathId();
                }
            }
            
            progress = new LearningProgress();
            progress.setUserId(userId);
            progress.setUnitId(unitId);
            progress.setPathId(pathId != null ? pathId : 0L);
            progress.setStatus("NOT_STARTED");
            progress.setStartTime(LocalDateTime.now());
            progress.setLastStudyTime(LocalDateTime.now());
            progress.setStudyDuration(0);
            progress.setRating(rating);
            progress.setIsFavorite(0);
            learningProgressMapper.insert(progress);
        } else {
            learningProgressMapper.updateRating(userId, unitId, rating);
        }
        
        return ResponseResult.success("评分成功");
    }

    @Override
    public ResponseResult<String> toggleFavorite(Long unitId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        
        LearningProgress progress = learningProgressMapper.findByUserAndUnit(userId, unitId);
        Integer newFavoriteStatus;
        
        if (progress == null) {
            // 如果没有学习记录，先创建一条
            Long pathId = null;
            LearningUnit unit = learningUnitMapper.findById(unitId);
            if (unit != null && unit.getStageId() != null) {
                PathStage stage = stageMapper.findById(unit.getStageId());
                if (stage != null) {
                    pathId = stage.getPathId();
                }
            }
            
            progress = new LearningProgress();
            progress.setUserId(userId);
            progress.setUnitId(unitId);
            progress.setPathId(pathId != null ? pathId : 0L);
            progress.setStatus("NOT_STARTED");
            progress.setStartTime(LocalDateTime.now());
            progress.setLastStudyTime(LocalDateTime.now());
            progress.setStudyDuration(0);
            progress.setRating(0);
            progress.setIsFavorite(1);
            learningProgressMapper.insert(progress);
            newFavoriteStatus = 1;
            
            // 同时添加到收藏表
            if (!favoriteMapper.existsByUserAndTarget(userId, "UNIT", unitId)) {
                favoriteMapper.insertWithParams(userId, "UNIT", unitId);
            }
        } else {
            newFavoriteStatus = (progress.getIsFavorite() != null && progress.getIsFavorite() == 1) ? 0 : 1;
            learningProgressMapper.updateFavorite(userId, unitId, newFavoriteStatus);
            
            // 同时更新收藏表
            if (newFavoriteStatus == 1) {
                if (!favoriteMapper.existsByUserAndTarget(userId, "UNIT", unitId)) {
                    favoriteMapper.insertWithParams(userId, "UNIT", unitId);
                }
            } else {
                favoriteMapper.deleteByUserAndTarget(userId, "UNIT", unitId);
            }
        }
        
        return ResponseResult.success(newFavoriteStatus == 1 ? "已收藏" : "已取消收藏");
    }

    @Override
    public ResponseResult<Integer> getRating(Long unitId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.success(0);
        }
        Integer rating = learningProgressMapper.getRating(userId, unitId);
        return ResponseResult.success(rating != null ? rating : 0);
    }

    @Override
    public ResponseResult<Integer> getFavoriteStatus(Long unitId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.success(0);
        }
        Integer isFavorite = learningProgressMapper.getFavorite(userId, unitId);
        return ResponseResult.success(isFavorite != null ? isFavorite : 0);
    }

    @Override
    public ResponseResult<Object> getUserProfileStats() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        int completedUnits = learningProgressMapper.countCompletedUnitsByUser(userId);
        int totalVisits = learningProgressMapper.countTotalVisitsByUser(userId);

        List<LearningProgress> allProgress = learningProgressMapper.findByUserId(userId);
        int totalUnits = allProgress.size();
        int completedCount = (int) allProgress.stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .count();

        int totalUnitsInSystem = 0;
        List<LearningPath> allPaths = pathMapper.findAll();
        for (LearningPath path : allPaths) {
            totalUnitsInSystem += learningUnitMapper.countByPathId(path.getId());
        }

        double completionRate = totalUnitsInSystem > 0 ? (completedUnits * 100.0 / totalUnitsInSystem) : 0;
        String completionRateStr = String.format("%.1f%%", completionRate);

        int totalStudyDuration = 0;
        Integer duration = learningProgressMapper.getTotalStudyDuration(userId);
        if (duration != null) {
            totalStudyDuration = duration;
        }

        int completedPaths = learningProgressMapper.countCompletedPathsByUser(userId);

        int totalNotes = noteMapper.countByUserId(userId);

        int streakDays = calculateStreakDays(userId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("completedUnits", completedUnits);
        stats.put("totalVisits", totalVisits);
        stats.put("completionRate", completionRateStr);
        stats.put("totalStudyDuration", totalStudyDuration);
        stats.put("streakDays", streakDays);
        stats.put("completedPaths", completedPaths);
        stats.put("totalNotes", totalNotes);

        return ResponseResult.success(stats);
    }

    private int calculateStreakDays(Long userId) {
        List<LocalDateTime> studyDates = learningProgressMapper.getStudyDatesByUserId(userId);
        if (studyDates == null || studyDates.isEmpty()) {
            return 0;
        }

        Set<LocalDate> uniqueDates = new HashSet<>();
        for (LocalDateTime dt : studyDates) {
            if (dt != null) {
                uniqueDates.add(dt.toLocalDate());
            }
        }

        if (uniqueDates.isEmpty()) {
            return 0;
        }

        List<LocalDate> sortedDates = new ArrayList<>(uniqueDates);
        sortedDates.sort((a, b) -> b.compareTo(a));

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        if (!sortedDates.get(0).equals(today) && !sortedDates.get(0).equals(yesterday)) {
            return 0;
        }

        int streak = 1;
        for (int i = 0; i < sortedDates.size() - 1; i++) {
            LocalDate current = sortedDates.get(i);
            LocalDate next = sortedDates.get(i + 1);
            if (current.minusDays(1).equals(next)) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    @Override
    public ResponseResult<Object> getWeeklyStudyTrend() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        List<Map<String, Object>> dailyDuration = learningProgressMapper.getDailyStudyDuration(userId);
        List<Map<String, Object>> dailyCount = learningProgressMapper.getDailyStudyCount(userId);

        Map<String, Object> durationMap = new HashMap<>();
        for (Map<String, Object> row : dailyDuration) {
            Object dateObj = row.get("study_date");
            Object durationObj = row.get("total_duration");
            if (dateObj != null && durationObj != null) {
                String dateStr = dateObj.toString();
                if (dateObj instanceof java.sql.Date) {
                    dateStr = ((java.sql.Date) dateObj).toString();
                } else if (dateObj instanceof java.time.LocalDate) {
                    dateStr = ((java.time.LocalDate) dateObj).toString();
                }
                durationMap.put(dateStr, durationObj);
            }
        }

        Map<String, Object> countMap = new HashMap<>();
        for (Map<String, Object> row : dailyCount) {
            Object dateObj = row.get("study_date");
            Object countObj = row.get("study_count");
            if (dateObj != null && countObj != null) {
                String dateStr = dateObj.toString();
                if (dateObj instanceof java.sql.Date) {
                    dateStr = ((java.sql.Date) dateObj).toString();
                } else if (dateObj instanceof java.time.LocalDate) {
                    dateStr = ((java.time.LocalDate) dateObj).toString();
                }
                countMap.put(dateStr, countObj);
            }
        }

        List<String> dates = new ArrayList<>();
        List<Integer> durations = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.toString();
            dates.add(dateStr);

            Object duration = durationMap.get(dateStr);
            durations.add(duration != null ? ((Number) duration).intValue() : 0);

            Object count = countMap.get(dateStr);
            counts.add(count != null ? ((Number) count).intValue() : 0);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("durations", durations);
        result.put("counts", counts);

        return ResponseResult.success(result);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            User user = userMapper.findByUsername(username);
            if (user != null) return user.getId();
        }
        return null;
    }
}