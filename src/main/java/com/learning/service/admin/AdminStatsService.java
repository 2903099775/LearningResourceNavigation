package com.learning.service.admin;

import com.learning.common.ResponseResult;

/**
 * 管理员统计服务接口
 * 定义管理员对系统统计数据的操作方法
 * 引用文件：com.learning.common.ResponseResult
 */
public interface AdminStatsService {
    ResponseResult getStats();
    
    ResponseResult getUserStats();
    
    ResponseResult getPathStats();
    
    ResponseResult getUnitStats();
    
    ResponseResult getWeeklyReport();
    
    ResponseResult getPieChartData();
    
    ResponseResult getBarChartData();
    
    ResponseResult getLineChartData();
}