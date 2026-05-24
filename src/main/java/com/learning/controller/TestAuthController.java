package com.learning.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试认证控制器
 * 负责处理测试认证相关的API请求，包括测试登录接口等
 */
@RestController
@RequestMapping("/api/public/auth")
public class TestAuthController {

    private static final Logger log = LoggerFactory.getLogger(TestAuthController.class);

    @PostMapping("/test-login")
    public Map<String, Object> testLogin(@RequestBody Map<String, Object> request) {
        log.info("收到测试登录请求");
        try {
            log.info("测试登录请求参数: 用户名={}, 密码长度={}", request.get("username"), request.get("password") != null ? ((String) request.get("password")).length() : 0);
            // 简化登录逻辑，直接返回成功响应
            Map<String, Object> response = new HashMap<>();
            response.put("message", "登录成功");
            response.put("username", request.get("username"));
            response.put("token", "test-token");
            response.put("refreshToken", "test-refresh-token");
            response.put("role", "USER");
            log.info("测试登录请求处理结果: 成功");
            return response;
        } catch (Exception e) {
            log.error("测试登录过程中发生异常: ", e);
            log.error("异常类型: {}", e.getClass().getName());
            log.error("异常消息: {}", e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "登录失败: 系统内部错误");
            return response;
        }
    }
}
