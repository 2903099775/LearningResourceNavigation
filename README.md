# 学习资源导航系统

## 项目简介

基于Spring Boot的学习资源导航系统，旨在为学习者提供高质量的学习资源和系统化的学习路径，帮助用户更高效、更系统地进行自学。

## 技术栈

- Spring Boot 3.2.5
- MyBatis 3.5.15
- MySQL 8.0.33
- Spring Security 6.0
- JWT 0.11.5
- Thymeleaf
- Druid 1.2.18

## 核心功能

1. **用户认证与授权**
   - 注册、登录、登出
   - JWT令牌认证
   - 角色权限管理（普通用户、VIP用户、管理员）

2. **学习路径管理**
   - 查看所有学习路径
   - 查看学习路径详情
   - 查看学习路径的学习单元
   - 查看学习进度

3. **学习单元管理**
   - 查看学习单元详情
   - 标记学习单元为已完成
   - 记录学习进度

4. **评论与收藏**
   - 对学习路径和学习单元发表评论
   - 收藏学习路径和学习单元

5. **用户管理**
   - 个人资料管理
   - VIP会员购买

6. **管理员功能**
   - 学习路径管理
   - 用户管理
   - 数据统计

## 项目结构

```
src/
├── main/
│   ├── java/com/learning/
│   │   ├── common/         # 公共类
│   │   ├── config/         # 配置类
│   │   ├── controller/     # 控制器
│   │   ├── entity/         # 实体类
│   │   ├── exception/      # 异常处理
│   │   ├── mapper/         # MyBatis映射器
│   │   ├── service/        # 服务层
│   │   ├── util/           # 工具类
│   │   └── LearningResourceNavigationApplication.java  # 应用入口
│   └── resources/
│       ├── db/             # 数据库脚本
│       ├── mapper/         # MyBatis XML映射文件
│       ├── templates/      # Thymeleaf模板
│       ├── application.properties  # 应用配置
│       └── log4j2.xml      # 日志配置
└── test/                   # 测试代码
```

## 安装与部署

### 前提条件

- JDK 21
- Maven 3.8+
- MySQL 8.0+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <项目地址>
   cd learning-resource-navigation
   ```

2. **创建数据库**
   ```sql
   CREATE DATABASE learning_resource_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **执行数据库脚本**
   执行 `src/main/resources/db/schema.sql` 文件创建表结构。

4. **配置数据库连接**
   修改 `src/main/resources/application.properties` 文件中的数据库连接信息：
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/learning_resource_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
   spring.datasource.username=root
   spring.datasource.password=123456
   ```

5. **构建项目**
   ```bash
   mvn clean package
   ```

6. **运行项目**
   ```bash
   java -jar target/learning-resource-navigation-0.0.1-SNAPSHOT.jar
   ```

## API文档

### 认证相关

- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出

### 学习路径相关

- `GET /api/paths` - 获取所有学习路径
- `GET /api/paths/{id}` - 获取学习路径详情
- `GET /api/paths/{id}/stages` - 获取学习路径的学习单元
- `GET /api/paths/{id}/progress` - 获取学习路径的学习进度

### 学习单元相关

- `GET /api/units` - 获取所有学习单元
- `GET /api/units/{id}` - 获取学习单元详情

### 评论相关

- `GET /api/comments` - 获取评论列表
- `POST /api/comments` - 发表评论

### 收藏相关

- `GET /api/favorites` - 获取收藏列表
- `POST /api/favorites` - 添加收藏
- `DELETE /api/favorites/{id}` - 删除收藏

## 安全注意事项

1. **JWT密钥管理**
   - 生产环境中应使用环境变量设置JWT密钥，避免硬编码
   - 定期更换JWT密钥

2. **密码安全**
   - 使用BCryptPasswordEncoder进行密码加密
   - 要求密码强度至少8位，包含大小写字母和数字

3. **访问控制**
   - 使用Spring Security进行细粒度的访问控制
   - 敏感操作需要管理员权限

4. **输入验证**
   - 对所有用户输入进行验证，防止SQL注入、XSS等攻击

## 性能优化

1. **数据库优化**
   - 使用索引优化查询性能
   - 合理设计表结构，避免冗余数据

2. **缓存优化**
   - 对频繁访问的数据使用缓存
   - 合理设置缓存过期时间

3. **代码优化**
   - 减少数据库查询次数
   - 优化算法和数据结构

## 开发规范

1. **代码风格**
   - 遵循Java代码规范
   - 使用lombok减少样板代码

2. **命名规范**
   - 类名使用大驼峰命名法
   - 方法名和变量名使用小驼峰命名法
   - 常量使用全大写字母，下划线分隔

3. **注释规范**
   - 类和方法添加必要的注释
   - 复杂逻辑添加详细注释

## 联系方式

- 邮箱：2903099775@qq.com
- QQ群：2903099775

## 许可证

本项目采用MIT许可证。
