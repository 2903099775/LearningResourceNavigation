package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.entity.User;
import com.learning.mapper.UserMapper;
import com.learning.service.AuthService;
import com.learning.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 * 负责处理用户注册、登录、登出、管理员登录和令牌刷新等认证相关功能
 * 引用文件：c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/common/ResponseResult.java, c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/entity/User.java, c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/mapper/UserMapper.java, c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/service/AuthService.java, c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/util/JwtUtil.java
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public ResponseResult<String> register(RegisterRequest request) {
        log.info("用户注册尝试: 用户名={}, 邮箱={}", request.getUsername(), request.getEmail());
        
        // 用户名验证
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            log.warn("注册失败: 用户名不能为空");
            return ResponseResult.error("用户名不能为空");
        }
        if (request.getUsername().length() < 3 || request.getUsername().length() > 20) {
            log.warn("注册失败: 用户名长度必须在3-20位之间");
            return ResponseResult.error("用户名长度必须在3-20位之间");
        }
        if (!request.getUsername().matches("^[a-zA-Z0-9_]+$") ) {
            log.warn("注册失败: 用户名只能包含字母、数字和下划线");
            return ResponseResult.error("用户名只能包含字母、数字和下划线");
        }
        if (userMapper.findByUsername(request.getUsername()) != null) {
            log.warn("注册失败: 用户名已存在");
            return ResponseResult.error("用户名已存在");
        }

        // 邮箱验证
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.warn("注册失败: 邮箱不能为空");
            return ResponseResult.error("邮箱不能为空");
        }
        if (!request.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") ) {
            log.warn("注册失败: 邮箱格式不正确");
            return ResponseResult.error("邮箱格式不正确");
        }
        if (userMapper.findByEmail(request.getEmail()) != null) {
            log.warn("注册失败: 邮箱已存在");
            return ResponseResult.error("邮箱已存在");
        }

        // 密码强度检查
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            log.warn("注册失败: 密码长度至少为6位");
            return ResponseResult.error("密码长度至少为6位");
        }

        // 电话号码验证（如果提供）
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            if (!request.getPhone().matches("^1[3-9]\\d{9}$") ) {
                log.warn("注册失败: 电话号码格式不正确");
                return ResponseResult.error("电话号码格式不正确");
            }
            if (userMapper.findByPhone(request.getPhone()) != null) {
                log.warn("注册失败: 手机号已被注册");
                return ResponseResult.error("手机号已被注册");
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 加密存储
        user.setEmail(request.getEmail());
        user.setRole("USER");
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
        log.info("用户注册成功: 用户名={}, 插入结果确认", request.getUsername());

        return ResponseResult.success("注册成功");
    }

    @Override
    public ResponseResult<Object> login(LoginRequest request) {
        log.info("用户登录尝试: 用户名={}", request != null ? request.getUsername() : "null");
        
        try {
            if (request == null) {
                log.warn("登录失败: 请求对象为空");
                return ResponseResult.error("请求对象为空");
            }
            
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                log.warn("登录失败: 用户名为空");
                return ResponseResult.error("用户名或密码错误");
            }
            
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                log.warn("登录失败: 密码为空");
                return ResponseResult.error("用户名或密码错误");
            }
            
            User user = userMapper.findByUsername(request.getUsername());
            if (user == null) {
                log.warn("登录失败: 用户不存在");
                return ResponseResult.error("用户名或密码错误");
            }
            
            // 验证密码（使用 PasswordEncoder 验证）
            boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
            if (!passwordMatch) {
                log.warn("登录失败: 密码错误");
                return ResponseResult.error("用户名或密码错误");
            }
            
            if (user.getStatus() == null || user.getStatus() != 1) {
                log.warn("登录失败: 用户已被禁用");
                return ResponseResult.error("用户已被禁用");
            }
            
            String token = jwtUtil.generateToken(user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "登录成功");
            response.put("username", user.getUsername());
            response.put("token", token);
            response.put("refreshToken", refreshToken);
            response.put("role", user.getRole());
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            // VIP到期日期，供前端判断VIP状态（切换页面后不丢失）
            response.put("vipExpireDate", user.getVipExpireDate() != null ? user.getVipExpireDate().toString() : null);
            response.put("avatar", user.getAvatar());
            
            log.info("登录成功: 用户名={}", user.getUsername());
            return ResponseResult.success(response);
        } catch (Exception e) {
            log.error("登录过程中发生异常: ", e);
            return ResponseResult.error("登录失败: 系统内部错误");
        }
    }

    @Override
    public ResponseResult<String> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            log.info("用户登出: 用户名={}", authentication.getName());
        }
        SecurityContextHolder.clearContext();
        log.info("用户登出成功");
        return ResponseResult.success("登出成功");
    }

    @Override
    public ResponseResult<Object> adminLogin(LoginRequest request) {
        log.info("管理员登录尝试: 用户名={}", request != null ? request.getUsername() : "null");
        
        try {
            if (request == null) {
                log.warn("管理员登录失败: 请求对象为空");
                return ResponseResult.error("请求对象为空");
            }
            
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                log.warn("管理员登录失败: 用户名为空");
                return ResponseResult.error("用户名或密码错误");
            }
            
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                log.warn("管理员登录失败: 密码为空");
                return ResponseResult.error("用户名或密码错误");
            }
            
            // 查询用户信息
            User user = userMapper.findByUsername(request.getUsername());
            if (user == null) {
                log.warn("管理员登录失败: 用户不存在");
                return ResponseResult.error("用户名或密码错误");
            }
            
            // 验证是否为管理员
            if (!"ADMIN".equals(user.getRole())) {
                log.warn("管理员登录失败: 用户不是管理员");
                return ResponseResult.error("只有管理员才能登录");
            }
            
            // 验证密码（使用 PasswordEncoder 验证）
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("管理员登录失败: 密码错误");
                return ResponseResult.error("用户名或密码错误");
            }
            
            // 检查用户状态
            if (user.getStatus() == null || user.getStatus() != 1) {
                log.warn("管理员登录失败: 用户已被禁用");
                return ResponseResult.error("用户已被禁用");
            }
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("token", token);
            response.put("refreshToken", refreshToken);
            
            String role = user.getRole() != null ? user.getRole() : "USER";
            response.put("role", role);
            
            log.info("管理员登录成功: 用户名={}", user.getUsername());
            return ResponseResult.success(response);
        } catch (Exception e) {
            log.error("管理员登录过程中发生异常: ", e);
            return ResponseResult.error("登录失败: 系统内部错误");
        }
    }

    @Override
    public ResponseResult<Object> refreshToken(RefreshTokenRequest request) {
        log.info("令牌刷新尝试");
        
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null) {
            log.warn("令牌刷新失败: 刷新令牌不能为空");
            return ResponseResult.error("刷新令牌不能为空");
        }

        // 验证是否为刷新令牌
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            log.warn("令牌刷新失败: 无效的刷新令牌类型");
            return ResponseResult.error("无效的刷新令牌");
        }

        // 验证令牌有效性
        if (!jwtUtil.validateToken(refreshToken)) {
            log.warn("令牌刷新失败: 刷新令牌无效或已过期");
            return ResponseResult.error("刷新令牌无效或已过期");
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        if (username == null) {
            log.warn("令牌刷新失败: 刷新令牌无效");
            return ResponseResult.error("刷新令牌无效");
        }

        User user = findUserByUsername(username);
        if (user == null) {
            log.warn("令牌刷新失败: 用户不存在");
            return ResponseResult.error("用户不存在");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            log.warn("令牌刷新失败: 用户已被禁用");
            return ResponseResult.error("用户已被禁用");
        }

        String newToken = jwtUtil.generateToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        Map<String, Object> response = new HashMap<>();
        response.put("token", newToken);
        response.put("refreshToken", newRefreshToken);

        log.info("令牌刷新成功: 用户名={}", username);
        return ResponseResult.success(response);
    }
    
    /**
     * 查找用户信息
     */
    private User findUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}