package com.learning.controller.admin;

import com.learning.common.ResponseResult;
import com.learning.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员认证控制器
 * 负责处理管理员登录和登出等认证相关的API请求
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseResult<Object> adminLogin(@RequestBody AuthService.LoginRequest request) {
        log.info("收到管理员登录请求: 用户名={}", request.getUsername());
        try {
            // 调用AuthService的adminLogin方法
            ResponseResult<Object> result = authService.adminLogin(request);
            log.info("管理员登录请求处理结果: code={}, message={}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("管理员登录过程中发生异常: ", e);
            return ResponseResult.error("登录失败: 系统内部错误");
        }
    }

    @PostMapping("/logout")
    public ResponseResult<String> adminLogout() {
        log.info("收到管理员登出请求");
        try {
            ResponseResult<String> result = authService.logout();
            log.info("管理员登出请求处理结果: code={}, message={}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("管理员登出过程中发生异常: ", e);
            return ResponseResult.error("登出失败: 系统内部错误");
        }
    }
}