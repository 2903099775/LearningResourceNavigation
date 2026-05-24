package com.learning.mapper;

import com.learning.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏数据访问接口
 * 负责收藏相关的数据库操作，包括查询、插入和删除等
 */
@Mapper
public interface FavoriteMapper {
    List<Favorite> findByUserId(@Param("userId") Long userId);

    Favorite findById(@Param("id") Long id);

    Favorite findByUserAndTarget(@Param("userId") Long userId,
                                 @Param("targetType") String targetType,
                                 @Param("targetId") Long targetId);

    boolean existsByUserAndTarget(@Param("userId") Long userId,
                                 @Param("targetType") String targetType,
                                 @Param("targetId") Long targetId);

    void insert(Favorite favorite);
    void insertWithParams(@Param("userId") Long userId,
                @Param("targetType") String targetType,
                @Param("targetId") Long targetId);

    void delete(@Param("id") Long id);
    void deleteByUserAndTarget(@Param("userId") Long userId,
                              @Param("targetType") String targetType,
                              @Param("targetId") Long targetId);
    
    int countFavoritesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
