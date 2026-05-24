package com.learning.service;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningUnit;

import java.util.List;

/**
 * 学习单元服务接口
 * 定义学习单元的CRUD操作、高级查询、统计、互动功能和进度跟踪等方法
 * 引用文件：c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\common\ResponseResult.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\LearningUnit.java
 */
public interface LearningUnitService {
    // 基本CRUD操作
    ResponseResult getUnitById(Long id);
    ResponseResult getUnitsByStageId(Long stageId);
    ResponseResult getUnitsByPathId(Long pathId);
    ResponseResult createUnit(LearningUnit learningUnit);
    ResponseResult updateUnit(Long id, LearningUnit learningUnit);
    ResponseResult deleteUnit(Long id);
    
    // 高级查询
    ResponseResult getUnitsByType(String type);
    ResponseResult getUnitsByPlatform(String platform);
    ResponseResult getVipOnlyUnits();
    ResponseResult searchUnitsByTitle(String keyword);
    
    // 统计相关
    ResponseResult countUnitsByStageId(Long stageId);
    ResponseResult countUnitsByPathId(Long pathId);
    ResponseResult countTotalUnits();
    
    // 互动功能
    ResponseResult addComment(Long unitId, String content);
    ResponseResult getCommentsByUnitId(Long unitId);
    ResponseResult addFavorite(Long unitId);
    ResponseResult removeFavorite(Long unitId);
    ResponseResult checkFavoriteStatus(Long unitId);
    
    // 进度跟踪
    ResponseResult updateProgress(Long unitId, String status, Integer studyDuration);
    ResponseResult getProgressByUnitId(Long unitId);
    ResponseResult getProgressByPathId(Long pathId);
    
    // 观看人数
    ResponseResult incrementViewCount(Long unitId);
}