package com.learning.controller.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.Post;
import com.learning.entity.User;
import com.learning.mapper.PostMapper;
import com.learning.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/posts")
public class AdminPostController {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public ResponseResult<Object> listPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        List<Post> list = postMapper.findAll(keyword, status);
        Integer total = postMapper.countAll(keyword, status);
        return ResponseResult.success(Map.of("list", list, "total", total));
    }

    @GetMapping("/{id}")
    public ResponseResult<Object> getPost(@PathVariable Long id) {
        Post post = postMapper.findById(id);
        if (post == null) return ResponseResult.error(404, "帖子不存在");
        return ResponseResult.success(post);
    }

    @PutMapping("/{id}")
    public ResponseResult<Object> updatePost(@PathVariable Long id, @RequestBody Post post) {
        post.setId(id);
        postMapper.update(post);
        return ResponseResult.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Object> deletePost(@PathVariable Long id) {
        postMapper.delete(id);
        return ResponseResult.success("删除成功", null);
    }

    @PostMapping("/{id}/pin")
    public ResponseResult<Object> togglePin(@PathVariable Long id) {
        Post post = postMapper.findById(id);
        if (post == null) return ResponseResult.error(404, "帖子不存在");

        Integer newPinStatus = post.getIsPinned() != null && post.getIsPinned() == 1 ? 0 : 1;
        postMapper.updatePinned(id, newPinStatus);
        return ResponseResult.success(newPinStatus == 1 ? "已置顶" : "已取消置顶", null);
    }

    @PutMapping("/user/{userId}/mute")
    public ResponseResult<Object> muteUser(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> body) {
        User user = userMapper.findById(userId);
        if (user == null) return ResponseResult.error(404, "用户不存在");

        Integer days = (Integer) body.getOrDefault("days", 0);
        String reason = (String) body.getOrDefault("reason", "");

        LocalDateTime muteEndDate = LocalDateTime.now().plusDays(days);
        userMapper.updateMuteStatus(userId, 1, muteEndDate);

        return ResponseResult.success("禁言成功", null);
    }

    @PutMapping("/user/{userId}/unmute")
    public ResponseResult<Object> unmuteUser(@PathVariable Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) return ResponseResult.error(404, "用户不存在");

        userMapper.updateMuteStatus(userId, 0, null);
        return ResponseResult.success("已解除禁言", null);
    }
}