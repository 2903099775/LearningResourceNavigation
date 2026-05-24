package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.entity.Note;
import com.learning.entity.User;
import com.learning.mapper.NoteMapper;
import com.learning.mapper.UserMapper;
import com.learning.service.AchievementService;
import com.learning.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习笔记服务实现类
 * 负责处理学习笔记的查询、创建、更新和删除等方法的具体实现
 * 引用文件：com.learning.common.ResponseResult, com.learning.entity.Note, com.learning.entity.User,
 *           com.learning.mapper.NoteMapper, com.learning.mapper.UserMapper, com.learning.service.NoteService
 */
@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AchievementService achievementService;

    @Override
    public ResponseResult<Object> getNotes() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        List<Note> notes = noteMapper.findByUserId(userId);
        return ResponseResult.success(notes);
    }

    @Override
    public ResponseResult<Object> getNoteById(Long id) {
        Note note = noteMapper.findById(id);
        if (note == null) {
            return ResponseResult.error("笔记不存在");
        }
        Long userId = getCurrentUserId();
        if (userId == null || !userId.equals(note.getUserId())) {
            return ResponseResult.error("无权查看该笔记");
        }
        return ResponseResult.success(note);
    }

    @Override
    public ResponseResult<String> createNote(Note note) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        if (!isVip(userId)) {
            return ResponseResult.error("VIP会员才可以使用学习笔记功能，请先开通VIP");
        }
        note.setUserId(userId);
        if (note.getIsFavorite() == null) {
            note.setIsFavorite(0);
        }
        LocalDateTime now = LocalDateTime.now();
        note.setCreatedAt(now);
        note.setUpdatedAt(now);
        noteMapper.insert(note);

        achievementService.checkAndUnlockAchievements(userId);

        return ResponseResult.success("创建笔记成功");
    }

    @Override
    public ResponseResult<String> updateNote(Long id, Note note) {
        Note existing = noteMapper.findById(id);
        if (existing == null) {
            return ResponseResult.error("笔记不存在");
        }
        Long userId = getCurrentUserId();
        if (userId == null || !userId.equals(existing.getUserId())) {
            return ResponseResult.error("无权修改该笔记");
        }
        if (!isVip(userId)) {
            return ResponseResult.error("VIP会员才可以使用学习笔记功能，请先开通VIP");
        }
        note.setId(id);
        note.setUserId(userId);
        if (note.getIsFavorite() == null) {
            note.setIsFavorite(existing.getIsFavorite());
        }
        note.setUpdatedAt(LocalDateTime.now());
        noteMapper.update(note);
        return ResponseResult.success("更新笔记成功");
    }

    @Override
    public ResponseResult<String> deleteNote(Long id) {
        Note existing = noteMapper.findById(id);
        if (existing == null) {
            return ResponseResult.error("笔记不存在");
        }
        Long userId = getCurrentUserId();
        if (userId == null || !userId.equals(existing.getUserId())) {
            return ResponseResult.error("无权删除该笔记");
        }
        noteMapper.delete(id);
        return ResponseResult.success("删除笔记成功");
    }

    @Override
    public ResponseResult<Object> getNotesByUnitId(Long unitId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        List<Note> notes = noteMapper.findByUserIdAndUnitId(userId, unitId);
        return ResponseResult.success(notes);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String principal = auth.getName();
        if ("anonymousUser".equals(principal)) {
            return null;
        }
        User user = userMapper.findByUsername(principal);
        return user != null ? user.getId() : null;
    }

    private boolean isVip(Long userId) {
        if (userId == null) return false;
        User user = userMapper.findById(userId);
        if (user == null) return false;

        // 管理员自动拥有VIP权限
        if ("ADMIN".equals(user.getRole())) {
            return true;
        }

        if (user.getVipExpireDate() == null) return false;
        return user.getVipExpireDate().compareTo(java.time.LocalDate.now()) >= 0;
    }
}
