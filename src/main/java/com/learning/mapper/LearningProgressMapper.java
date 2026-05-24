package com.learning.mapper;

import com.learning.entity.LearningProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 学习进度数据访问接口
 * 负责学习进度相关的数据库操作，包括查询、插入、更新和统计等
 */
@Mapper
public interface LearningProgressMapper {
    LearningProgress findByUserAndUnit(@Param("userId") Long userId, @Param("unitId") Long unitId);

    List<LearningProgress> findByUserAndPath(@Param("userId") Long userId, @Param("pathId") Long pathId);

    List<LearningProgress> findByUserId(@Param("userId") Long userId);

    void insert(LearningProgress progress);
    void insertWithParams(@Param("userId") Long userId, @Param("unitId") Long unitId, @Param("pathId") Long pathId, @Param("status") String status, @Param("studyDuration") Integer studyDuration);

    void update(LearningProgress progress);

    void delete(@Param("id") Long id);

    int countCompletedByPath(@Param("userId") Long userId, @Param("pathId") Long pathId);

    int countTotalByPath(@Param("pathId") Long pathId);

    // 记录学习访问：更新最近学习时间和状态
    void updateVisitTime(@Param("userId") Long userId, @Param("unitId") Long unitId);

    // 累加学习时长（每次访问增加指定秒数，默认60秒）
    void incrementStudyDuration(@Param("userId") Long userId, @Param("unitId") Long unitId, @Param("seconds") int seconds);

    // 获取用户对某单元的学习次数
    int countVisitsByUserAndUnit(@Param("userId") Long userId, @Param("unitId") Long unitId);

    // 获取用户所有学习单元的总访问次数
    int countTotalVisits(@Param("userId") Long userId);

    // 获取用户最近一次学习时间
    LearningProgress findLatestByUserId(@Param("userId") Long userId);

    // 统计近N小时内访问某单元的去重用户数
    int countRecentVisitors(@Param("unitId") Long unitId, @Param("hours") int hours);

    // 获取近N小时内访问某单元的用户信息（头像、用户名、访问时间）
    List<Map<String, Object>> findRecentVisitors(@Param("unitId") Long unitId, @Param("hours") int hours);
    
    // 获取用户最近的学习记录
    List<Map<String, Object>> findRecentLearningRecords(@Param("userId") Long userId, @Param("limit") int limit);

    // 更新用户评分
    void updateRating(@Param("userId") Long userId, @Param("unitId") Long unitId, @Param("rating") Integer rating);

    // 更新收藏状态
    void updateFavorite(@Param("userId") Long userId, @Param("unitId") Long unitId, @Param("isFavorite") Integer isFavorite);

    // 获取用户评分
    Integer getRating(@Param("userId") Long userId, @Param("unitId") Long unitId);

    // 获取用户收藏状态
    Integer getFavorite(@Param("userId") Long userId, @Param("unitId") Long unitId);
    
    // 统计指定日期范围内完成的学习单元数
    int countCompletedUnitsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // 统计指定日期范围内的学习进度更新数
    int countProgressUpdatesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // 统计各学习路线的学习人数
    List<Map<String, Object>> countUsersByPath();

    // 获取用户过去7天每日学习时长统计
    List<Map<String, Object>> getDailyStudyDuration(@Param("userId") Long userId);

    // 获取用户过去7天每日学习次数统计
    List<Map<String, Object>> getDailyStudyCount(@Param("userId") Long userId);

    // 获取用户已完成单元总数
    int countCompletedUnitsByUser(@Param("userId") Long userId);

    // 获取用户总学习次数（访问次数）
    int countTotalVisitsByUser(@Param("userId") Long userId);

    // 获取用户总学习时长（秒）
    Integer getTotalStudyDuration(@Param("userId") Long userId);

    // 获取用户已完成路线数
    int countCompletedPathsByUser(@Param("userId") Long userId);

    // 获取用户最后学习日期
    List<LocalDateTime> getStudyDatesByUserId(@Param("userId") Long userId);

    List<Map<String, Object>> countProgressGroupByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
