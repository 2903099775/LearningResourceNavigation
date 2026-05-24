package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.entity.Post;
import com.learning.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseResult<Object> createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    @GetMapping
    public ResponseResult<Object> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return postService.getPosts(page, size);
    }

    @GetMapping("/{id}")
    public ResponseResult<Object> getPost(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/user/{userId}")
    public ResponseResult<Object> getPostsByUser(@PathVariable Long userId) {
        return postService.getPostsByUser(userId);
    }

    @PutMapping("/{id}")
    public ResponseResult<Object> updatePost(@PathVariable Long id, @RequestBody Post post) {
        return postService.updatePost(id, post);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Object> deletePost(@PathVariable Long id) {
        return postService.deletePost(id);
    }

    @PostMapping("/{id}/like")
    public ResponseResult<Object> likePost(@PathVariable Long id) {
        return postService.likePost(id);
    }

    @GetMapping("/announcements/pinned")
    public ResponseResult<Object> getPinnedAnnouncements() {
        return postService.getPinnedAnnouncements();
    }

    @GetMapping("/top-users/monthly")
    public ResponseResult<Object> getMonthlyTopUsers(
            @RequestParam(defaultValue = "3") int limit) {
        return postService.getMonthlyTopUsers(limit);
    }
}