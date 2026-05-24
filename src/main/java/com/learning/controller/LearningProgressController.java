package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.service.LearningProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学习进度控制器
 * 负责处理学习进度相关的API请求，包括开始学习、完成单元、获取进度概览和统计等操作
 * 引用文件：com.learning.common.ResponseResult, com.learning.service.LearningProgressService
 */
@RestController
@RequestMapping("/api/progress")
public class LearningProgressController {

    @Autowired
    private LearningProgressService learningProgressService;

    @PostMapping("/start/{unitId}")
    public ResponseResult<String> startLearning(@PathVariable Long unitId) {
        return learningProgressService.startLearning(unitId);
    }

    @PostMapping("/complete/{unitId}")
    public ResponseResult<String> completeUnit(@PathVariable Long unitId) {
        return learningProgressService.completeUnit(unitId);
    }

    @GetMapping("/overview")
    public ResponseResult<Object> getProgressOverview() {
        return learningProgressService.getProgressOverview();
    }

    @GetMapping("/stats")
    public ResponseResult<Object> getProgressStats() {
        return learningProgressService.getProgressStats();
    }

    // 记录学习访问（点击外部链接时调用）
    @PostMapping("/visit/{unitId}")
    public ResponseResult<String> recordVisit(@PathVariable Long unitId) {
        return learningProgressService.recordVisit(unitId);
    }

    // 获取某单元的学习统计
    @GetMapping("/unit-stats/{unitId}")
    public ResponseResult<Object> getUnitStats(@PathVariable Long unitId) {
        return learningProgressService.getUnitStats(unitId);
    }

    // 获取近12小时内学习某单元的用户（公开接口）
    @GetMapping("/recent-visitors/{unitId}")
    public ResponseResult<Object> getRecentVisitors(@PathVariable Long unitId) {
        return learningProgressService.getRecentVisitors(unitId);
    }

    // 获取用户最近的学习记录
    @GetMapping("/recent")
    public ResponseResult<Object> getRecentLearningRecords(@RequestParam(defaultValue = "10") int limit) {
        return learningProgressService.getRecentLearningRecords(limit);
    }

    // 评分功能
    @PostMapping("/rate/{unitId}")
    public ResponseResult<String> rateUnit(@PathVariable Long unitId, @RequestParam Integer rating) {
        return learningProgressService.rateUnit(unitId, rating);
    }

    // 获取评分
    @GetMapping("/rating/{unitId}")
    public ResponseResult<Integer> getRating(@PathVariable Long unitId) {
        return learningProgressService.getRating(unitId);
    }

    // 切换收藏状态
    @PostMapping("/favorite/{unitId}")
    public ResponseResult<String> toggleFavorite(@PathVariable Long unitId) {
        return learningProgressService.toggleFavorite(unitId);
    }

    // 获取收藏状态
    @GetMapping("/favorite/{unitId}")
    public ResponseResult<Integer> getFavoriteStatus(@PathVariable Long unitId) {
        return learningProgressService.getFavoriteStatus(unitId);
    }

    // 获取用户profile统计信息（已完成单元、学习次数、完成率）
    @GetMapping("/profile-stats")
    public ResponseResult<Object> getUserProfileStats() {
        return learningProgressService.getUserProfileStats();
    }

    // 获取过去7天学习趋势数据
    @GetMapping("/weekly-trend")
    public ResponseResult<Object> getWeeklyStudyTrend() {
        return learningProgressService.getWeeklyStudyTrend();
    }
}