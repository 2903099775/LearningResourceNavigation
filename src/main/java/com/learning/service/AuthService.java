package com.learning.service;

import com.learning.common.ResponseResult;

/**
 * 认证服务接口
 * 定义用户注册、登录、登出、管理员登录和令牌刷新等认证相关方法
 * 引用文件：c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\common\ResponseResult.java
 */
public interface AuthService {
    ResponseResult<String> register(RegisterRequest request);
    
    ResponseResult<Object> login(LoginRequest request);
    
    ResponseResult<Object> adminLogin(LoginRequest request);
    
    ResponseResult<String> logout();
    
    ResponseResult<Object> refreshToken(RefreshTokenRequest request);
    
    class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String phone;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    class LoginRequest {
        private String username;
        private String password;
        private boolean rememberMe;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isRememberMe() {
            return rememberMe;
        }

        public void setRememberMe(boolean rememberMe) {
            this.rememberMe = rememberMe;
        }
    }
    
    class RefreshTokenRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}