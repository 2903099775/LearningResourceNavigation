package com.learning.mapper;

import com.learning.entity.MonthlyActiveUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 月度活跃用户统计Mapper接口
 * 提供月度活跃用户数据的数据库操作方法
 */
@Mapper
public interface MonthlyActiveUserMapper {

    /**
     * 获取指定月份的活跃用户排行榜
     * @param yearMonth 年月，格式：YYYY-MM
     * @param limit 返回数量限制
     * @return 活跃用户列表（按综合得分降序排列）
     */
    List<MonthlyActiveUser> getMonthlyRanking(@Param("yearMonth") String yearMonth, @Param("limit") int limit);

    /**
     * 统计并更新/插入用户的月度活跃数据
     * @param userId 用户ID
     * @param yearMonth 年月
     */
    void upsertUserActivity(@Param("userId") Long userId, @Param("yearMonth") String yearMonth);

    /**
     * 获取当前月份所有用户的活跃统计数据（用于批量更新排名）
     * @param yearMonth 年月
     * @return 活跃用户列表
     */
    List<MonthlyActiveUser> getMonthlyStats(@Param("yearMonth") String yearMonth);
}