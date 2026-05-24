# 项目规则

## 项目重启步骤

项目基于 **Spring Boot 3.2.5 + Java 21 + Maven**，端口 **8080**。

### 完整重启流程

```powershell
# 1. 进入项目目录
cd C:\Users\29030\Documents\trae_projects

# 2. 查找并停止所有 Java 进程
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force

# 3. 编译项目（确保无错误）
mvn compile -q

# 4. 启动 Spring Boot 服务
mvn spring-boot:run
```

### 快速重启（跳过编译）

如果代码没有改动：

```powershell
cd C:\Users\29030\Documents\trae_projects
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force
mvn spring-boot:run
```

### 访问地址

- 首页: http://localhost:8080
- 管理后台: http://localhost:8080/admin

### 五层资源层级结构

```
领域 → 子域 → 路线 → 阶段 → 单元
```

- **领域** (LearningCategory): 编程、数学、英语等顶层分类
- **子域** (LearningSubcategory): 领域下的子分类，如编程→Python、Java
- **路线** (LearningPath): 子域下的学习路线
- **阶段** (LearningStage): 路线下的学习阶段
- **单元** (LearningUnit): 阶段下的具体视频资源

### 关键文件映射

| 页面 | 模板文件 | 控制器 |
|------|---------|--------|
| 首页 | `index.html` | PageController |
| 领域列表 | `learning-domains.html` | LearningCategoryController |
| 子域页面 | `learning-subdomain.html` | LearningSubcategoryController |
| 路线详情 | `learning-path.html` | LearningPathController |
| 单元详情 | `learning-unit.html` | LearningUnitController |
| 管理后台 | `admin.html` | AdminPageController |
