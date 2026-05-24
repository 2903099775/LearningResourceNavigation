package com.learning.service.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.User;

/**
 * 管理员用户服务接口
 * 定义管理员对用户的操作方法，包括获取用户列表等
 * 引用文件：com.learning.common.ResponseResult, com.learning.entity.User
 */
public interface AdminUserService {
    ResponseResult<Object> getUsers();
    ResponseResult<Object> getUserById(Long id);
    ResponseResult<Object> createUser(User user);
    ResponseResult<Object> updateUser(Long id, User user);
    ResponseResult<Object> deleteUser(Long id);
    ResponseResult<Object> toggleUserStatus(Long id, Integer status);
    ResponseResult<Object> searchUsers(String keyword, Integer page, Integer size);
}