package com.learning.controller.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningUnit;
import com.learning.service.admin.AdminUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员学习单元控制器
 * 负责处理管理员对学习单元的API请求，包括创建、更新、删除和查询等
 */
@RestController
@RequestMapping("/api/admin/units")
public class AdminUnitController {

    @Autowired
    private AdminUnitService adminUnitService;

    /**
     * 获取学习单元列表（分页+过滤）
     */
    @GetMapping
    public ResponseResult getUnitList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String platform) {
        return adminUnitService.getUnitList(page, size, keyword, type, platform);
    }

    /**
     * 获取学习单元详情
     */
    @GetMapping("/{id}")
    public ResponseResult getUnitById(@PathVariable Long id) {
        return adminUnitService.getUnitById(id);
    }

    /**
     * 创建学习单元
     */
    @PostMapping
    public ResponseResult createUnit(@RequestBody LearningUnit unit) {
        return adminUnitService.createUnit(unit);
    }

    /**
     * 更新学习单元
     */
    @PutMapping("/{id}")
    public ResponseResult updateUnit(@PathVariable Long id, @RequestBody LearningUnit unit) {
        unit.setId(id);
        return adminUnitService.updateUnit(unit);
    }

    /**
     * 删除学习单元
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteUnit(@PathVariable Long id) {
        return adminUnitService.deleteUnit(id);
    }
}
