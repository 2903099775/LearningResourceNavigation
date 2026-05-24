package com.learning.mapper;

import com.learning.entity.Note;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学习笔记数据访问接口
 * 负责学习笔记相关的数据库操作，包括查询、插入、更新和删除等
 * 引用文件：com.learning.entity.Note
 */
@Mapper
public interface NoteMapper {
    List<Note> findByUserId(@Param("userId") Long userId);

    List<Note> findByUnitId(@Param("unitId") Long unitId);

    Note findById(@Param("id") Long id);

    void insert(Note note);

    void update(Note note);

    void delete(@Param("id") Long id);

    List<Note> findByUserIdAndUnitId(@Param("userId") Long userId, @Param("unitId") Long unitId);

    int countByUserId(@Param("userId") Long userId);
}
