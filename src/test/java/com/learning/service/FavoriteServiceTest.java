package com.learning.service;

import com.learning.common.ResponseResult;
import com.learning.entity.Favorite;
import com.learning.entity.User;
import com.learning.mapper.FavoriteMapper;
import com.learning.mapper.UserMapper;
import com.learning.service.impl.FavoriteServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FavoriteServiceTest {

    @Mock
    private FavoriteMapper favoriteMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    public FavoriteServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddFavorite() {
        Favorite favorite = new Favorite();
        favorite.setTargetType("PATH");
        favorite.setTargetId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userMapper.findByUsername(anyString())).thenReturn(user);
        when(favoriteMapper.findByUserAndTarget(1L, "PATH", 1L)).thenReturn(null);

        ResponseResult result = favoriteService.addFavorite(favorite);
        assertEquals("添加收藏成功", result.getData());
        verify(favoriteMapper, times(1)).insert(favorite);
    }

    @Test
    public void testAddFavoriteAlreadyExists() {
        Favorite favorite = new Favorite();
        favorite.setTargetType("PATH");
        favorite.setTargetId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userMapper.findByUsername(anyString())).thenReturn(user);
        when(favoriteMapper.findByUserAndTarget(1L, "PATH", 1L)).thenReturn(new Favorite());

        ResponseResult result = favoriteService.addFavorite(favorite);
        assertEquals("已经收藏过了", result.getMessage());
    }

    @Test
    public void testGetFavorites() {
        List<Favorite> favorites = new ArrayList<>();
        Favorite favorite = new Favorite();
        favorite.setTargetType("PATH");
        favorite.setTargetId(1L);
        favorites.add(favorite);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userMapper.findByUsername(anyString())).thenReturn(user);
        when(favoriteMapper.findByUserId(1L)).thenReturn(favorites);

        ResponseResult result = favoriteService.getFavorites();
        assertEquals(200, result.getCode());
    }

    @Test
    public void testRemoveFavorite() {
        Favorite favorite = new Favorite();
        favorite.setId(1L);

        when(favoriteMapper.findById(1L)).thenReturn(favorite);

        ResponseResult result = favoriteService.removeFavorite(1L);
        assertEquals("取消收藏成功", result.getData());
        verify(favoriteMapper, times(1)).delete(1L);
    }

    @Test
    public void testRemoveFavoriteNotFound() {
        when(favoriteMapper.findById(1L)).thenReturn(null);

        ResponseResult result = favoriteService.removeFavorite(1L);
        assertEquals("收藏不存在", result.getMessage());
    }
}
