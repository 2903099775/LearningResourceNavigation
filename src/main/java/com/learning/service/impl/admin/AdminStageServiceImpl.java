package com.learning.service.impl.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.PathStage;
import com.learning.mapper.LearningUnitMapper;
import com.learning.mapper.PathStageMapper;
import com.learning.service.admin.AdminStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员学习阶段服务实现类
 * 负责管理员对学习阶段的创建、更新、删除和查询等操作
 */
@Service
public class AdminStageServiceImpl implements AdminStageService {

    @Autowired
    private PathStageMapper pathStageMapper;

    @Autowired
    private LearningUnitMapper learningUnitMapper;

    @Override
    public ResponseResult getStagesByPathId(Long pathId) {
        List<PathStage> stages = pathStageMapper.findByPathId(pathId);
        // 为每个阶段填充下属学习单元数量
        for (PathStage stage : stages) {
            int unitCount = learningUnitMapper.countByStageId(stage.getId());
            stage.setUnits(null); // 管理列表不需要加载完整单元数据
        }
        return ResponseResult.success(stages);
    }

    @Override
    public ResponseResult getStageById(Long id) {
        PathStage stage = pathStageMapper.findById(id);
        if (stage == null) {
            return ResponseResult.error("阶段不存在");
        }
        return ResponseResult.success(stage);
    }

    @Override
    @CacheEvict(value = {"learningPathStages", "learningPaths"}, allEntries = true)
    public ResponseResult createStage(PathStage stage) {
        stage.setCreatedAt(LocalDateTime.now());
        if (stage.getSortOrder() == null) {
            stage.setSortOrder(1);
        }
        if (stage.getIsLocked() == null) {
            stage.setIsLocked(0);
        }
        pathStageMapper.insert(stage);
        return ResponseResult.success("阶段创建成功", stage);
    }

    @Override
    @CacheEvict(value = {"learningPathStages", "learningPaths"}, allEntries = true)
    public ResponseResult updateStage(PathStage stage) {
        PathStage existing = pathStageMapper.findById(stage.getId());
        if (existing == null) {
            return ResponseResult.error("阶段不存在");
        }
        pathStageMapper.update(stage);
        return ResponseResult.success("阶段更新成功");
    }

    @Override
    @Transactional
    @CacheEvict(value = {"learningPathStages", "learningPaths"}, allEntries = true)
    public ResponseResult deleteStage(Long id) {
        PathStage stage = pathStageMapper.findById(id);
        if (stage == null) {
            return ResponseResult.error("阶段不存在");
        }
        // 级联删除阶段下的学习单元
        learningUnitMapper.deleteByStageId(id);
        pathStageMapper.delete(id);
        return ResponseResult.success("阶段删除成功");
    }

    @Override
    @Transactional
    @CacheEvict(value = {"learningPathStages", "learningPaths"}, allEntries = true)
    public ResponseResult batchSaveStages(Long pathId, List<PathStage> stages) {
        if (pathId == null || stages == null) {
            return ResponseResult.error("参数错误");
        }

        // 查询数据库中已有的阶段
        List<PathStage> existingStages = pathStageMapper.findByPathId(pathId);
        Map<Long, PathStage> existingMap = existingStages.stream()
                .collect(Collectors.toMap(PathStage::getId, s -> s));

        // 收集前端传来的已有阶段ID
        List<Long> frontendIds = stages.stream()
                .filter(s -> s.getId() != null)
                .map(PathStage::getId)
                .collect(Collectors.toList());

        // 找出需要删除的阶段（数据库中有但前端没有的）
        for (PathStage existing : existingStages) {
            if (!frontendIds.contains(existing.getId())) {
                // 级联删除阶段下的学习单元
                learningUnitMapper.deleteByStageId(existing.getId());
                pathStageMapper.delete(existing.getId());
            }
        }

        // 新增或更新阶段
        for (int i = 0; i < stages.size(); i++) {
            PathStage stage = stages.get(i);
            stage.setPathId(pathId);
            stage.setSortOrder(i + 1);

            if (stage.getId() == null) {
                // 新增
                stage.setCreatedAt(LocalDateTime.now());
                if (stage.getIsLocked() == null) {
                    stage.setIsLocked(0);
                }
                pathStageMapper.insert(stage);
            } else if (existingMap.containsKey(stage.getId())) {
                // 更新
                pathStageMapper.update(stage);
            }
        }

        return ResponseResult.success("小节保存成功");
    }
}
