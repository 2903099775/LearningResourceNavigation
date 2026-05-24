package com.learning.service;

import com.learning.common.ResponseResult;
import com.learning.entity.Post;

public interface PostService {
    ResponseResult<Object> createPost(Post post);

    ResponseResult<Object> getPosts(int page, int size);

    ResponseResult<Object> getPostById(Long id);

    ResponseResult<Object> getPostsByUser(Long userId);

    ResponseResult<Object> updatePost(Long id, Post post);

    ResponseResult<Object> deletePost(Long id);

    ResponseResult<Object> likePost(Long id);

    ResponseResult<Object> getPinnedAnnouncements();

    ResponseResult<Object> getMonthlyTopUsers(int limit);
}