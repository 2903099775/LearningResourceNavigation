package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.entity.Comment;
import com.learning.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 * 负责处理评论相关的API请求，包括创建评论、获取评论、点赞评论和审核评论等
 * 引用文件：com.learning.common.ResponseResult, com.learning.entity.Comment, com.learning.service.CommentService
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseResult<String> createComment(@RequestBody Comment comment) {
        return commentService.createComment(comment);
    }

    @GetMapping("/{type}/{id}")
    public ResponseResult<Object> getComments(@PathVariable String type, @PathVariable Long id) {
        return commentService.getComments(type, id);
    }

    @PostMapping("/{id}/like")
    public ResponseResult<String> likeComment(@PathVariable Long id) {
        return commentService.likeComment(id);
    }

    @PostMapping("/{id}/reply")
    public ResponseResult<String> replyToComment(@PathVariable Long id, @RequestBody Comment comment) {
        comment.setParentId(id);
        return commentService.replyComment(comment);
    }

    @GetMapping("/admin/all")
    public ResponseResult<Object> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/admin/status/{status}")
    public ResponseResult<Object> getCommentsByStatus(@PathVariable Integer status) {
        return commentService.getCommentsByStatus(status);
    }

    @PostMapping("/admin/{id}/approve")
    public ResponseResult<String> approveComment(@PathVariable Long id) {
        return commentService.approveComment(id);
    }

    @PostMapping("/admin/{id}/reject")
    public ResponseResult<String> rejectComment(@PathVariable Long id) {
        return commentService.rejectComment(id);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseResult<String> deleteComment(@PathVariable Long id) {
        return commentService.deleteComment(id);
    }

    @PutMapping("/admin/{id}")
    public ResponseResult<String> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        return commentService.updateComment(id, comment.getContent());
    }

    @PostMapping("/admin/{id}/top")
    public ResponseResult<String> toggleTopComment(@PathVariable Long id) {
        return commentService.toggleTopComment(id);
    }

    @PostMapping("/admin/reply")
    public ResponseResult<String> replyComment(@RequestBody Comment comment) {
        return commentService.replyComment(comment);
    }
}