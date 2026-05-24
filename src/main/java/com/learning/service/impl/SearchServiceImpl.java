package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningPath;
import com.learning.entity.LearningUnit;
import com.learning.mapper.LearningPathMapper;
import com.learning.mapper.LearningUnitMapper;
import com.learning.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索服务实现类
 * 提供统一的搜索功能实现，支持同时搜索学习路线和学习单元
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private LearningPathMapper learningPathMapper;

    @Autowired
    private LearningUnitMapper learningUnitMapper;

    @Override
    public ResponseResult search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseResult.error("搜索关键词不能为空");
        }

        String trimmedKeyword = keyword.trim();
        
        List<LearningPath> paths = learningPathMapper.searchByKeyword(trimmedKeyword);
        List<LearningUnit> units = learningUnitMapper.searchByTitle(trimmedKeyword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("paths", paths);
        result.put("units", units);
        result.put("pathCount", paths.size());
        result.put("unitCount", units.size());
        result.put("totalCount", paths.size() + units.size());
        result.put("keyword", trimmedKeyword);
        
        return ResponseResult.success(result);
    }

    @Override
    public ResponseResult searchPaths(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseResult.error("搜索关键词不能为空");
        }

        List<LearningPath> paths = learningPathMapper.searchByKeyword(keyword.trim());
        return ResponseResult.success(paths);
    }

    @Override
    public ResponseResult searchUnits(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseResult.error("搜索关键词不能为空");
        }

        List<LearningUnit> units = learningUnitMapper.searchByTitle(keyword.trim());
        return ResponseResult.success(units);
    }
}
