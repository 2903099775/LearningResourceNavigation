package com.learning.mapper;

import com.learning.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户数据访问接口
 * 负责用户相关的数据库操作，包括查询、插入和更新等
 * 引用文件：c:\Users\29030\Documents\trae_projects\src\main\java\com\learning\entity\User.java
 */
@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);

    User findByEmail(@Param("email") String email);

    User findByPhone(@Param("phone") String phone);

    int insert(User user);

    void update(User user);

    User findById(@Param("id") Long id);

    List<User> findAll();
    List<User> searchUsers(@Param("keyword") String keyword);
    boolean isVipUser(@Param("userId") Long userId);
    
    List<Map<String, Object>> findAllWithDetails();
    int countUsersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    void updateMuteStatus(@Param("userId") Long userId, @Param("muteStatus") Integer muteStatus, @Param("muteEndDate") LocalDateTime muteEndDate);
    
    List<Map<String, Object>> countUsersGroupByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    int countTotal();

    List<User> findByRole(@Param("role") String role);
}