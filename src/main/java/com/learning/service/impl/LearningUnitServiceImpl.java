package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.entity.Comment;
import com.learning.entity.LearningPath;
import com.learning.entity.LearningUnit;
import com.learning.mapper.*;
import com.learning.entity.PathStage;
import com.learning.service.LearningUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LearningUnitServiceImpl implements LearningUnitService {

    @Autowired
    private LearningUnitMapper learningUnitMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private LearningProgressMapper learningProgressMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PathStageMapper pathStageMapper;

    @Autowired
    private LearningPathMapper learningPathMapper;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String principal = authentication.getName();
            try {
                return Long.parseLong(principal);
            } catch (NumberFormatException e) {
                try {
                    com.learning.entity.User user = userMapper.findByUsername(principal);
                    if (user != null) {
                        return user.getId();
                    }
                } catch (Exception ex) {
                }
                return null;
            }
        }
        return null;
    }

    private boolean isVipUser(Long userId) {
        if (userId == null) {
            return false;
        }
        return userMapper.isVipUser(userId);
    }

    @Override
    public ResponseResult getUnitById(Long id) {
        LearningUnit unit = learningUnitMapper.findById(id);
        if (unit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        Long userId = getCurrentUserId();
        if (unit.getIsVipOnly() != null && unit.getIsVipOnly() == 1 && !isVipUser(userId)) {
            return ResponseResult.error("该学习单元仅对VIP用户开放");
        }

        if (userId != null) {
            unit.setFavorite(favoriteMapper.existsByUserAndTarget(userId, "UNIT", id));

            var progress = learningProgressMapper.findByUserAndUnit(userId, id);
            if (progress != null) {
                unit.setProgressStatus(progress.getStatus());
                unit.setStudyDuration(progress.getStudyDuration());
            }
        }

        // 获取阶段和路线信息用于面包屑导航
        PathStage stage = pathStageMapper.findById(unit.getStageId());
        if (stage != null) {
            unit.setStageName(stage.getTitle());
            unit.setPathId(stage.getPathId());
            LearningPath path = learningPathMapper.findById(stage.getPathId());
            if (path != null) {
                unit.setPathName(path.getTitle());
            }
        }

        return ResponseResult.success(unit);
    }

    @Override
    public ResponseResult getUnitsByStageId(Long stageId) {
        List<LearningUnit> units = learningUnitMapper.findByStageId(stageId);
        Long userId = getCurrentUserId();

        for (LearningUnit unit : units) {
            if (unit.getIsVipOnly() != null && unit.getIsVipOnly() == 1 && !isVipUser(userId)) {
                unit.setTitle(unit.getTitle() + " (VIP专属)");
                unit.setExternalUrl(null);
            }

            if (userId != null) {
                unit.setFavorite(favoriteMapper.existsByUserAndTarget(userId, "UNIT", unit.getId()));
                var progress = learningProgressMapper.findByUserAndUnit(userId, unit.getId());
                if (progress != null) {
                    unit.setProgressStatus(progress.getStatus());
                    unit.setStudyDuration(progress.getStudyDuration());
                }
            }
        }

        return ResponseResult.success(units);
    }

    @Override
    public ResponseResult getUnitsByPathId(Long pathId) {
        List<LearningUnit> units = learningUnitMapper.findByPathId(pathId);
        Long userId = getCurrentUserId();

        for (LearningUnit unit : units) {
            if (unit.getIsVipOnly() != null && unit.getIsVipOnly() == 1 && !isVipUser(userId)) {
                unit.setTitle(unit.getTitle() + " (VIP专属)");
                unit.setExternalUrl(null);
            }

            if (userId != null) {
                unit.setFavorite(favoriteMapper.existsByUserAndTarget(userId, "UNIT", unit.getId()));
                var progress = learningProgressMapper.findByUserAndUnit(userId, unit.getId());
                if (progress != null) {
                    unit.setProgressStatus(progress.getStatus());
                    unit.setStudyDuration(progress.getStudyDuration());
                }
            }
        }

        return ResponseResult.success(units);
    }

    @Override
    @Transactional
    public ResponseResult createUnit(LearningUnit learningUnit) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        learningUnit.setCreatedAt(LocalDateTime.now());
        learningUnit.setStatus(1);
        learningUnitMapper.insert(learningUnit);

        return ResponseResult.success("学习单元创建成功", learningUnit);
    }

    @Override
    @Transactional
    public ResponseResult updateUnit(Long id, LearningUnit learningUnit) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        LearningUnit existingUnit = learningUnitMapper.findById(id);
        if (existingUnit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        learningUnit.setId(id);
        learningUnitMapper.update(learningUnit);

        return ResponseResult.success("学习单元更新成功", learningUnit);
    }

    @Override
    @Transactional
    public ResponseResult deleteUnit(Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        LearningUnit existingUnit = learningUnitMapper.findById(id);
        if (existingUnit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        learningUnitMapper.delete(id);

        return ResponseResult.success("学习单元删除成功");
    }

    @Override
    public ResponseResult getUnitsByType(String type) {
        List<LearningUnit> units = learningUnitMapper.findByType(type);
        Long userId = getCurrentUserId();

        for (LearningUnit unit : units) {
            if (unit.getIsVipOnly() != null && unit.getIsVipOnly() == 1 && !isVipUser(userId)) {
                unit.setTitle(unit.getTitle() + " (VIP专属)");
                unit.setExternalUrl(null);
            }

            if (userId != null) {
                unit.setFavorite(favoriteMapper.existsByUserAndTarget(userId, "UNIT", unit.getId()));
                var progress = learningProgressMapper.findByUserAndUnit(userId, unit.getId());
                if (progress != null) {
                    unit.setProgressStatus(progress.getStatus());
                    unit.setStudyDuration(progress.getStudyDuration());
                }
            }
        }

        return ResponseResult.success(units);
    }

    @Override
    public ResponseResult getUnitsByPlatform(String platform) {
        List<LearningUnit> units = learningUnitMapper.findByPlatform(platform);
        Long userId = getCurrentUserId();

        for (LearningUnit unit : units) {
            if (unit.getIsVipOnly() != null && unit.getIsVipOnly() == 1 && !isVipUser(userId)) {
                unit.setTitle(unit.getTitle() + " (VIP专属)");
                unit.setExternalUrl(null);
            }

            if (userId != null) {
                unit.setFavorite(favoriteMapper.existsByUserAndTarget(userId, "UNIT", unit.getId()));
                var progress = learningProgressMapper.findByUserAndUnit(userId, unit.getId());
                if (progress != null) {
                    unit.setProgressStatus(progress.getStatus());
                    unit.setStudyDuration(progress.getStudyDuration());
                }
            }
        }

        return ResponseResult.success(units);
    }

    @Override
    public ResponseResult getVipOnlyUnits() {
        List<LearningUnit> units = learningUnitMapper.findVipOnly();
        Long userId = getCurrentUserId();

        for (LearningUnit unit : units) {
            if (!isVipUser(userId)) {
                unit.setTitle(unit.getTitle() + " (VIP专属)");
                unit.setExternalUrl(null);
            }

            if (userId != null) {
                unit.setFavorite(favoriteMapper.existsByUserAndTarget(userId, "UNIT", unit.getId()));
                var progress = learningProgressMapper.findByUserAndUnit(userId, unit.getId());
                if (progress != null) {
                    unit.setProgressStatus(progress.getStatus());
                    unit.setStudyDuration(progress.getStudyDuration());
                }
            }
        }

        return ResponseResult.success(units);
    }

    @Override
    public ResponseResult searchUnitsByTitle(String keyword) {
        List<LearningUnit> units = learningUnitMapper.searchByTitle(keyword);
        Long userId = getCurrentUserId();

        for (LearningUnit unit : units) {
            if (unit.getIsVipOnly() != null && unit.getIsVipOnly() == 1 && !isVipUser(userId)) {
                unit.setTitle(unit.getTitle() + " (VIP专属)");
                unit.setExternalUrl(null);
            }

            if (userId != null) {
                unit.setFavorite(favoriteMapper.existsByUserAndTarget(userId, "UNIT", unit.getId()));
                var progress = learningProgressMapper.findByUserAndUnit(userId, unit.getId());
                if (progress != null) {
                    unit.setProgressStatus(progress.getStatus());
                    unit.setStudyDuration(progress.getStudyDuration());
                }
            }
        }

        return ResponseResult.success(units);
    }

    @Override
    public ResponseResult countUnitsByStageId(Long stageId) {
        Integer count = learningUnitMapper.countByStageId(stageId);
        return ResponseResult.success(count);
    }

    @Override
    public ResponseResult countUnitsByPathId(Long pathId) {
        Integer count = learningUnitMapper.countByPathId(pathId);
        return ResponseResult.success(count);
    }

    @Override
    public ResponseResult countTotalUnits() {
        Integer count = learningUnitMapper.countTotal();
        return ResponseResult.success(count);
    }

    @Override
    @Transactional
    public ResponseResult addComment(Long unitId, String content) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        LearningUnit unit = learningUnitMapper.findById(unitId);
        if (unit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        if (unit.getIsVipOnly() == 1 && !isVipUser(userId)) {
            return ResponseResult.error("该学习单元仅对VIP用户开放");
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setTargetType("UNIT");
        comment.setTargetId(unitId);
        comment.setContent(content);
        comment.setCreatedAt(java.time.LocalDateTime.now());
        commentMapper.insert(comment);

        return ResponseResult.success("评论添加成功");
    }

    @Override
    public ResponseResult getCommentsByUnitId(Long unitId) {
        LearningUnit unit = learningUnitMapper.findById(unitId);
        if (unit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        Long userId = getCurrentUserId();
        if (unit.getIsVipOnly() != null && unit.getIsVipOnly() == 1 && !isVipUser(userId)) {
            return ResponseResult.error("该学习单元仅对VIP用户开放");
        }

        var comments = commentMapper.findByTarget("UNIT", unitId);
        return ResponseResult.success(comments);
    }

    @Override
    @Transactional
    public ResponseResult addFavorite(Long unitId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        LearningUnit unit = learningUnitMapper.findById(unitId);
        if (unit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        if (favoriteMapper.existsByUserAndTarget(userId, "UNIT", unitId)) {
            return ResponseResult.error("已经收藏过该学习单元");
        }

        favoriteMapper.insertWithParams(userId, "UNIT", unitId);

        return ResponseResult.success("收藏成功");
    }

    @Override
    @Transactional
    public ResponseResult removeFavorite(Long unitId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        LearningUnit unit = learningUnitMapper.findById(unitId);
        if (unit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        if (!favoriteMapper.existsByUserAndTarget(userId, "UNIT", unitId)) {
            return ResponseResult.error("尚未收藏该学习单元");
        }

        favoriteMapper.deleteByUserAndTarget(userId, "UNIT", unitId);

        return ResponseResult.success("取消收藏成功");
    }

    @Override
    public ResponseResult checkFavoriteStatus(Long unitId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.success(false);
        }

        LearningUnit unit = learningUnitMapper.findById(unitId);
        if (unit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        boolean isFavorite = favoriteMapper.existsByUserAndTarget(userId, "UNIT", unitId);
        return ResponseResult.success(isFavorite);
    }

    @Override
    @Transactional
    public ResponseResult updateProgress(Long unitId, String status, Integer studyDuration) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        LearningUnit unit = learningUnitMapper.findById(unitId);
        if (unit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        if (unit.getIsVipOnly() == 1 && !isVipUser(userId)) {
            return ResponseResult.error("该学习单元仅对VIP用户开放");
        }

        var existingProgress = learningProgressMapper.findByUserAndUnit(userId, unitId);
        Long pathId = null;
        PathStage stage = pathStageMapper.findById(unit.getStageId());
        if (stage != null) {
            pathId = stage.getPathId();
        }

        if (existingProgress != null) {
            existingProgress.setStatus(status);
            existingProgress.setStudyDuration(studyDuration);
            if ("COMPLETED".equals(status)) {
                existingProgress.setCompleteTime(LocalDateTime.now());
            }
            learningProgressMapper.update(existingProgress);
        } else {
            if (pathId != null) {
                learningProgressMapper.insertWithParams(userId, unitId, pathId, status, studyDuration);
            } else {
                return ResponseResult.error("无法确定学习路线，进度更新失败");
            }
        }

        return ResponseResult.success("进度更新成功");
    }

    @Override
    public ResponseResult getProgressByUnitId(Long unitId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        LearningUnit unit = learningUnitMapper.findById(unitId);
        if (unit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        if (unit.getIsVipOnly() == 1 && !isVipUser(userId)) {
            return ResponseResult.error("该学习单元仅对VIP用户开放");
        }

        var progress = learningProgressMapper.findByUserAndUnit(userId, unitId);
        return ResponseResult.success(progress);
    }

    @Override
    public ResponseResult getProgressByPathId(Long pathId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }

        var progressList = learningProgressMapper.findByUserAndPath(userId, pathId);
        return ResponseResult.success(progressList);
    }

    @Override
    @Transactional
    public ResponseResult incrementViewCount(Long unitId) {
        LearningUnit unit = learningUnitMapper.findById(unitId);
        if (unit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        learningUnitMapper.incrementViewCount(unitId);
        return ResponseResult.success("观看人数增加成功");
    }
}
