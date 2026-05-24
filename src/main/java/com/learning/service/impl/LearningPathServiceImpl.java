package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningPath;
import com.learning.entity.LearningUnit;
import com.learning.entity.LearningProgress;
import com.learning.entity.PathStage;
import com.learning.entity.User;
import com.learning.mapper.LearningPathMapper;
import com.learning.mapper.LearningUnitMapper;
import com.learning.mapper.LearningProgressMapper;
import com.learning.mapper.UserMapper;
import com.learning.mapper.PathStageMapper;
import com.learning.service.LearningPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 学习路线服务实现类
 * 负责处理学习路线的查询、获取详情、获取阶段和进度等方法的具体实现
 * 引用文件：c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\common\ResponseResult.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\LearningPath.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\LearningUnit.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\LearningProgress.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\PathStage.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\User.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\mapper\LearningPathMapper.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\mapper\LearningUnitMapper.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\mapper\LearningProgressMapper.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\mapper\UserMapper.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\mapper\PathStageMapper.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\service\LearningPathService.java
 */
@Service
public class LearningPathServiceImpl implements LearningPathService {

    private static final Logger log = LoggerFactory.getLogger(LearningPathServiceImpl.class);

    @Autowired
    private LearningPathMapper learningPathMapper;

    @Autowired
    private LearningUnitMapper learningUnitMapper;

    @Autowired
    private PathStageMapper pathStageMapper;

    @Autowired
    private LearningProgressMapper learningProgressMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Cacheable(value = "learningPaths", key = "'all'")
    public ResponseResult getPaths() {
        List<LearningPath> paths = learningPathMapper.findAll();
        return ResponseResult.success(paths);
    }

    @Override
    @Cacheable(value = "learningPaths", key = "#id")
    public ResponseResult getPathById(Long id) {
        LearningPath path = learningPathMapper.findById(id);
        if (path == null) {
            return ResponseResult.error(404, "路线不存在");
        }
        return ResponseResult.success(path);
    }

    @Override
    @Cacheable(value = "learningPathStages", key = "#id")
    public ResponseResult getPathStages(Long id) {
        LearningPath path = learningPathMapper.findById(id);
        if (path == null) {
            return ResponseResult.error(404, "路线不存在");
        }
        List<PathStage> stages = pathStageMapper.findByPathId(id);
        for (PathStage stage : stages) {
            List<LearningUnit> units = learningUnitMapper.findByStageId(stage.getId());
            stage.setUnits(units);
        }
        return ResponseResult.success(stages);
    }

    @Override
    public ResponseResult getPathProgress(Long id) {
        LearningPath path = learningPathMapper.findById(id);
        if (path == null) {
            return ResponseResult.error(404, "路线不存在");
        }
        
        List<LearningUnit> units = learningUnitMapper.findByPathId(id);
        Long userId = getCurrentUserId();
        
        long totalUnits = units.size();
        long completedUnits = 0;
        long inProgressUnits = 0;
        
        if (totalUnits > 0) {
            // 使用批量查询替代单个查询，减少数据库查询次数
            List<LearningProgress> progressList = learningProgressMapper.findByUserAndPath(userId, id);
            
            // 创建学习单元ID到进度状态的映射
            Map<Long, String> progressMap = new HashMap<>();
            for (LearningProgress progress : progressList) {
                progressMap.put(progress.getUnitId(), progress.getStatus());
            }
            
            // 统计完成和进行中的单元数量
            for (LearningUnit unit : units) {
                String status = progressMap.get(unit.getId());
                if (status != null) {
                    if ("COMPLETED".equals(status)) {
                        completedUnits++;
                    } else if ("IN_PROGRESS".equals(status)) {
                        inProgressUnits++;
                    }
                }
            }
        }
        
        Map<String, Object> progressInfo = new HashMap<>();
        progressInfo.put("pathId", id);
        progressInfo.put("pathName", path.getTitle());
        progressInfo.put("totalUnits", totalUnits);
        progressInfo.put("completedUnits", completedUnits);
        progressInfo.put("inProgressUnits", inProgressUnits);
        progressInfo.put("completionRate", totalUnits > 0 ? String.format("%.1f%%", (completedUnits * 100.0 / totalUnits)) : "0%");
        
        return ResponseResult.success(progressInfo);
    }
    
    @Override
    public ResponseResult searchPaths(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseResult.error("搜索关键词不能为空");
        }
        
        List<LearningPath> paths = learningPathMapper.searchByKeyword(keyword.trim());
        return ResponseResult.success(paths);
    }
    
    @Override
    public ResponseResult getHotPaths(int limit) {
        if (limit <= 0) {
            limit = 5; // 默认返回5条热门路线
        }
        
        try {
            List<LearningPath> hotPaths = learningPathMapper.findHotPaths(limit);
            return ResponseResult.success(hotPaths);
        } catch (Exception e) {
            log.error("获取热门路线失败: ", e);
            return ResponseResult.error("获取热门路线失败");
        }
    }

    @Override
    public ResponseResult getPathsByCategory(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            return ResponseResult.error("分类ID不能为空");
        }
        
        try {
            List<LearningPath> paths = learningPathMapper.findByCategory(categoryId);
            return ResponseResult.success(paths);
        } catch (Exception e) {
            log.error("根据分类获取学习路线失败: ", e);
            return ResponseResult.error("根据分类获取学习路线失败");
        }
    }

    @Override
    public ResponseResult getPathsBySubcategory(Long subcategoryId) {
        if (subcategoryId == null || subcategoryId <= 0) {
            return ResponseResult.error("子分类ID不能为空");
        }
        
        try {
            List<LearningPath> paths = learningPathMapper.findBySubcategory(subcategoryId);
            return ResponseResult.success(paths);
        } catch (Exception e) {
            log.error("根据子分类获取学习路线失败: ", e);
            return ResponseResult.error("根据子分类获取学习路线失败");
        }
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
}