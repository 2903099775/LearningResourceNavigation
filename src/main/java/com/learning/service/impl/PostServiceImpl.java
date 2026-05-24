package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.entity.Announcement;
import com.learning.entity.Post;
import com.learning.entity.User;
import com.learning.mapper.AnnouncementMapper;
import com.learning.mapper.PostMapper;
import com.learning.mapper.UserMapper;
import com.learning.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public ResponseResult<Object> createPost(Post post) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error(401, "请先登录");
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            return ResponseResult.error(401, "用户不存在");
        }

        if (user.getMuteStatus() != null && user.getMuteStatus() == 1) {
            if (user.getMuteEndDate() != null && user.getMuteEndDate().isAfter(java.time.LocalDateTime.now())) {
                return ResponseResult.error(403, "您已被禁言，禁言结束时间：" + user.getMuteEndDate());
            }
        }

        post.setUserId(userId);
        post.setUsername(user.getUsername());
        post.setUserAvatar(user.getAvatar());
        post.setStatus(1);
        post.setLikesCount(0);
        post.setCommentsCount(0);
        post.setIsPinned(0);

        postMapper.insert(post);
        return ResponseResult.success("发帖成功", post);
    }

    @Override
    public ResponseResult<Object> getPosts(int page, int size) {
        List<Post> allPosts = postMapper.findAll(null, 1);

        int start = (page - 1) * size;
        int end = Math.min(start + size, allPosts.size());

        List<Post> pagedPosts = start < allPosts.size() ? allPosts.subList(start, end) : List.of();

        return ResponseResult.success(java.util.Map.of(
                "list", pagedPosts,
                "total", allPosts.size(),
                "page", page,
                "size", size
        ));
    }

    @Override
    public ResponseResult<Object> getPostById(Long id) {
        Post post = postMapper.findById(id);
        if (post == null) {
            return ResponseResult.error(404, "帖子不存在");
        }
        return ResponseResult.success(post);
    }

    @Override
    public ResponseResult<Object> getPostsByUser(Long userId) {
        List<Post> posts = postMapper.findByUserId(userId);
        return ResponseResult.success(posts);
    }

    @Override
    public ResponseResult<Object> updatePost(Long id, Post post) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error(401, "请先登录");
        }

        Post existingPost = postMapper.findById(id);
        if (existingPost == null) {
            return ResponseResult.error(404, "帖子不存在");
        }

        if (!existingPost.getUserId().equals(userId)) {
            return ResponseResult.error(403, "无权修改此帖子");
        }

        post.setId(id);
        post.setUserId(existingPost.getUserId());
        postMapper.update(post);
        return ResponseResult.success("更新成功", null);
    }

    @Override
    public ResponseResult<Object> deletePost(Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error(401, "请先登录");
        }

        Post post = postMapper.findById(id);
        if (post == null) {
            return ResponseResult.error(404, "帖子不存在");
        }

        User user = userMapper.findById(userId);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            if (!post.getUserId().equals(userId)) {
                return ResponseResult.error(403, "无权删除此帖子");
            }
        }

        postMapper.delete(id);
        return ResponseResult.success("删除成功", null);
    }

    @Override
    public ResponseResult<Object> likePost(Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error(401, "请先登录");
        }

        Post post = postMapper.findById(id);
        if (post == null) {
            return ResponseResult.error(404, "帖子不存在");
        }

        postMapper.updateLikesCount(id, 1);
        return ResponseResult.success("点赞成功", null);
    }

    @Override
    public ResponseResult<Object> getPinnedAnnouncements() {
        List<Announcement> pinnedAnnouncements = announcementMapper.findPinned();
        return ResponseResult.success(pinnedAnnouncements);
    }

    @Override
    public ResponseResult<Object> getMonthlyTopUsers(int limit) {
        List<java.util.Map<String, Object>> topUsers = postMapper.findMonthlyTopUsers(limit);
        return ResponseResult.success(topUsers);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            String username = (String) auth.getPrincipal();
            User user = userMapper.findByUsername(username);
            if (user != null) return user.getId();
        }
        return null;
    }
}