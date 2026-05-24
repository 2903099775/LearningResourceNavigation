package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.mapper.LearningPathMapper;
import com.learning.mapper.LearningUnitMapper;
import com.learning.mapper.UserMapper;
import com.learning.service.PublicStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PublicStatsServiceImpl implements PublicStatsService {

    @Autowired
    private LearningPathMapper learningPathMapper;

    @Autowired
    private LearningUnitMapper learningUnitMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Cacheable(value = "homeStats", unless = "#result.code != 200")
    public ResponseResult getHomeStats() {
        int totalPaths = learningPathMapper.countPublished();
        int totalResources = learningUnitMapper.countTotal();
        int totalLearners = userMapper.countTotal();

        Map<String, Object> stats = new HashMap<>();
        stats.put("paths", totalPaths);
        stats.put("resources", totalResources);
        stats.put("learners", totalLearners);

        return ResponseResult.success(stats);
    }
}
