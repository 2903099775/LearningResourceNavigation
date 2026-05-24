package com.learning.service.impl.admin;

import com.learning.common.ResponseResult;
import com.learning.mapper.*;
import com.learning.service.admin.AdminStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminStatsServiceImpl implements AdminStatsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LearningPathMapper learningPathMapper;

    @Autowired
    private LearningUnitMapper learningUnitMapper;

    @Autowired
    private LearningProgressMapper learningProgressMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    private static int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public ResponseResult getStats() {
        List<Map<String, Object>> paths = learningPathMapper.findAllWithDetails();
        List<Map<String, Object>> users = userMapper.findAllWithDetails();
        List<Map<String, Object>> units = learningUnitMapper.findAllWithDetails();
        int totalComments = commentMapper.countAll();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPaths", paths.size());
        stats.put("totalUsers", users.size());
        stats.put("totalUnits", units.size());
        stats.put("totalComments", totalComments);
        stats.put("message", "统计数据获取成功");

        return ResponseResult.success(stats);
    }

    @Override
    public ResponseResult getUserStats() {
        List<Map<String, Object>> users = userMapper.findAllWithDetails();
        int totalUsers = users.size();
        int vipUsers = (int) users.stream().filter(u -> u.get("vip_expire_date") != null).count();
        int adminUsers = (int) users.stream().filter(u -> "ADMIN".equals(u.get("role"))).count();
        int activeUsers = (int) users.stream().filter(u -> toInt(u.get("status")) == 1).count();

        Map<String, Object> userStats = new HashMap<>();
        userStats.put("totalUsers", totalUsers);
        userStats.put("vipUsers", vipUsers);
        userStats.put("adminUsers", adminUsers);
        userStats.put("activeUsers", activeUsers);

        return ResponseResult.success(userStats);
    }

    @Override
    public ResponseResult getPathStats() {
        List<Map<String, Object>> paths = learningPathMapper.findAllWithDetails();
        int totalPaths = paths.size();
        int publishedPaths = (int) paths.stream().filter(p -> "PUBLISHED".equals(p.get("status"))).count();
        int vipPaths = (int) paths.stream().filter(p -> toInt(p.get("is_vip_only")) == 1).count();

        Map<String, Long> difficultyDistribution = paths.stream()
                .collect(Collectors.groupingBy(p -> Objects.toString(p.get("difficulty"), "UNKNOWN"), Collectors.counting()));

        Map<String, Object> pathStats = new HashMap<>();
        pathStats.put("totalPaths", totalPaths);
        pathStats.put("publishedPaths", publishedPaths);
        pathStats.put("vipPaths", vipPaths);
        pathStats.put("difficultyDistribution", difficultyDistribution);

        return ResponseResult.success(pathStats);
    }

    @Override
    public ResponseResult getUnitStats() {
        List<Map<String, Object>> units = learningUnitMapper.findAllWithDetails();
        int totalUnits = units.size();
        int vipUnits = (int) units.stream().filter(u -> toInt(u.get("is_vip_only")) == 1).count();

        Map<String, Long> typeDistribution = units.stream()
                .collect(Collectors.groupingBy(u -> Objects.toString(u.get("type"), "UNKNOWN"), Collectors.counting()));

        int totalViews = units.stream().mapToInt(u -> toInt(u.get("view_count"))).sum();

        Map<String, Object> unitStats = new HashMap<>();
        unitStats.put("totalUnits", totalUnits);
        unitStats.put("vipUnits", vipUnits);
        unitStats.put("typeDistribution", typeDistribution);
        unitStats.put("totalViews", totalViews);

        return ResponseResult.success(unitStats);
    }

    @Override
    public ResponseResult getWeeklyReport() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        Map<String, Object> weeklyReport = new HashMap<>();
        weeklyReport.put("weekStart", startOfWeek.toString());
        weeklyReport.put("weekEnd", endOfWeek.toString());

        int newUsers = userMapper.countUsersByDateRange(startOfWeek.atStartOfDay(), endOfWeek.plusDays(1).atStartOfDay());
        weeklyReport.put("newUsers", newUsers);

        int completedUnits = learningProgressMapper.countCompletedUnitsByDateRange(startOfWeek.atStartOfDay(), endOfWeek.plusDays(1).atStartOfDay());
        weeklyReport.put("completedUnits", completedUnits);

        int newComments = commentMapper.countCommentsByDateRange(startOfWeek.atStartOfDay(), endOfWeek.plusDays(1).atStartOfDay());
        weeklyReport.put("newComments", newComments);

        int newFavorites = favoriteMapper.countFavoritesByDateRange(startOfWeek.atStartOfDay(), endOfWeek.plusDays(1).atStartOfDay());
        weeklyReport.put("newFavorites", newFavorites);

        return ResponseResult.success(weeklyReport);
    }

    @Override
    public ResponseResult getPieChartData() {
        Map<String, Object> pieChartData = new HashMap<>();

        List<Map<String, Object>> users = userMapper.findAllWithDetails();
        Map<String, Long> roleDistribution = users.stream()
                .collect(Collectors.groupingBy(u -> Objects.toString(u.get("role"), "UNKNOWN"), Collectors.counting()));
        pieChartData.put("roleDistribution", roleDistribution);

        List<Map<String, Object>> paths = learningPathMapper.findAllWithDetails();
        Map<String, Long> difficultyDistribution = paths.stream()
                .collect(Collectors.groupingBy(p -> Objects.toString(p.get("difficulty"), "UNKNOWN"), Collectors.counting()));
        pieChartData.put("difficultyDistribution", difficultyDistribution);

        List<Map<String, Object>> units = learningUnitMapper.findAllWithDetails();
        Map<String, Long> unitTypeDistribution = units.stream()
                .collect(Collectors.groupingBy(u -> Objects.toString(u.get("type"), "UNKNOWN"), Collectors.counting()));
        pieChartData.put("unitTypeDistribution", unitTypeDistribution);

        Map<String, Long> vipDistribution = users.stream()
                .collect(Collectors.groupingBy(u -> {
                    Object expireDate = u.get("vip_expire_date");
                    if (expireDate == null) return "普通用户";
                    LocalDate now = LocalDate.now();
                    if (expireDate instanceof java.sql.Date) {
                        return ((java.sql.Date) expireDate).toLocalDate().isAfter(now) ? "VIP用户" : "普通用户";
                    }
                    if (expireDate instanceof LocalDate) {
                        return ((LocalDate) expireDate).isAfter(now) ? "VIP用户" : "普通用户";
                    }
                    if (expireDate instanceof LocalDateTime) {
                        return ((LocalDateTime) expireDate).toLocalDate().isAfter(now) ? "VIP用户" : "普通用户";
                    }
                    try {
                        LocalDate parsed = LocalDate.parse(expireDate.toString().substring(0, 10));
                        return parsed.isAfter(now) ? "VIP用户" : "普通用户";
                    } catch (Exception e) {
                        return "普通用户";
                    }
                }, Collectors.counting()));
        pieChartData.put("vipDistribution", vipDistribution);

        return ResponseResult.success(pieChartData);
    }

    @Override
    public ResponseResult getBarChartData() {
        Map<String, Object> barChartData = new HashMap<>();

        List<Map<String, Object>> pathProgress = learningProgressMapper.countUsersByPath();
        Map<String, Integer> pathUserCount = new HashMap<>();
        for (Map<String, Object> item : pathProgress) {
            pathUserCount.put(Objects.toString(item.get("title"), "UNKNOWN"), toInt(item.get("user_count")));
        }
        barChartData.put("pathUserCount", pathUserCount);

        List<Map<String, Object>> units = learningUnitMapper.findAllWithDetails();
        Map<String, Long> unitTypeCount = units.stream()
                .collect(Collectors.groupingBy(u -> Objects.toString(u.get("type"), "UNKNOWN"), Collectors.counting()));
        barChartData.put("unitTypeCount", unitTypeCount);

        return ResponseResult.success(barChartData);
    }

    @Override
    public ResponseResult getLineChartData() {
        Map<String, Object> lineChartData = new HashMap<>();

        LocalDateTime startDate = LocalDateTime.now().minusDays(29);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<Map<String, Object>> userTrend = userMapper.countUsersGroupByDate(startDate, endDate);
        Map<String, Integer> userRegistrationTrend = new LinkedHashMap<>();
        for (int i = 0; i < 30; i++) {
            LocalDate date = startDate.plusDays(i).toLocalDate();
            userRegistrationTrend.put(date.toString(), 0);
        }
        for (Map<String, Object> item : userTrend) {
            Object dateKey = item.get("register_date");
            if (dateKey != null) {
                String dateStr = dateKey.toString().substring(0, 10);
                userRegistrationTrend.put(dateStr, toInt(item.get("user_count")));
            }
        }
        lineChartData.put("userRegistrationTrend", userRegistrationTrend);

        List<Map<String, Object>> activityTrend = learningProgressMapper.countProgressGroupByDate(startDate, endDate);
        Map<String, Integer> learningActivityTrend = new LinkedHashMap<>();
        for (int i = 0; i < 30; i++) {
            LocalDate date = startDate.plusDays(i).toLocalDate();
            learningActivityTrend.put(date.toString(), 0);
        }
        for (Map<String, Object> item : activityTrend) {
            Object dateKey = item.get("activity_date");
            if (dateKey != null) {
                String dateStr = dateKey.toString().substring(0, 10);
                learningActivityTrend.put(dateStr, toInt(item.get("activity_count")));
            }
        }
        lineChartData.put("learningActivityTrend", learningActivityTrend);

        return ResponseResult.success(lineChartData);
    }
}
