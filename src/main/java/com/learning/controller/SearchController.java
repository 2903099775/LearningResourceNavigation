package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 搜索控制器
 * 提供统一的搜索API接口，支持搜索学习路线和学习单元
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public ResponseResult search(@RequestParam(required = false) String keyword) {
        return searchService.search(keyword);
    }

    @GetMapping("/paths")
    public ResponseResult searchPaths(@RequestParam(required = false) String keyword) {
        return searchService.searchPaths(keyword);
    }

    @GetMapping("/units")
    public ResponseResult searchUnits(@RequestParam(required = false) String keyword) {
        return searchService.searchUnits(keyword);
    }
}
