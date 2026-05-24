package com.learning.service.impl.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningPath;
import com.learning.mapper.LearningPathMapper;
import com.learning.mapper.LearningUnitMapper;
import com.learning.service.admin.AdminPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.learning.entity.PathStage;

/**
 * 管理员学习路线服务实现类
 * 负责管理员对学习路线的创建、更新、删除和查询等操作
 * 引用文件：c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\common\ResponseResult.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\LearningPath.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\mapper\LearningPathMapper.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\mapper\LearningUnitMapper.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\mapper\PathStageMapper.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\service\admin\AdminPathService.java
 */
@Service
public class AdminPathServiceImpl implements AdminPathService {

    @Autowired
    private LearningPathMapper learningPathMapper;

    @Autowired
    private LearningUnitMapper learningUnitMapper;

    @Autowired
    private com.learning.mapper.PathStageMapper pathStageMapper;

    @Override
    @Transactional
    @CacheEvict(value = {"learningPaths", "learningPathStages"}, allEntries = true)
    public ResponseResult createPath(LearningPath path) {
        // 将难度等级转换为大写
        if (path.getDifficulty() != null) {
            path.setDifficulty(path.getDifficulty().toUpperCase());
        }
        path.setStatus("PUBLISHED");
        path.setCreatedAt(LocalDateTime.now());
        path.setUpdatedAt(LocalDateTime.now());
        if (path.getIsVipOnly() == null) {
            path.setIsVipOnly(0);
        }
        if (path.getSortOrder() == null) {
            path.setSortOrder(0);
        }
        learningPathMapper.insert(path);

        // 保存阶段（小节）数据
        if (path.getStages() != null && !path.getStages().isEmpty()) {
            for (PathStage stage : path.getStages()) {
                stage.setId(null); // 重置ID，让数据库自动生成
                stage.setPathId(path.getId());
                stage.setCreatedAt(LocalDateTime.now());
                if (stage.getDurationDays() == null) {
                    stage.setDurationDays(7);
                }
                if (stage.getSortOrder() == null) {
                    stage.setSortOrder(0);
                }
                if (stage.getIsLocked() == null) {
                    stage.setIsLocked(0);
                }
                pathStageMapper.insert(stage);
            }
        }

        return ResponseResult.success("路线创建成功");
    }

    @Override
    @Transactional
    @CacheEvict(value = {"learningPaths", "learningPathStages"}, allEntries = true)
    public ResponseResult updatePath(LearningPath path) {
        // 获取现有数据用于填充 null 字段
        LearningPath existing = learningPathMapper.selectById(path.getId());
        if (existing == null) {
            return ResponseResult.error("路线不存在");
        }

        // 将难度等级转换为大写
        if (path.getDifficulty() != null) {
            path.setDifficulty(path.getDifficulty().toUpperCase());
        }
        // 补充前端未传的 NOT NULL 字段
        if (path.getStatus() == null || path.getStatus().isEmpty()) {
            path.setStatus(existing.getStatus() != null ? existing.getStatus() : "PUBLISHED");
        }
        if (path.getIsVipOnly() == null) {
            path.setIsVipOnly(existing.getIsVipOnly() != null ? existing.getIsVipOnly() : 0);
        }
        if (path.getSortOrder() == null) {
            path.setSortOrder(existing.getSortOrder() != null ? existing.getSortOrder() : 0);
        }
        path.setUpdatedAt(LocalDateTime.now());
        learningPathMapper.update(path);

        // 更新阶段（小节）数据：先删除旧的，再插入新的
        if (path.getStages() != null) {
            pathStageMapper.deleteByPathId(path.getId());
            for (PathStage stage : path.getStages()) {
                if (stage.getTitle() == null || stage.getTitle().trim().isEmpty()) {
                    continue;
                }
                stage.setId(null); // 重置ID，让数据库自动生成
                stage.setPathId(path.getId());
                stage.setCreatedAt(LocalDateTime.now());
                if (stage.getDurationDays() == null) {
                    stage.setDurationDays(7);
                }
                if (stage.getSortOrder() == null) {
                    stage.setSortOrder(0);
                }
                if (stage.getIsLocked() == null) {
                    stage.setIsLocked(0);
                }
                pathStageMapper.insert(stage);
            }
        }

        return ResponseResult.success("路线更新成功");
    }

    @Override
    @Transactional
    @CacheEvict(value = {"learningPaths", "learningPathStages"}, allEntries = true)
    public ResponseResult deletePath(Long id) {
        // 先删除该路线下的所有学习单元
        learningUnitMapper.deleteByPathId(id);
        // 再删除该路线下的所有阶段
        pathStageMapper.deleteByPathId(id);
        // 最后删除路线本身
        learningPathMapper.delete(id);
        return ResponseResult.success("路线删除成功");
    }

    @Override
    public ResponseResult getPathList(Integer page, Integer size, String keyword, String status, String category) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 转换category为Long类型
        Long categoryId = null;
        if (category != null && !category.isEmpty()) {
            try {
                categoryId = Long.parseLong(category);
            } catch (NumberFormatException e) {
                // 如果转换失败，设置为null，不进行category过滤
                categoryId = null;
            }
        }
        
        // 获取路线列表
        List<LearningPath> paths = learningPathMapper.selectList(offset, size, keyword, status, categoryId);
        
        // 获取总数
        int total = learningPathMapper.count(keyword, status, categoryId);
        
        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", paths);
        
        return ResponseResult.success(result);
    }

    @Override
    public ResponseResult getPathById(Long id) {
        LearningPath path = learningPathMapper.selectById(id);
        if (path == null) {
            return ResponseResult.error("路线不存在");
        }
        return ResponseResult.success(path);
    }
}