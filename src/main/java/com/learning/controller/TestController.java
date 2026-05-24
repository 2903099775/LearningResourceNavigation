package com.learning.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 负责处理测试相关的API请求，包括ping和echo等测试接口
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/ping")
    public Map<String, String> ping() {
        log.info("收到ping请求");
        Map<String, String> response = new HashMap<>();
        response.put("message", "pong");
        return response;
    }

    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> request) {
        log.info("收到echo请求: {}", request);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "echo");
        response.put("data", request);
        return response;
    }
}
