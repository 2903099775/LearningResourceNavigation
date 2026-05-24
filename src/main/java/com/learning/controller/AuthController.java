package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.entity.User;
import com.learning.service.AuthService;
import com.learning.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 负责处理用户注册、登录、登出和令牌刷新等认证相关的API请求
 */
@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseResult<String> register(@RequestBody AuthService.RegisterRequest request) {
        log.info("收到注册请求: 用户名={}, 邮箱={}", request.getUsername(), request.getEmail());
        try {
            ResponseResult<String> result = authService.register(request);
            log.info("注册请求处理结果: code={}, message={}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("注册过程中发生异常: ", e);
            return ResponseResult.error("注册失败: 系统内部错误");
        }
    }

    @PostMapping("/login")
    public ResponseResult<Object> login(@RequestBody AuthService.LoginRequest request) {
        System.out.println("=== AuthController 登录请求开始 ===");
        System.out.println("请求对象: " + request);
        if (request != null) {
            System.out.println("用户名: " + request.getUsername());
            System.out.println("密码长度: " + (request.getPassword() != null ? request.getPassword().length() : 0));
        }
        
        try {
            System.out.println("1. 验证请求对象");
            if (request == null) {
                System.out.println("请求对象为空");
                return ResponseResult.error("请求对象为空");
            }
            
            System.out.println("2. 调用AuthService.login");
            ResponseResult<Object> result = authService.login(request);
            System.out.println("3. 处理结果: code=" + result.getCode() + ", message=" + result.getMessage());
            
            return result;
        } catch (Exception e) {
            System.out.println("=== 登录异常 ===");
            System.out.println("异常类型: " + e.getClass().getName());
            System.out.println("异常消息: " + e.getMessage());
            e.printStackTrace();
            return ResponseResult.error("登录失败: 系统内部错误");
        }
    }

    @PostMapping("/logout")
    public ResponseResult<String> logout() {
        log.info("收到登出请求");
        try {
            ResponseResult<String> result = authService.logout();
            log.info("登出请求处理结果: code={}, message={}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("登出过程中发生异常: ", e);
            return ResponseResult.error("登出失败: 系统内部错误");
        }
    }

    @PostMapping("/refresh")
    public ResponseResult<Object> refreshToken(@RequestBody AuthService.RefreshTokenRequest request) {
        log.info("收到令牌刷新请求");
        try {
            ResponseResult<Object> result = authService.refreshToken(request);
            log.info("令牌刷新请求处理结果: code={}, message={}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("令牌刷新过程中发生异常: ", e);
            return ResponseResult.error("令牌刷新失败: 系统内部错误");
        }
    }
}
