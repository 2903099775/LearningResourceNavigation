package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.service.PublicStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/stats")
public class PublicStatsController {

    @Autowired
    private PublicStatsService publicStatsService;

    @GetMapping
    public ResponseResult<Object> getHomeStats() {
        return publicStatsService.getHomeStats();
    }
}
