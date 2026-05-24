package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.entity.Favorite;
import com.learning.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 收藏控制器
 * 负责处理收藏相关的API请求，包括添加收藏、获取收藏列表和移除收藏等操作
 * 引用文件：com.learning.common.ResponseResult, com.learning.entity.Favorite, com.learning.service.FavoriteService
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping
    public ResponseResult<String> addFavorite(@RequestBody Favorite favorite) {
        return favoriteService.addFavorite(favorite);
    }

    @GetMapping
    public ResponseResult<Object> getFavorites() {
        return favoriteService.getFavorites();
    }

    @DeleteMapping("/{id}")
    public ResponseResult<String> removeFavorite(@PathVariable Long id) {
        return favoriteService.removeFavorite(id);
    }

    /**
     * 按目标类型和ID移除收藏（前端不知道收藏记录ID时使用）
     */
    @DeleteMapping("/target")
    public ResponseResult<String> removeFavoriteByTarget(@RequestBody Map<String, Object> body) {
        String targetType = (String) body.get("targetType");
        Object targetIdObj = body.get("targetId");
        if (targetType == null || targetIdObj == null) {
            return ResponseResult.error("缺少参数");
        }
        Long targetId = ((Number) targetIdObj).longValue();
        return favoriteService.removeFavoriteByTarget(targetType, targetId);
    }
}