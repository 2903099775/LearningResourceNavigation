package com.learning.controller.admin;

import com.learning.common.ResponseResult;
import com.learning.service.admin.AdminStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员统计控制器
 * 负责处理管理员对系统统计数据的API请求
 * 引用文件：com.learning.common.ResponseResult, com.learning.service.admin.AdminStatsService
 */
@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired
    private AdminStatsService adminStatsService;

    @GetMapping
    public ResponseResult<Object> getStats() {
        return adminStatsService.getStats();
    }

    @GetMapping("/user")
    public ResponseResult<Object> getUserStats() {
        return adminStatsService.getUserStats();
    }

    @GetMapping("/path")
    public ResponseResult<Object> getPathStats() {
        return adminStatsService.getPathStats();
    }

    @GetMapping("/unit")
    public ResponseResult<Object> getUnitStats() {
        return adminStatsService.getUnitStats();
    }

    @GetMapping("/weekly")
    public ResponseResult<Object> getWeeklyReport() {
        return adminStatsService.getWeeklyReport();
    }

    @GetMapping("/pie-chart")
    public ResponseResult<Object> getPieChartData() {
        return adminStatsService.getPieChartData();
    }

    @GetMapping("/bar-chart")
    public ResponseResult<Object> getBarChartData() {
        return adminStatsService.getBarChartData();
    }

    @GetMapping("/line-chart")
    public ResponseResult<Object> getLineChartData() {
        return adminStatsService.getLineChartData();
    }
}