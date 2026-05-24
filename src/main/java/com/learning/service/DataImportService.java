package com.learning.service;

import com.learning.entity.LearningPath;
import com.learning.entity.PathStage;
import com.learning.entity.LearningUnit;
import com.learning.mapper.LearningPathMapper;
import com.learning.mapper.PathStageMapper;
import com.learning.mapper.LearningUnitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据导入服务
 * 负责从学习路线md文件导入数据到数据库
 */
@Service
public class DataImportService {

    @Autowired
    private LearningPathMapper learningPathMapper;

    @Autowired
    private PathStageMapper pathStageMapper;

    @Autowired
    private LearningUnitMapper learningUnitMapper;

    /**
     * 从md文件导入学习路线数据
     */
    public void importLearningPathsFromMd(String filePath) throws IOException {
        String content = Files.readString(Paths.get(filePath));
        
        // 解析前端开发路线
        parseFrontendPath(content);
        
        // 解析后端开发路线
        parseBackendPath(content);
        
        // 解析数据库开发路线
        parseDatabasePath(content);
        
        // 解析运维开发路线
        parseDevopsPath(content);
    }

    private void parseFrontendPath(String content) {
        // 创建前端开发学习路线
        LearningPath frontendPath = new LearningPath();
        frontendPath.setTitle("前端开发");
        frontendPath.setCategoryId(1L);
        frontendPath.setDescription("从零基础到精通的前端开发学习路线，包含HTML5、CSS3、JavaScript、Vue3、React18等核心技术");
        frontendPath.setDifficulty("初级");
        frontendPath.setDurationWeeks(20);
        frontendPath.setIsVipOnly(0);
        frontendPath.setStatus("PUBLISHED");
        frontendPath.setSortOrder(1);
        frontendPath.setCreatedBy(1L);
        frontendPath.setCreatedAt(LocalDateTime.now());
        frontendPath.setUpdatedAt(LocalDateTime.now());
        
        learningPathMapper.insert(frontendPath);
        Long pathId = frontendPath.getId();
        
        // 解析各个阶段
        parseFrontendStages(content, pathId);
    }

    private void parseFrontendStages(String content, Long pathId) {
        // 解析第一阶段：HTML5基础
        PathStage stage1 = createStage(pathId, "HTML5基础", "HTML5基础语法和语义化标签", 14, 1);
        parseHtml5Units(content, stage1.getId());
        
        // 解析第二阶段：CSS3基础
        PathStage stage2 = createStage(pathId, "CSS3基础", "CSS3样式和布局技术", 21, 2);
        parseCss3Units(content, stage2.getId());
        
        // 解析第三阶段：JavaScript基础
        PathStage stage3 = createStage(pathId, "JavaScript基础", "JavaScript编程语言基础", 28, 3);
        parseJavaScriptUnits(content, stage3.getId());
        
        // 解析第四阶段：Vue3框架
        PathStage stage4 = createStage(pathId, "Vue3框架", "Vue3框架和生态系统", 21, 4);
        parseVue3Units(content, stage4.getId());
        
        // 解析第五阶段：React18框架
        PathStage stage5 = createStage(pathId, "React18框架", "React18框架和生态系统", 14, 5);
        parseReactUnits(content, stage5.getId());
        
        // 解析第六阶段：前端工程化
        PathStage stage6 = createStage(pathId, "前端工程化", "前端工程化和性能优化", 14, 6);
        parseFrontendEngineeringUnits(content, stage6.getId());
        
        // 解析第七阶段：前端实战项目
        PathStage stage7 = createStage(pathId, "前端实战项目", "前端项目实战和部署", 14, 7);
        parseFrontendProjectUnits(content, stage7.getId());
    }

    private PathStage createStage(Long pathId, String title, String description, int durationDays, int sortOrder) {
        PathStage stage = new PathStage();
        stage.setPathId(pathId);
        stage.setTitle(title);
        stage.setDescription(description);
        stage.setDurationDays(durationDays);
        stage.setSortOrder(sortOrder);
        stage.setIsLocked(0);
        stage.setCreatedAt(LocalDateTime.now());
        
        pathStageMapper.insert(stage);
        return stage;
    }

