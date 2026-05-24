package com.learning.service;

import com.learning.common.ResponseResult;
import com.learning.entity.User;
import com.learning.mapper.UserMapper;
import com.learning.service.impl.AuthServiceImpl;
import com.learning.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    public AuthServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterSuccess() {
        AuthService.RegisterRequest request = new AuthService.RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Test1234");
        request.setEmail("test@example.com");

        when(userMapper.findByUsername("testuser")).thenReturn(null);
        when(userMapper.findByEmail("test@example.com")).thenReturn(null);
        when(passwordEncoder.encode("Test1234")).thenReturn("encodedPassword");

        ResponseResult<String> result = authService.register(request);
        assertEquals("注册成功", result.getData());
    }

    @Test
    public void testRegisterUsernameExists() {
        AuthService.RegisterRequest request = new AuthService.RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Test1234");
        request.setEmail("test@example.com");

        when(userMapper.findByUsername("testuser")).thenReturn(new User());

        ResponseResult<String> result = authService.register(request);
        assertEquals("用户名已存在", result.getMessage());
    }

    @Test
    public void testRegisterEmailExists() {
        AuthService.RegisterRequest request = new AuthService.RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Test1234");
        request.setEmail("test@example.com");

        when(userMapper.findByUsername("testuser")).thenReturn(null);
        when(userMapper.findByEmail("test@example.com")).thenReturn(new User());

        ResponseResult<String> result = authService.register(request);
        assertEquals("邮箱已存在", result.getMessage());
    }

    @Test
    public void testLoginSuccess() {
        AuthService.LoginRequest request = new AuthService.LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test1234");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setStatus(1);
        user.setRole("USER");

        when(userMapper.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("Test1234", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser")).thenReturn("token");

        ResponseResult<Object> result = authService.login(request);
        assertEquals(200, result.getCode());
    }

    @Test
    public void testLoginUserNotFound() {
        AuthService.LoginRequest request = new AuthService.LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test1234");

        when(userMapper.findByUsername("testuser")).thenReturn(null);

        ResponseResult<Object> result = authService.login(request);
        assertEquals("用户名或密码错误", result.getMessage());
    }

    @Test
    public void testLoginUserDisabled() {
        AuthService.LoginRequest request = new AuthService.LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test1234");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setStatus(0);

        when(userMapper.findByUsername("testuser")).thenReturn(user);

        ResponseResult<Object> result = authService.login(request);
        assertEquals("用户已被禁用", result.getMessage());
    }

    @Test
    public void testLoginPasswordWrong() {
        AuthService.LoginRequest request = new AuthService.LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test1234");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setStatus(1);

        when(userMapper.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("Test1234", "encodedPassword")).thenReturn(false);

        ResponseResult<Object> result = authService.login(request);
        assertEquals("用户名或密码错误", result.getMessage());
    }
}
