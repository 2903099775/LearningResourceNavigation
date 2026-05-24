package com.learning.controller.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.PathStage;
import com.learning.service.admin.AdminStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员学习阶段控制器
 * 负责处理管理员对学习阶段的API请求，包括创建、更新、删除和查询等
 */
@RestController
@RequestMapping("/api/admin/stages")
public class AdminStageController {

    @Autowired
    private AdminStageService adminStageService;

    /**
     * 获取指定路线下的所有阶段（小节）
     */
    @GetMapping("/path/{pathId}")
    public ResponseResult getStagesByPathId(@PathVariable Long pathId) {
        return adminStageService.getStagesByPathId(pathId);
    }

    /**
     * 获取阶段详情
     */
    @GetMapping("/{id}")
    public ResponseResult getStageById(@PathVariable Long id) {
        return adminStageService.getStageById(id);
    }

    /**
     * 创建阶段
     */
    @PostMapping
    public ResponseResult createStage(@RequestBody PathStage stage) {
        return adminStageService.createStage(stage);
    }

    /**
     * 更新阶段
     */
    @PutMapping("/{id}")
    public ResponseResult updateStage(@PathVariable Long id, @RequestBody PathStage stage) {
        stage.setId(id);
        return adminStageService.updateStage(stage);
    }

    /**
     * 删除阶段
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteStage(@PathVariable Long id) {
        return adminStageService.deleteStage(id);
    }

    /**
     * 批量保存阶段（用于路线编辑时同步保存小节）
     * 请求体格式：{ "pathId": 1, "stages": [{ "id": null, "title": "xxx", ... }, ...] }
     */
    @PostMapping("/batch")
    public ResponseResult batchSaveStages(@RequestBody Map<String, Object> body) {
        Long pathId = Long.valueOf(body.get("pathId").toString());
        @SuppressWarnings("unchecked")
        List<PathStage> stages = (List<PathStage>) body.get("stages");
        return adminStageService.batchSaveStages(pathId, stages);
    }
}
