package com.learning.service;

import com.learning.common.ResponseResult;
import com.learning.entity.Favorite;

/**
 * 收藏服务接口
 * 定义收藏的添加、获取和移除等方法
 * 引用文件：com.learning.common.ResponseResult, com.learning.entity.Favorite
 */
public interface FavoriteService {
    ResponseResult addFavorite(Favorite favorite);
    
    ResponseResult getFavorites();
    
    ResponseResult removeFavorite(Long id);
    
    ResponseResult removeFavoriteByTarget(String targetType, Long targetId);
}