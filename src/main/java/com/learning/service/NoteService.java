package com.learning.service;

import com.learning.common.ResponseResult;
import com.learning.entity.Note;

/**
 * 学习笔记服务接口
 * 定义学习笔记的查询、创建、更新和删除等方法
 * 引用文件：com.learning.common.ResponseResult, com.learning.entity.Note
 */
public interface NoteService {
    ResponseResult<Object> getNotes();
    
    ResponseResult<Object> getNoteById(Long id);
    
    ResponseResult<String> createNote(Note note);
    
    ResponseResult<String> updateNote(Long id, Note note);
    
    ResponseResult<String> deleteNote(Long id);
    
    ResponseResult<Object> getNotesByUnitId(Long unitId);
}