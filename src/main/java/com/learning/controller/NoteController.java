package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.entity.Note;
import com.learning.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 学习笔记控制器
 * 负责处理学习笔记相关的API请求，包括获取、创建、更新和删除等操作
 * 引用文件：com.learning.common.ResponseResult, com.learning.entity.Note, com.learning.service.NoteService
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    /** 获取当前用户所有笔记 */
    @GetMapping
    public ResponseEntity<ResponseResult<Object>> getNotes() {
        return ResponseEntity.ok(noteService.getNotes());
    }

    /** 根据ID获取单条笔记 */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseResult<Object>> getNoteById(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNoteById(id));
    }

    /** 创建笔记（userId 在 Service 层从 SecurityContext 获取） */
    @PostMapping
    public ResponseEntity<ResponseResult<String>> createNote(@RequestBody Note note) {
        return ResponseEntity.ok(noteService.createNote(note));
    }

    /** 更新笔记（Service 层校验所有权） */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseResult<String>> updateNote(@PathVariable Long id, @RequestBody Note note) {
        return ResponseEntity.ok(noteService.updateNote(id, note));
    }

    /** 删除笔记（Service 层校验所有权） */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseResult<String>> deleteNote(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.deleteNote(id));
    }

    /** 根据学习单元ID获取当前用户笔记 */
    @GetMapping("/unit/{unitId}")
    public ResponseEntity<ResponseResult<Object>> getNotesByUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(noteService.getNotesByUnitId(unitId));
    }
}
