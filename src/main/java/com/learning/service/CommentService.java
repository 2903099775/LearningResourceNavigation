package com.learning.service;

import com.learning.common.ResponseResult;
import com.learning.entity.Comment;

/**
 * 评论服务接口
 * 定义评论的创建、获取、点赞和审核等方法
 * 引用文件：com.learning.common.ResponseResult, com.learning.entity.Comment
 */
public interface CommentService {
    ResponseResult<String> createComment(Comment comment);
    
    ResponseResult<Object> getComments(String type, Long id);
    
    ResponseResult<String> likeComment(Long id);
    
    ResponseResult<Object> getAllComments();
    
    ResponseResult<Object> getCommentsByStatus(Integer status);
    
    ResponseResult<String> approveComment(Long id);
    
    ResponseResult<String> rejectComment(Long id);
    
    ResponseResult<String> deleteComment(Long id);
    
    ResponseResult<String> updateComment(Long id, String content);
    
    ResponseResult<String> toggleTopComment(Long id);
    
    ResponseResult<String> replyComment(Comment comment);
}