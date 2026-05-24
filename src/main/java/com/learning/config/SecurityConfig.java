package com.learning.config;

import com.learning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 安全配置类
 * 负责配置Spring Security，包括认证、授权、密码加密和JWT过滤器等
 * 引用文件：c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/service/UserService.java, c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/config/JwtAuthenticationFilter.java, c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/entity/User.java
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            try {
                com.learning.entity.User user = userService.findByUsername(username);
                if (user == null) {
                    throw new org.springframework.security.core.userdetails.UsernameNotFoundException("用户不存在");
                }
                String role = user.getRole() != null ? user.getRole() : "USER";
                return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(role)
                        .build();
            } catch (Exception e) {
                throw new org.springframework.security.core.userdetails.UsernameNotFoundException("用户不存在", e);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/learning-path", "/learning-unit", "/profile", "/vip", "/admin", "/admin-dashboard", "/admin/edit-path").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/public/**", "/api/test/**", "/api/admin/auth/**", "/api/search/**").permitAll()
                        // 公开的获取操作
                        .requestMatchers(HttpMethod.GET, "/api/paths/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/units/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/resources/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/progress/recent-visitors/**").permitAll()
                        // 需要认证的操作
                        .requestMatchers(HttpMethod.POST, "/api/comments/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/comments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()
                        // 学习单元的用户交互操作（评论、收藏）只需登录
                        .requestMatchers(HttpMethod.POST, "/api/units/*/comments").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/units/*/favorites").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/units/*/favorites").authenticated()
                        // 学习单元管理操作需要ADMIN角色（创建、修改、删除单元本身）
                        .requestMatchers(HttpMethod.POST, "/api/units").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/units/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/units/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/units/**").hasRole("ADMIN")
                        // 收藏和笔记功能需要认证
                        .requestMatchers("/api/favorites/**").authenticated()
                        .requestMatchers("/api/notes/**").authenticated()
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/progress/unit-stats/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/progress/recent-visitors/**").permitAll()
                        .requestMatchers("/api/progress/**").authenticated()
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/api/feedback/**").authenticated()
                        // 支付功能需要认证
                        .requestMatchers("/api/payment/**").authenticated()
                        // 管理功能需要ADMIN角色
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // VIP功能需要ADMIN角色或VIP状态
                        .requestMatchers("/api/vip/**").hasAnyRole("ADMIN")
                        .anyRequest().permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (!request.getRequestURI().startsWith("/api")) {
                                response.sendRedirect("/login");
                            } else {
                                response.setStatus(401);
                                response.setContentType("application/json; charset=utf-8");
                                response.getWriter().write("{\"code\": 401, \"message\": \"请先登录\"}");
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // 对于页面请求，重定向到登录页
                            if (!request.getRequestURI().startsWith("/api")) {
                                response.sendRedirect("/login?error=access_denied&message=请先登录以访问此资源");
                            } else {
                                // 对于API请求，返回403错误
                                response.setStatus(403);
                                response.setContentType("application/json; charset=utf-8");
                                response.getWriter().write("{\"code\": 403, \"message\": \"没有权限访问此资源，请先登录\"}");
                            }
                        }))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}