package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.entity.User;
import com.learning.service.AchievementService;
import com.learning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/achievement")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseResult<Object> getAchievementList(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseResult.error(401, "用户未登录");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseResult.error(401, "用户不存在");
        }

        achievementService.checkAndUnlockAchievements(user.getId());
        Map<String, Object> progress = achievementService.getAchievementProgress(user.getId());

        return ResponseResult.success(progress);
    }

    @PostMapping("/check")
    public ResponseResult<Object> checkAchievements(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseResult.error(401, "用户未登录");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseResult.error(401, "用户不存在");
        }

        achievementService.checkAndUnlockAchievements(user.getId());
        Map<String, Object> progress = achievementService.getAchievementProgress(user.getId());

        return ResponseResult.success(progress);
    }
}