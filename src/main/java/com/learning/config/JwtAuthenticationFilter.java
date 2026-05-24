package com.learning.config;

import com.learning.service.UserService;
import com.learning.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * 负责验证JWT令牌并设置用户认证信息到SecurityContext
 * 引用文件：c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/service/UserService.java, c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/util/JwtUtil.java, c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/entity/User.java
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        // 对于登录、注册等公开接口，直接放行
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/public/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // 验证token有效性
                if (!jwtUtil.validateToken(token)) {
                    // Token无效，清除上下文
                    SecurityContextHolder.clearContext();
                    // 对于API请求，返回401错误
                    if (requestURI.startsWith("/api")) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json; charset=utf-8");
                        response.getWriter().write("{\"code\": 401, \"message\": \"令牌无效或已过期\"}");
                        return;
                    }
                } else {
                    String username = jwtUtil.getUsernameFromToken(token);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        com.learning.entity.User user = userService.findByUsername(username);
                        if (user != null) {
                            // 检查用户状态
                            if (user.getStatus() == null || user.getStatus() != 1) {
                                // 用户已被禁用
                                SecurityContextHolder.clearContext();
                                if (requestURI.startsWith("/api")) {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setContentType("application/json; charset=utf-8");
                                    response.getWriter().write("{\"code\": 403, \"message\": \"用户已被禁用\"}");
                                    return;
                                }
                            } else {
                                String role = user.getRole();
                                if (role == null) role = "USER";
                                UsernamePasswordAuthenticationToken authentication = 
                                        new UsernamePasswordAuthenticationToken(
                                                username, 
                                                null, 
                                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                                        );
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Token处理异常，清除上下文
                SecurityContextHolder.clearContext();
                // 对于API请求，返回401错误
                if (requestURI.startsWith("/api")) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json; charset=utf-8");
                    response.getWriter().write("{\"code\": 401, \"message\": \"令牌处理失败\"}");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
