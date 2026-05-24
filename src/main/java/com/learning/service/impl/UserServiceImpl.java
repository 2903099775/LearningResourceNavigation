package com.learning.service.impl;

import com.learning.entity.User;
import com.learning.mapper.UserMapper;
import com.learning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * 负责处理用户相关的业务逻辑，包括用户查询、注册和更新等操作
 * 引用文件：c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\User.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\mapper\UserMapper.java, c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\service\UserService.java
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Cacheable(value = "users", key = "#username")
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void register(User user) {
        userMapper.insert(user);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public boolean isVip(String username) {
        User user = findByUsername(username);
        if (user == null) return false;

        // 管理员自动拥有VIP权限
        if ("ADMIN".equals(user.getRole())) {
            return true;
        }

        if (user.getVipExpireDate() == null) {
            return false;
        }
        return user.getVipExpireDate().isAfter(java.time.LocalDate.now());
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return rawPassword.equals(encodedPassword);
    }
}
