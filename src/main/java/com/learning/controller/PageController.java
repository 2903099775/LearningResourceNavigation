package com.learning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面控制器
 * 负责处理系统中各类页面的路由请求，包括首页、学习路线、学习单元、个人资料、登录注册和管理员页面等
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/learning-path")
    public String learningPath() {
        return "learning-path";
    }

    @GetMapping("/learning-unit")
    public String learningUnit() {
        return "learning-unit";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/admin/edit-path")
    public String adminEditPath() {
        return "admin-edit-path";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
    
    @GetMapping("/devops-learning-path")
    public String devopsLearningPath() {
        return "devops-learning-path";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping("/vip")
    public String vip() {
        return "vip";
    }
    
    @GetMapping("/announcements")
    public String announcements() {
        return "announcements";
    }

    @GetMapping("/community")
    public String community() {
        return "community";
    }

    @GetMapping("/learning-domains")
    public String learningDomains() {
        return "learning-domains";
    }

    @GetMapping("/learning-subdomain")
    public String learningSubdomain() {
        return "learning-subdomain";
    }
}