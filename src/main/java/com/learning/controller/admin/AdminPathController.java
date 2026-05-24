package com.learning.controller.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningPath;
import com.learning.service.admin.AdminPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员学习路线控制器
 * 负责处理管理员对学习路线的API请求，包括创建、更新、删除和查询等
 */
@RestController
@RequestMapping("/api/admin/paths")
public class AdminPathController {

    @Autowired
    private AdminPathService adminPathService;

    @PostMapping
    public ResponseResult createPath(@RequestBody LearningPath path) {
        return adminPathService.createPath(path);
    }

    @PutMapping("/{id}")
    public ResponseResult updatePath(@PathVariable Long id, @RequestBody LearningPath path) {
        path.setId(id);
        return adminPathService.updatePath(path);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deletePath(@PathVariable Long id) {
        return adminPathService.deletePath(id);
    }

    @GetMapping
    public ResponseResult getPathList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {
        return adminPathService.getPathList(page, size, keyword, status, category);
    }

    @GetMapping("/{id}")
    public ResponseResult getPathById(@PathVariable Long id) {
        return adminPathService.getPathById(id);
    }
}