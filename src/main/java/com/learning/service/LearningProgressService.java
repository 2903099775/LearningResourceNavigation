package com.learning.service;

import com.learning.common.ResponseResult;

/**
 * 学习进度服务接口
 * 定义学习进度相关的方法，包括开始学习、完成单元、获取进度概览和统计等
 * 引用文件：com.learning.common.ResponseResult
 */
public interface LearningProgressService {
    ResponseResult<String> startLearning(Long unitId);
    
    ResponseResult<String> completeUnit(Long unitId);
    
    ResponseResult<Object> getProgressOverview();
    
    ResponseResult<Object> getProgressStats();

    ResponseResult<String> recordVisit(Long unitId);

    ResponseResult<Object> getUnitStats(Long unitId);

    ResponseResult<Object> getRecentVisitors(Long unitId);
    
    ResponseResult<Object> getRecentLearningRecords(int limit);

    ResponseResult<String> rateUnit(Long unitId, Integer rating);

    ResponseResult<String> toggleFavorite(Long unitId);

    ResponseResult<Integer> getRating(Long unitId);

    ResponseResult<Integer> getFavoriteStatus(Long unitId);

    ResponseResult<Object> getUserProfileStats();

    ResponseResult<Object> getWeeklyStudyTrend();
}