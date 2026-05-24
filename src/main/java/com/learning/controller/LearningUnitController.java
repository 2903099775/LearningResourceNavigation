package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningUnit;
import com.learning.service.LearningUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学习单元控制器
 * 负责处理学习单元相关的API请求，包括CRUD操作、高级查询、统计、互动功能和进度跟踪等
 * 引用文件：c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\common\ResponseResult.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\LearningUnit.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\service\LearningUnitService.java
 */
@RestController
@RequestMapping("/api/units")
public class LearningUnitController {

    @Autowired
    private LearningUnitService learningUnitService;

    // 基本CRUD操作
    @GetMapping("/{id}")
    public ResponseResult<Object> getUnitById(@PathVariable Long id) {
        return learningUnitService.getUnitById(id);
    }

    @GetMapping("/stage/{stageId}")
    public ResponseResult<Object> getUnitsByStageId(@PathVariable Long stageId) {
        return learningUnitService.getUnitsByStageId(stageId);
    }

    @GetMapping("/path/{pathId}")
    public ResponseResult<Object> getUnitsByPathId(@PathVariable Long pathId) {
        return learningUnitService.getUnitsByPathId(pathId);
    }

    @PostMapping
    public ResponseResult<Object> createUnit(@RequestBody LearningUnit learningUnit) {
        return learningUnitService.createUnit(learningUnit);
    }

    @PutMapping("/{id}")
    public ResponseResult<Object> updateUnit(@PathVariable Long id, @RequestBody LearningUnit learningUnit) {
        return learningUnitService.updateUnit(id, learningUnit);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Object> deleteUnit(@PathVariable Long id) {
        return learningUnitService.deleteUnit(id);
    }

    // 高级查询
    @GetMapping("/type/{type}")
    public ResponseResult<Object> getUnitsByType(@PathVariable String type) {
        return learningUnitService.getUnitsByType(type);
    }

    @GetMapping("/platform/{platform}")
    public ResponseResult<Object> getUnitsByPlatform(@PathVariable String platform) {
        return learningUnitService.getUnitsByPlatform(platform);
    }

    @GetMapping("/vip-only")
    public ResponseResult<Object> getVipOnlyUnits() {
        return learningUnitService.getVipOnlyUnits();
    }

    @GetMapping("/search")
    public ResponseResult<Object> searchUnitsByTitle(@RequestParam String keyword) {
        return learningUnitService.searchUnitsByTitle(keyword);
    }

    // 统计相关
    @GetMapping("/count/stage/{stageId}")
    public ResponseResult<Object> countUnitsByStageId(@PathVariable Long stageId) {
        return learningUnitService.countUnitsByStageId(stageId);
    }

    @GetMapping("/count/path/{pathId}")
    public ResponseResult<Object> countUnitsByPathId(@PathVariable Long pathId) {
        return learningUnitService.countUnitsByPathId(pathId);
    }

    @GetMapping("/count/total")
    public ResponseResult<Object> countTotalUnits() {
        return learningUnitService.countTotalUnits();
    }

    // 互动功能
    @PostMapping("/{id}/comments")
    public ResponseResult<Object> addComment(@PathVariable Long id, @RequestBody CommentRequest commentRequest) {
        return learningUnitService.addComment(id, commentRequest.getContent());
    }

    @GetMapping("/{id}/comments")
    public ResponseResult<Object> getCommentsByUnitId(@PathVariable Long id) {
        return learningUnitService.getCommentsByUnitId(id);
    }

    @PostMapping("/{id}/favorites")
    public ResponseResult<Object> addFavorite(@PathVariable Long id) {
        return learningUnitService.addFavorite(id);
    }

    @DeleteMapping("/{id}/favorites")
    public ResponseResult<Object> removeFavorite(@PathVariable Long id) {
        return learningUnitService.removeFavorite(id);
    }

    @GetMapping("/{id}/favorites/status")
    public ResponseResult<Object> checkFavoriteStatus(@PathVariable Long id) {
        return learningUnitService.checkFavoriteStatus(id);
    }

    // 进度跟踪
    @PostMapping("/{id}/progress")
    public ResponseResult<Object> updateProgress(@PathVariable Long id, @RequestBody ProgressRequest progressRequest) {
        return learningUnitService.updateProgress(id, progressRequest.getStatus(), progressRequest.getStudyDuration());
    }

    @GetMapping("/{id}/progress")
    public ResponseResult<Object> getProgressByUnitId(@PathVariable Long id) {
        return learningUnitService.getProgressByUnitId(id);
    }

    @GetMapping("/progress/path/{pathId}")
    public ResponseResult<Object> getProgressByPathId(@PathVariable Long pathId) {
        return learningUnitService.getProgressByPathId(pathId);
    }

    // 观看人数
    @PostMapping("/{id}/view")
    public ResponseResult<Object> incrementViewCount(@PathVariable Long id) {
        return learningUnitService.incrementViewCount(id);
    }

    // 请求体类
    public static class CommentRequest {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class ProgressRequest {
        private String status;
        private Integer studyDuration;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getStudyDuration() {
            return studyDuration;
        }

        public void setStudyDuration(Integer studyDuration) {
            this.studyDuration = studyDuration;
        }
    }
}