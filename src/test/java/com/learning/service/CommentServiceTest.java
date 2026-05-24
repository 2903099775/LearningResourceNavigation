package com.learning.service;

import com.learning.common.ResponseResult;
import com.learning.entity.Comment;
import com.learning.entity.User;
import com.learning.mapper.CommentMapper;
import com.learning.mapper.UserMapper;
import com.learning.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    public CommentServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateComment() {
        Comment comment = new Comment();
        comment.setTargetType("PATH");
        comment.setTargetId(1L);
        comment.setContent("Test comment");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userMapper.findByUsername(anyString())).thenReturn(user);

        ResponseResult<String> result = commentService.createComment(comment);
        assertEquals("评论创建成功", result.getData());
        verify(commentMapper, times(1)).insert(comment);
    }

    @Test
    public void testGetComments() {
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setContent("Test comment");
        comments.add(comment);

        when(commentMapper.findByTarget("PATH", 1L)).thenReturn(comments);

        ResponseResult<Object> result = commentService.getComments("PATH", 1L);
        assertEquals(200, result.getCode());
    }

    @Test
    public void testLikeComment() {
        Comment comment = new Comment();
        comment.setId(1L);

        when(commentMapper.findById(1L)).thenReturn(comment);

        ResponseResult<String> result = commentService.likeComment(1L);
        assertEquals("点赞成功", result.getData());
        verify(commentMapper, times(1)).updateLikesCount(1L);
    }

    @Test
    public void testLikeCommentNotFound() {
        when(commentMapper.findById(1L)).thenReturn(null);

        ResponseResult<String> result = commentService.likeComment(1L);
        assertEquals("评论不存在", result.getMessage());
    }
}
