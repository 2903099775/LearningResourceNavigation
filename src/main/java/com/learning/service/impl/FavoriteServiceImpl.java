package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.entity.Favorite;
import com.learning.entity.User;
import com.learning.entity.LearningPath;
import com.learning.entity.LearningUnit;
import com.learning.mapper.FavoriteMapper;
import com.learning.mapper.UserMapper;
import com.learning.mapper.LearningPathMapper;
import com.learning.mapper.LearningUnitMapper;
import com.learning.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LearningPathMapper pathMapper;

    @Autowired
    private LearningUnitMapper unitMapper;

    @Override
    public ResponseResult addFavorite(Favorite favorite) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        if (!isVip(userId)) {
            return ResponseResult.error("VIP会员才可以使用收藏功能，请先开通VIP");
        }
        Favorite existing = favoriteMapper.findByUserAndTarget(userId, favorite.getTargetType(), favorite.getTargetId());
        if (existing != null) {
            return ResponseResult.error("已经收藏过了");
        }
        favorite.setUserId(userId);
        favoriteMapper.insert(favorite);
        return ResponseResult.success("添加收藏成功");
    }

    @Override
    public ResponseResult getFavorites() {
        Long userId = getCurrentUserId();
        List<Favorite> favorites = favoriteMapper.findByUserId(userId);

        for (Favorite favorite : favorites) {
            if ("PATH".equals(favorite.getTargetType())) {
                LearningPath path = pathMapper.findById(favorite.getTargetId());
                if (path != null) {
                    favorite.setPath(path);
                }
            } else if ("UNIT".equals(favorite.getTargetType())) {
                LearningUnit unit = unitMapper.findById(favorite.getTargetId());
                if (unit != null) {
                    favorite.setUnit(unit);
                }
            }
        }

        return ResponseResult.success(favorites);
    }

    @Override
    public ResponseResult removeFavorite(Long id) {
        Favorite favorite = favoriteMapper.findById(id);
        if (favorite == null) {
            return ResponseResult.error("收藏不存在");
        }
        favoriteMapper.delete(id);
        return ResponseResult.success("取消收藏成功");
    }

    @Override
    public ResponseResult removeFavoriteByTarget(String targetType, Long targetId) {
        Long userId = getCurrentUserId();
        Favorite existing = favoriteMapper.findByUserAndTarget(userId, targetType, targetId);
        if (existing == null) {
            return ResponseResult.error("尚未收藏");
        }
        favoriteMapper.delete(existing.getId());
        return ResponseResult.success("取消收藏成功");
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            String username = (String) auth.getPrincipal();
            User user = userMapper.findByUsername(username);
            if (user != null) return user.getId();
        }
        return 1L;
    }

    private boolean isVip(Long userId) {
        if (userId == null) return false;
        User user = userMapper.findById(userId);
        if (user == null) return false;

        // 管理员自动拥有VIP权限
        if ("ADMIN".equals(user.getRole())) {
            return true;
        }

        if (user.getVipExpireDate() == null) return false;
        return user.getVipExpireDate().compareTo(java.time.LocalDate.now()) >= 0;
    }
}
