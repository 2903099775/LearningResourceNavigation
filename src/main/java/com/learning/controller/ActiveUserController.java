package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.entity.MonthlyActiveUser;
import com.learning.service.ActiveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活跃用户统计控制器
 * 提供月度活跃用户排行榜的API接口
 */
@RestController
@RequestMapping("/api/active-users")
public class ActiveUserController {

    @Autowired
    private ActiveUserService activeUserService;

    /**
     * 获取当前月份的活跃用户排行榜（前10名）
     * GET /api/active-users/ranking
     *
     * @param limit 返回数量，可选，默认10，最大50
     * @return 活跃用户列表（按综合得分降序排列）
     */
    @GetMapping("/ranking")
    public ResponseResult<List<MonthlyActiveUser>> getMonthlyRanking(
            @RequestParam(defaultValue = "10") int limit) {
        // 限制最大返回数量为50
        if (limit > 50) {
            limit = 50;
        }
        return activeUserService.getMonthlyRanking(limit);
    }

    /**
     * 获取指定月份的活跃用户排行榜
     * GET /api/active-users/ranking/{yearMonth}
     *
     * @param yearMonth 年月，格式：YYYY-MM（例如：2026-05）
     * @param limit 返回数量，默认10
     * @return 活跃用户列表
     */
    @GetMapping("/ranking/{yearMonth}")
    public ResponseResult<List<MonthlyActiveUser>> getRankingByMonth(
            @PathVariable String yearMonth,
            @RequestParam(defaultValue = "10") int limit) {
        // 限制最大返回数量为50
        if (limit > 50) {
            limit = 50;
        }
        return activeUserService.getRankingByMonth(yearMonth, limit);
    }

    /**
     * 手动刷新当前月份的活跃用户统计数据
     * POST /api/active-users/refresh
     *
     * 管理员可调用此接口强制刷新统计数据
     * @return 操作结果
     */
    @PostMapping("/refresh")
    public ResponseResult<String> refreshMonthlyStats() {
        return activeUserService.refreshMonthlyStats();
    }
}