    private void parseHtml5Units(String content, Long stageId) {
        // 解析HTML5基础单元
        List<LearningUnit> units = new ArrayList<>();
        
        // 单元1.1：HTML基础结构与语义化
        LearningUnit unit1 = createLearningUnit(stageId, "HTML基础结构与语义化", 
            "理解HTML文档的基本结构，掌握语义化标签的正确使用", 
            "https://www.bilibili.com/video/BV1XJ411X7Ud", "Bilibili", "尚硅谷", 180, 1);
        units.add(unit1);
        
        // 单元1.2：HTML常用标签详解
        LearningUnit unit2 = createLearningUnit(stageId, "HTML常用标签详解", 
            "熟练使用常用HTML标签，掌握链接和图片的使用", 
            "https://www.bilibili.com/video/BV1XJ411X7Ud", "Bilibili", "尚硅谷", 240, 2);
        units.add(unit2);
        
        // 单元1.3：HTML表单与表格
        LearningUnit unit3 = createLearningUnit(stageId, "HTML表单与表格", 
            "掌握表格的创建和结构，熟练使用表单元素", 
            "https://www.bilibili.com/video/BV1XJ411X7Ud", "Bilibili", "尚硅谷", 240, 3);
        units.add(unit3);
        
        // 单元1.4：HTML5新特性
        LearningUnit unit4 = createLearningUnit(stageId, "HTML5新特性", 
            "了解HTML5的新特性，掌握多媒体标签的使用", 
            "https://www.bilibili.com/video/BV1XJ411X7Ud", "Bilibili", "尚硅谷", 180, 4);
        units.add(unit4);
        
        // 批量插入学习单元
        for (LearningUnit unit : units) {
            learningUnitMapper.insert(unit);
        }
    }

    private void parseCss3Units(String content, Long stageId) {
        // 解析CSS3基础单元
        List<LearningUnit> units = new ArrayList<>();
        
        // 单元2.1：CSS基础语法与选择器
        LearningUnit unit1 = createLearningUnit(stageId, "CSS基础语法与选择器", 
            "掌握CSS基础语法，理解选择器的使用", 
            "https://www.bilibili.com/video/BV14J4114768", "Bilibili", "尚硅谷", 240, 1);
        units.add(unit1);
        
        // 单元2.2：CSS盒模型与定位
        LearningUnit unit2 = createLearningUnit(stageId, "CSS盒模型与定位", 
            "理解盒模型，掌握各种定位方式", 
            "https://www.bilibili.com/video/BV14J4114768", "Bilibili", "尚硅谷", 240, 2);
        units.add(unit2);
        
        // 单元2.3：CSS布局技术
        LearningUnit unit3 = createLearningUnit(stageId, "CSS布局技术", 
            "掌握传统布局和现代布局技术", 
            "https://www.bilibili.com/video/BV14J4114768", "Bilibili", "尚硅谷", 240, 3);
        units.add(unit3);
        
        // 单元2.4：CSS3新特性
        LearningUnit unit4 = createLearningUnit(stageId, "CSS3新特性", 
            "掌握CSS3动画、过渡和变换", 
            "https://www.bilibili.com/video/BV14J4114768", "Bilibili", "尚硅谷", 240, 4);
        units.add(unit4);
        
        // 单元2.5：Flexbox布局
        LearningUnit unit5 = createLearningUnit(stageId, "Flexbox布局", 
            "掌握Flexbox弹性布局", 
            "https://www.bilibili.com/video/BV14J4114768", "Bilibili", "尚硅谷", 180, 5);
        units.add(unit5);
        
        // 单元2.6：Grid布局
        LearningUnit unit6 = createLearningUnit(stageId, "Grid布局", 
            "掌握CSS Grid网格布局", 
            "https://www.bilibili.com/video/BV14J4114768", "Bilibili", "尚硅谷", 180, 6);
        units.add(unit6);
        
        for (LearningUnit unit : units) {
            learningUnitMapper.insert(unit);
        }
    }

    private void parseJavaScriptUnits(String content, Long stageId) {
        // 解析JavaScript基础单元
        List<LearningUnit> units = new ArrayList<>();
        
        // 单元3.1：JavaScript基础语法
        LearningUnit unit1 = createLearningUnit(stageId, "JavaScript基础语法", 
            "掌握JavaScript基础语法和数据类型", 
            "https://www.bilibili.com/video/BV1YW411T7GX", "Bilibili", "尚硅谷", 240, 1);
        units.add(unit1);
        
        // 单元3.2：JavaScript函数与作用域
        LearningUnit unit2 = createLearningUnit(stageId, "JavaScript函数与作用域", 
            "理解函数作用域和闭包", 
            "https://www.bilibili.com/video/BV1YW411T7GX", "Bilibili", "尚硅谷", 240, 2);
        units.add(unit2);
        
        // 单元3.3：JavaScript面向对象
        LearningUnit unit3 = createLearningUnit(stageId, "JavaScript面向对象", 
            "掌握面向对象编程", 
            "https://www.bilibili.com/video/BV1YW411T7GX", "Bilibili", "尚硅谷", 240, 3);
        units.add(unit3);
        
        // 单元3.4：DOM操作
        LearningUnit unit4 = createLearningUnit(stageId, "DOM操作", 
            "掌握DOM操作和事件处理", 
            "https://www.bilibili.com/video/BV1YW411T7GX", "Bilibili", "尚硅谷", 240, 4);
        units.add(unit4);
        
        // 单元3.5：BOM操作
        LearningUnit unit5 = createLearningUnit(stageId, "BOM操作", 
            "掌握浏览器对象模型", 
            "https://www.bilibili.com/video/BV1YW411T7GX", "Bilibili", "尚硅谷", 180, 5);
        units.add(unit5);
        
        // 单元3.6：ES6+新特性
        LearningUnit unit6 = createLearningUnit(stageId, "ES6+新特性", 
            "掌握ES6+新特性", 
            "https://www.bilibili.com/video/BV1YW411T7GX", "Bilibili", "尚硅谷", 240, 6);
        units.add(unit6);
        
        for (LearningUnit unit : units) {
            learningUnitMapper.insert(unit);
        }
    }

