package com.learning.service.impl.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.User;
import com.learning.mapper.UserMapper;
import com.learning.service.admin.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员用户服务实现类
 * 负责处理管理员对用户的操作方法的具体实现，包括获取用户列表等
 * 引用文件：com.learning.common.ResponseResult, com.learning.entity.User, com.learning.mapper.UserMapper, com.learning.service.admin.AdminUserService
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseResult getUsers() {
        List<User> users = userMapper.findAll();
        users.forEach(u -> u.setPassword(null));
        return ResponseResult.success(users);
    }

    @Override
    public ResponseResult getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            return ResponseResult.error(404, "用户不存在");
        }
        user.setPassword(null);
        return ResponseResult.success(user);
    }

    @Override
    public ResponseResult createUser(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return ResponseResult.error(400, "用户名已存在");
        }
        if (userMapper.findByEmail(user.getEmail()) != null) {
            return ResponseResult.error(400, "邮箱已存在");
        }
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            if (userMapper.findByPhone(user.getPhone()) != null) {
                return ResponseResult.error(400, "手机号已存在");
            }
        }
        
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        
        userMapper.insert(user);
        user.setPassword(null);
        return ResponseResult.success(user);
    }

    @Override
    public ResponseResult updateUser(Long id, User user) {
        User existingUser = userMapper.findById(id);
        if (existingUser == null) {
            return ResponseResult.error(404, "用户不存在");
        }
        
        if (existingUser.getRole().equals("ADMIN") && !user.getRole().equals("ADMIN")) {
            return ResponseResult.error(403, "不能修改管理员角色");
        }
        
        User userByUsername = userMapper.findByUsername(user.getUsername());
        if (userByUsername != null && !userByUsername.getId().equals(id)) {
            return ResponseResult.error(400, "用户名已存在");
        }
        
        User userByEmail = userMapper.findByEmail(user.getEmail());
        if (userByEmail != null && !userByEmail.getId().equals(id)) {
            return ResponseResult.error(400, "邮箱已存在");
        }

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            User userByPhone = userMapper.findByPhone(user.getPhone());
            if (userByPhone != null && !userByPhone.getId().equals(id)) {
                return ResponseResult.error(400, "手机号已存在");
            }
        }
        
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        } else {
            user.setPassword(existingUser.getPassword());
        }
        
        user.setId(id);
        userMapper.update(user);
        user.setPassword(null);
        return ResponseResult.success(user);
    }

    @Override
    public ResponseResult deleteUser(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            return ResponseResult.error(404, "用户不存在");
        }
        
        // 不能删除管理员
        if (user.getRole().equals("ADMIN")) {
            return ResponseResult.error(403, "不能删除管理员账户");
        }
        
        // 这里实际项目中可能需要软删除或级联删除相关数据
        // 目前暂时只做逻辑删除
        user.setStatus(0);
        userMapper.update(user);
        return ResponseResult.success("用户删除成功");
    }

    @Override
    public ResponseResult toggleUserStatus(Long id, Integer status) {
        User user = userMapper.findById(id);
        if (user == null) {
            return ResponseResult.error(404, "用户不存在");
        }
        
        // 不能禁用管理员
        if (user.getRole().equals("ADMIN") && status == 0) {
            return ResponseResult.error(403, "不能禁用管理员账户");
        }
        
        user.setStatus(status);
        userMapper.update(user);
        return ResponseResult.success("用户状态更新成功");
    }

    @Override
    public ResponseResult searchUsers(String keyword, Integer page, Integer size) {
        List<User> users = userMapper.searchUsers(keyword);
        users.forEach(u -> u.setPassword(null));
        
        // 简单的分页处理
        int start = (page - 1) * size;
        int end = Math.min(start + size, users.size());
        List<User> paginatedUsers = users.subList(start, end);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", paginatedUsers);
        result.put("total", users.size());
        result.put("page", page);
        result.put("size", size);
        
        return ResponseResult.success(result);
    }
}
