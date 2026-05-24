package com.learning.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 管理员页面控制器
 * 负责处理管理员页面的路由请求，包括学习路线管理页面等
 */
@Controller
public class AdminPageController {

    private static final Logger log = LoggerFactory.getLogger(AdminPageController.class);

    @GetMapping("/admin-paths")
    public String adminPaths() {
        log.info("访问管理员学习路线管理页面");
        // 登录状态检查由前端处理
        return "admin-paths";
    }
}