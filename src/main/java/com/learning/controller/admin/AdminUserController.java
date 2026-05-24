package com.learning.controller.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.User;
import com.learning.service.admin.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员用户控制器
 * 负责处理管理员对用户的API请求，包括获取用户列表等操作
 * 引用文件：com.learning.common.ResponseResult, com.learning.service.admin.AdminUserService, com.learning.entity.User
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    public ResponseResult<Object> getUsers() {
        return adminUserService.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseResult<Object> getUserById(@PathVariable Long id) {
        return adminUserService.getUserById(id);
    }

    @PostMapping
    public ResponseResult<Object> createUser(@RequestBody User user) {
        return adminUserService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseResult<Object> updateUser(@PathVariable Long id, @RequestBody User user) {
        return adminUserService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Object> deleteUser(@PathVariable Long id) {
        return adminUserService.deleteUser(id);
    }

    @PutMapping("/{id}/status")
    public ResponseResult<Object> toggleUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        return adminUserService.toggleUserStatus(id, status);
    }

    @GetMapping("/search")
    public ResponseResult<Object> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return adminUserService.searchUsers(keyword, page, size);
    }
}