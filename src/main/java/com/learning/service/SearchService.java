package com.learning.service;

import com.learning.common.ResponseResult;

/**
 * 搜索服务接口
 * 提供统一的搜索功能，支持同时搜索学习路线和学习单元
 */
public interface SearchService {
    
    ResponseResult search(String keyword);
    
    ResponseResult searchPaths(String keyword);
    
    ResponseResult searchUnits(String keyword);
}
