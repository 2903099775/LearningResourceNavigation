package com.learning.service;

import com.learning.common.ResponseResult;
import com.learning.entity.MonthlyActiveUser;
import java.util.List;

/**
 * 活跃用户统计服务接口
 * 提供月度活跃用户排行榜的查询、更新等功能
 */
public interface ActiveUserService {

    /**
     * 获取当前月份的活跃用户排行榜（前N名）
     * @param limit 返回数量，默认10
     * @return 活跃用户列表
     */
    ResponseResult<List<MonthlyActiveUser>> getMonthlyRanking(int limit);

    /**
     * 刷新/同步当前月份的活跃用户统计数据
     * 从posts表和comments表重新计算并更新
     * @return 操作结果
     */
    ResponseResult<String> refreshMonthlyStats();

    /**
     * 获取指定月份的活跃用户排行榜
     * @param yearMonth 年月，格式：YYYY-MM
     * @param limit 返回数量
     * @return 活跃用户列表
     */
    ResponseResult<List<MonthlyActiveUser>> getRankingByMonth(String yearMonth, int limit);
}