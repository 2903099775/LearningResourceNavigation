package com.learning.service.impl;

import com.learning.service.AuthService;
import com.learning.service.AuthService.LoginRequest;
import com.learning.common.ResponseResult;
import com.learning.entity.User;
import com.learning.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.Map;

@SpringBootTest
public class AuthServiceImplTest {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void initTestUser() {
        // 检查 admin 用户是否存在
        User adminUser = userMapper.findByUsername("admin");
        if (adminUser == null) {
            // 创建 admin 用户
            adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("123456"));
            adminUser.setEmail("admin@example.com");
            adminUser.setRole("ADMIN");
            adminUser.setStatus(1);
            adminUser.setCreatedAt(LocalDateTime.now());
            adminUser.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(adminUser);
            System.out.println("创建了测试用户: admin");
        } else {
            System.out.println("测试用户已存在: admin");
        }
    }

    @Test
    public void testLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("123456");
        
        ResponseResult<Object> result = authService.login(request);
        System.out.println("登录结果: " + result);
        
        // 验证登录结果
        assertNotNull(result, "登录结果不能为空");
        assertEquals(200, result.getCode(), "登录应该成功，返回码应为200");
        assertNotNull(result.getData(), "登录数据不能为空");
        
        // 验证返回的数据结构
        Map<?, ?> data = (Map<?, ?>) result.getData();
        assertNotNull(data.get("token"), "返回结果应包含token");
        assertNotNull(data.get("refreshToken"), "返回结果应包含refreshToken");
        assertNotNull(data.get("username"), "返回结果应包含username");
        assertNotNull(data.get("role"), "返回结果应包含role");
    }
}