    private void parseVue3Units(String content, Long stageId) {
        // 解析Vue3框架单元
        List<LearningUnit> units = new ArrayList<>();
        
        // 单元4.1：Vue3基础
        LearningUnit unit1 = createLearningUnit(stageId, "Vue3基础", 
            "掌握Vue3基础语法", 
            "https://www.bilibili.com/video/BV1QA4y1d7xf", "Bilibili", "尚硅谷", 240, 1);
        units.add(unit1);
        
        // 单元4.2：Vue3组件化
        LearningUnit unit2 = createLearningUnit(stageId, "Vue3组件化", 
            "掌握组件化开发", 
            "https://www.bilibili.com/video/BV1QA4y1d7xf", "Bilibili", "尚硅谷", 240, 2);
        units.add(unit2);
        
        // 单元4.3：Vue3组合式API
        LearningUnit unit3 = createLearningUnit(stageId, "Vue3组合式API", 
            "掌握组合式API", 
            "https://www.bilibili.com/video/BV1QA4y1d7xf", "Bilibili", "尚硅谷", 240, 3);
        units.add(unit3);
        
        // 单元4.4：Vue Router
        LearningUnit unit4 = createLearningUnit(stageId, "Vue Router", 
            "掌握路由管理", 
            "https://www.bilibili.com/video/BV1QA4y1d7xf", "Bilibili", "尚硅谷", 180, 4);
        units.add(unit4);
        
        // 单元4.5：Pinia状态管理
        LearningUnit unit5 = createLearningUnit(stageId, "Pinia状态管理", 
            "掌握状态管理", 
            "https://www.bilibili.com/video/BV1QA4y1d7xf", "Bilibili", "尚硅谷", 180, 5);
        units.add(unit5);
        
        for (LearningUnit unit : units) {
            learningUnitMapper.insert(unit);
        }
    }

    private void parseReactUnits(String content, Long stageId) {
        // 解析React18框架单元
        List<LearningUnit> units = new ArrayList<>();
        
        // 单元5.1：React18基础
        LearningUnit unit1 = createLearningUnit(stageId, "React18基础", 
            "掌握React基础语法", 
            "https://www.bilibili.com/video/BV1dP4y1c7qd", "Bilibili", "尚硅谷", 240, 1);
        units.add(unit1);
        
        // 单元5.2：React Hooks
        LearningUnit unit2 = createLearningUnit(stageId, "React Hooks", 
            "掌握Hooks使用", 
            "https://www.bilibili.com/video/BV1dP4y1c7qd", "Bilibili", "尚硅谷", 240, 2);
        units.add(unit2);
        
        // 单元5.3：Redux状态管理
        LearningUnit unit3 = createLearningUnit(stageId, "Redux状态管理", 
            "掌握状态管理", 
            "https://www.bilibili.com/video/BV1dP4y1c7qd", "Bilibili", "尚硅谷", 180, 3);
        units.add(unit3);
        
        for (LearningUnit unit : units) {
            learningUnitMapper.insert(unit);
        }
    }

    private void parseFrontendEngineeringUnits(String content, Long stageId) {
        // 解析前端工程化单元
        List<LearningUnit> units = new ArrayList<>();
        
        // 单元6.1：前端工程化基础
        LearningUnit unit1 = createLearningUnit(stageId, "前端工程化基础", 
            "掌握工程化概念", 
            "https://www.bilibili.com/video/BV1GAWDe7E3k", "Bilibili", "尚硅谷", 180, 1);
        units.add(unit1);
        
        // 单元6.2：Vite构建工具
        LearningUnit unit2 = createLearningUnit(stageId, "Vite构建工具", 
            "掌握Vite使用", 
            "https://www.bilibili.com/video/BV1GAWDe7E3k", "Bilibili", "尚硅谷", 180, 2);
        units.add(unit2);
        
        // 单元6.3：TypeScript
        LearningUnit unit3 = createLearningUnit(stageId, "TypeScript", 
            "掌握TypeScript", 
            "https://www.bilibili.com/video/BV1GAWDe7E3k", "Bilibili", "尚硅谷", 240, 3);
        units.add(unit3);
        
        for (LearningUnit unit : units) {
            learningUnitMapper.insert(unit);
        }
    }

