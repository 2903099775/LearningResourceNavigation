package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.entity.MonthlyActiveUser;
import com.learning.mapper.MonthlyActiveUserMapper;
import com.learning.service.ActiveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 活跃用户统计服务实现类
 * 负责月度活跃用户排行榜的数据处理和业务逻辑
 */
@Service
public class ActiveUserServiceImpl implements ActiveUserService {

    @Autowired
    private MonthlyActiveUserMapper monthlyActiveUserMapper;

    /**
     * 获取当前月份的活跃用户排行榜（前N名）
     */
    @Override
    public ResponseResult<List<MonthlyActiveUser>> getMonthlyRanking(int limit) {
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return getRankingByMonth(currentMonth, limit);
    }

    /**
     * 刷新/同步当前月份的活跃用户统计数据
     * 从posts表和comments表重新计算并更新到monthly_active_users表
     */
    @Override
    public ResponseResult<String> refreshMonthlyStats() {
        try {
            String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

            // 调用Mapper的upsert方法，基于posts和comments表实时计算数据
            monthlyActiveUserMapper.upsertUserActivity(null, currentMonth);

            return ResponseResult.success("统计数据刷新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("统计数据刷新失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定月份的活跃用户排行榜
     */
    @Override
    public ResponseResult<List<MonthlyActiveUser>> getRankingByMonth(String yearMonth, int limit) {
        if (yearMonth == null || yearMonth.isEmpty()) {
            yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        if (limit <= 0) {
            limit = 10; // 默认返回前10名
        }

        List<MonthlyActiveUser> ranking = monthlyActiveUserMapper.getMonthlyRanking(yearMonth, limit);

        if (ranking == null || ranking.isEmpty()) {
            return ResponseResult.success("本月暂无活跃用户数据", java.util.Collections.emptyList());
        }

        return ResponseResult.success(ranking);
    }
}