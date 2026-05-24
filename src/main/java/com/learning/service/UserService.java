package com.learning.service;

import com.learning.entity.User;

/**
 * 用户服务接口
 * 定义用户相关的业务逻辑方法，包括用户查询、注册和更新等操作
 * 引用文件：c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\User.java
 */
public interface UserService {
    User findByUsername(String username);
    
    User findByEmail(String email);
    
    void register(User user);
    
    User findById(Long id);
    
    void update(User user);

    boolean isVip(String username);

    boolean verifyPassword(String rawPassword, String encodedPassword);
}