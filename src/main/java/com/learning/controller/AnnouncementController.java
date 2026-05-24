package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.entity.Announcement;
import com.learning.entity.Wish;
import com.learning.mapper.AnnouncementMapper;
import com.learning.mapper.WishMapper;
import com.learning.mapper.FeedbackMapper;
import com.learning.entity.Feedback;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程预告控制器（用户端）
 * 提供公告查看、用户期待发布/点赞、用户反馈提交等接口
 */
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementMapper announcementMapper;
    @Autowired
    private WishMapper wishMapper;
    @Autowired
    private FeedbackMapper feedbackMapper;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 获取已发布公告列表
     * GET /api/announcements?type=announcement
     */
    @GetMapping
    public ResponseResult<Object> list(@RequestParam(required = false) String type) {
        List<Announcement> list = announcementMapper.findPublished(type);
        return ResponseResult.success(list);
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public ResponseResult<Object> detail(@PathVariable Long id) {
        Announcement a = announcementMapper.findById(id);
        if (a == null) return ResponseResult.error(404, "公告不存在");
        return ResponseResult.success(a);
    }

    /**
     * 点赞公告
     */
    @PostMapping("/{id}/like")
    public ResponseResult<Object> likeAnnouncement(@PathVariable Long id) {
        Announcement a = announcementMapper.findById(id);
        if (a == null) return ResponseResult.error(404, "公告不存在");
        announcementMapper.updateLikesCount(id, 1);
        return ResponseResult.success("点赞成功", null);
    }

    // ==================== 用户期待 ====================

    /**
     * 获取用户期待列表
     * GET /api/announcements/wishes?category=course&sort=popular
     */
    @GetMapping("/wishes")
    public ResponseResult<Object> listWishes(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "popular") String sort) {
        List<Wish> list = wishMapper.findAll(category, sort);
        return ResponseResult.success(list);
    }

    /**
     * 发布用户期待（需登录）
     */
    @PostMapping("/wishes")
    public ResponseResult<Object> createWish(@RequestBody Wish wish, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        if (userId == null) return ResponseResult.error(401, "请先登录");
        wish.setUserId(userId);
        wish.setUsername(username);
        if (wish.getCategory() == null || wish.getCategory().isEmpty()) wish.setCategory("course");
        wishMapper.insert(wish);
        return ResponseResult.success("发布成功", wish);
    }

    /**
     * 点赞/取消点赞（需登录）
     */
    @PostMapping("/wishes/{id}/like")
    public ResponseResult<Object> toggleLike(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return ResponseResult.error(401, "请先登录");

        Wish wish = wishMapper.findById(id);
        if (wish == null) return ResponseResult.error(404, "不存在");

        Integer hasLiked = wishMapper.hasUserLiked(id, userId);
        if (hasLiked != null && hasLiked > 0) {
            wishMapper.removeUserLike(id, userId);
            wishMapper.updateLikeCount(id, -1);
            return ResponseResult.success("已取消点赞", null);
        } else {
            wishMapper.addUserLike(id, userId);
            wishMapper.updateLikeCount(id, 1);
            return ResponseResult.success("已点赞", null);
        }
    }

    // ==================== 用户反馈 ====================

    /**
     * 提交反馈（需登录）
     */
    @PostMapping("/feedback")
    public ResponseResult<Object> submitFeedback(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return ResponseResult.error(401, "请先登录");

        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setType("user_feedback");
        feedback.setTitle(body.getOrDefault("title", ""));
        feedback.setContent(body.getOrDefault("content", ""));
        feedback.setContact(body.getOrDefault("contact", ""));
        feedback.setStatus("pending");
        feedbackMapper.insert(feedback);
        return ResponseResult.success("反馈已提交，感谢您的建议！", null);
    }
}