    private void parseFrontendProjectUnits(String content, Long stageId) {
        // 解析前端实战项目单元
        List<LearningUnit> units = new ArrayList<>();
        
        // 单元7.1：后台管理系统
        LearningUnit unit1 = createLearningUnit(stageId, "后台管理系统", 
            "实战项目开发", 
            "https://www.bilibili.com/video/BV1GAWDe7E3k", "Bilibili", "尚硅谷", 480, 1);
        units.add(unit1);
        
        // 单元7.2：移动端商城
        LearningUnit unit2 = createLearningUnit(stageId, "移动端商城", 
            "移动端项目开发", 
            "https://www.bilibili.com/video/BV1GAWDe7E3k", "Bilibili", "尚硅谷", 480, 2);
        units.add(unit2);
        
        for (LearningUnit unit : units) {
            learningUnitMapper.insert(unit);
        }
    }

    private void parseBackendPath(String content) {
        // 创建后端开发学习路线
        LearningPath backendPath = new LearningPath();
        backendPath.setTitle("后端开发");
        backendPath.setCategoryId(2L);
        backendPath.setDescription("从Java基础到Spring Boot微服务的后端开发学习路线");
        backendPath.setDifficulty("中级");
        backendPath.setDurationWeeks(24);
        backendPath.setIsVipOnly(0);
        backendPath.setStatus("PUBLISHED");
        backendPath.setSortOrder(2);
        backendPath.setCreatedBy(1L);
        backendPath.setCreatedAt(LocalDateTime.now());
        backendPath.setUpdatedAt(LocalDateTime.now());
        
        learningPathMapper.insert(backendPath);
        // TODO: 解析后端开发阶段和单元
    }

    private void parseDatabasePath(String content) {
        // 创建数据库开发学习路线
        LearningPath databasePath = new LearningPath();
        databasePath.setTitle("数据库开发");
        databasePath.setCategoryId(3L);
        databasePath.setDescription("MySQL、Redis等数据库技术学习路线");
        databasePath.setDifficulty("初级");
        databasePath.setDurationWeeks(12);
        databasePath.setIsVipOnly(0);
        databasePath.setStatus("PUBLISHED");
        databasePath.setSortOrder(3);
        databasePath.setCreatedBy(1L);
        databasePath.setCreatedAt(LocalDateTime.now());
        databasePath.setUpdatedAt(LocalDateTime.now());
        
        learningPathMapper.insert(databasePath);
        // TODO: 解析数据库开发阶段和单元
    }

    private void parseDevopsPath(String content) {
        // 创建运维开发学习路线
        LearningPath devopsPath = new LearningPath();
        devopsPath.setTitle("运维开发");
        devopsPath.setCategoryId(4L);
        devopsPath.setDescription("Docker、Kubernetes等运维技术学习路线");
        devopsPath.setDifficulty("高级");
        devopsPath.setDurationWeeks(16);
        devopsPath.setIsVipOnly(0);
        devopsPath.setStatus("PUBLISHED");
        devopsPath.setSortOrder(4);
        devopsPath.setCreatedBy(1L);
        devopsPath.setCreatedAt(LocalDateTime.now());
        devopsPath.setUpdatedAt(LocalDateTime.now());
        
        learningPathMapper.insert(devopsPath);
        // TODO: 解析运维开发阶段和单元
    }

    private LearningUnit createLearningUnit(Long stageId, String title, String description, 
                                          String externalUrl, String platform, String author, 
                                          int durationMinutes, int sortOrder) {
        LearningUnit unit = new LearningUnit();
        unit.setStageId(stageId);
        unit.setTitle(title);
        unit.setType("VIDEO");
        unit.setContentType("TUTORIAL");
        unit.setExternalUrl(externalUrl);
        unit.setPlatform(platform);
        unit.setAuthor(author);
        unit.setDurationMinutes(durationMinutes);
        unit.setDescription(description);
        unit.setSortOrder(sortOrder);
        unit.setIsVipOnly(0);
        unit.setStatus(1);
        unit.setViewCount(0);
        unit.setCreatedAt(LocalDateTime.now());
        
        return unit;
    }
}