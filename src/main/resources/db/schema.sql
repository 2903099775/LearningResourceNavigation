-- 删除已存在的表
DROP TABLE IF EXISTS feedbacks;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS path_tags;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS user_notes;
DROP TABLE IF EXISTS user_progress;
DROP TABLE IF EXISTS learning_units;
DROP TABLE IF EXISTS path_stages;
DROP TABLE IF EXISTS learning_paths;
DROP TABLE IF EXISTS user_notifications;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS users;

-- 用户表 (users)
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    avatar VARCHAR(255) COMMENT '头像URL',
    role ENUM('USER', 'ADMIN') DEFAULT 'USER' COMMENT '角色',
    vip_expire_date DATE COMMENT 'VIP到期日',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_role ON users(role);
CREATE INDEX idx_vip_expire_date ON users(vip_expire_date);
CREATE INDEX idx_status ON users(status);
CREATE INDEX idx_created_at ON users(created_at);

-- 社区帖子表 (posts)
CREATE TABLE posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '发帖用户ID',
    title VARCHAR(200) COMMENT '帖子标题',
    content TEXT COMMENT '帖子内容',
    images LONGTEXT COMMENT '图片URLs，逗号分隔或base64',
    likes_count INT DEFAULT 0 COMMENT '点赞数',
    comments_count INT DEFAULT 0 COMMENT '评论数',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0已删除',
    is_pinned TINYINT DEFAULT 0 COMMENT '是否置顶：1是 0否',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_status ON posts(status);
CREATE INDEX idx_posts_created_at ON posts(created_at);

-- 学习路线表 (learning_paths)
CREATE TABLE learning_paths (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL COMMENT '路线标题',
    category_id BIGINT COMMENT '分类ID',
    unit_id BIGINT COMMENT '归属学习单元ID',
    description TEXT COMMENT '路线描述',
    cover_image VARCHAR(255) COMMENT '封面图',
    difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') COMMENT '难度',
    duration_weeks INT COMMENT '预计周数',
    is_vip_only TINYINT DEFAULT 0 COMMENT '是否VIP专属',
    status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') DEFAULT 'DRAFT',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_by BIGINT COMMENT '创建者ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_status ON learning_paths(status);
CREATE INDEX idx_category_id ON learning_paths(category_id);
CREATE INDEX idx_unit_id ON learning_paths(unit_id);
CREATE INDEX idx_created_by ON learning_paths(created_by);
CREATE INDEX idx_difficulty ON learning_paths(difficulty);
CREATE INDEX idx_is_vip_only ON learning_paths(is_vip_only);

-- 学习阶段表 (path_stages)
CREATE TABLE path_stages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    path_id BIGINT NOT NULL COMMENT '所属路线ID',
    title VARCHAR(100) NOT NULL COMMENT '阶段标题',
    description TEXT COMMENT '阶段描述',
    duration_days INT COMMENT '预计天数',
    sort_order INT DEFAULT 0,
    is_locked TINYINT DEFAULT 0 COMMENT '是否锁定',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_path_id ON path_stages(path_id);

-- 学习单元/资源表 (learning_units)
CREATE TABLE learning_units (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stage_id BIGINT NOT NULL COMMENT '所属阶段ID',
    title VARCHAR(100) NOT NULL COMMENT '单元标题',
    type ENUM('VIDEO', 'ARTICLE', 'DOC', 'PROJECT') COMMENT '资源类型',
    content_type ENUM('EXTERNAL') DEFAULT 'EXTERNAL' COMMENT '内容类型（系统只支持外部资源）',
    external_url VARCHAR(500) COMMENT '外部链接',
    platform VARCHAR(50) COMMENT '平台名称(B站/慕课网等)',
    author VARCHAR(100) COMMENT '资源作者',
    duration_minutes INT COMMENT '时长(分钟)',
    description TEXT COMMENT '描述',
    sort_order INT DEFAULT 0,
    is_vip_only TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1,
    view_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stage_id ON learning_units(stage_id);
CREATE INDEX idx_type ON learning_units(type);
CREATE INDEX idx_is_vip_only ON learning_units(is_vip_only);

-- 用户学习进度表 (user_progress)
CREATE TABLE user_progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    unit_id BIGINT NOT NULL,
    path_id BIGINT NOT NULL,
    status ENUM('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'NOT_STARTED',
    start_time TIMESTAMP NULL COMMENT '开始时间',
    complete_time TIMESTAMP NULL COMMENT '完成时间',
    last_study_time TIMESTAMP NULL COMMENT '最近学习时间',
    study_duration INT DEFAULT 0 COMMENT '学习时长(秒)',
    visit_count INT DEFAULT 0 COMMENT '访问次数',
    rating INT DEFAULT 0 COMMENT '用户评分(1-5星)',
    is_favorite TINYINT DEFAULT 0 COMMENT '是否收藏(0-未收藏, 1-已收藏)'
);

CREATE UNIQUE INDEX uk_user_unit_path ON user_progress(user_id, unit_id, path_id);
CREATE INDEX idx_user_path ON user_progress(user_id, path_id);
CREATE INDEX idx_user_last_study ON user_progress(user_id, last_study_time DESC);
CREATE INDEX idx_path ON user_progress(path_id);
CREATE INDEX idx_status_complete_time ON user_progress(status, complete_time);
CREATE INDEX idx_last_study_time ON user_progress(last_study_time);

-- 用户笔记表 (user_notes)
CREATE TABLE user_notes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    unit_id BIGINT NOT NULL COMMENT '关联学习单元',
    path_id BIGINT COMMENT '关联路线',
    title VARCHAR(200) COMMENT '笔记标题',
    content TEXT COMMENT '笔记内容(富文本)',
    is_favorite TINYINT DEFAULT 0 COMMENT '是否收藏',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 评论表 (comments)
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_type ENUM('PATH', 'UNIT') COMMENT '评论对象类型',
    target_id BIGINT NOT NULL COMMENT '对象ID',
    content TEXT NOT NULL,
    parent_id BIGINT COMMENT '父评论ID(回复)',
    likes_count INT DEFAULT 0,
    status TINYINT DEFAULT 1 COMMENT '1正常 0隐藏',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶 1-置顶 0-不置顶',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_created_at ON comments(created_at);

-- 收藏表 (favorites)
CREATE TABLE favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_type ENUM('PATH', 'UNIT', 'NOTE') COMMENT '收藏类型',
    target_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_user_target ON favorites(user_id, target_type, target_id);
CREATE INDEX idx_created_at ON favorites(created_at);

-- 标签表 (tags)
CREATE TABLE tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    category VARCHAR(50) COMMENT '标签分类',
    usage_count INT DEFAULT 0
);

-- 路线-标签关联表 (path_tags)
CREATE TABLE path_tags (
    path_id BIGINT,
    tag_id BIGINT,
    PRIMARY KEY (path_id, tag_id)
);

-- 支付记录表 (payments)
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_no VARCHAR(64) UNIQUE NOT NULL COMMENT '订单号',
    amount DECIMAL(10,2) COMMENT '金额',
    months INT COMMENT '购买月数',
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED') DEFAULT 'PENDING',
    pay_time TIMESTAMP COMMENT '支付时间',
    expire_date DATE COMMENT 'VIP到期日',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 退款申请表 (refund_requests)
CREATE TABLE refund_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_no VARCHAR(100) NOT NULL COMMENT '关联支付订单号',
    reason TEXT NOT NULL COMMENT '退款原因',
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING' COMMENT '状态: PENDING-待审核, APPROVED-已批准, REJECTED-已拒绝',
    admin_remark TEXT COMMENT '管理员备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL COMMENT '处理时间'
);

CREATE INDEX idx_refund_user ON refund_requests(user_id);
CREATE INDEX idx_refund_status ON refund_requests(status);

-- 反馈表 (feedbacks)
CREATE TABLE feedbacks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    type ENUM('BUG', 'FEATURE', 'RESOURCE', 'OTHER') COMMENT '反馈类型',
    title VARCHAR(200),
    content TEXT,
    contact VARCHAR(100),
    status ENUM('PENDING', 'PROCESSING', 'RESOLVED', 'CLOSED') DEFAULT 'PENDING',
    reply TEXT COMMENT '回复内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL
);

-- 用户通知表 (user_notifications)
CREATE TABLE user_notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    related_type VARCHAR(50),
    related_id BIGINT,
    is_read TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP
);

-- 公告表 (announcements) - 课程预告/功能更新/系统通知
CREATE TABLE announcements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT COMMENT '发布管理员ID',
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT COMMENT '公告内容',
    type ENUM('announcement', 'feature', 'notice') DEFAULT 'announcement' COMMENT '类型：课程预告/功能更新/系统通知',
    status TINYINT DEFAULT 0 COMMENT '状态：0草稿 1已发布',
    priority INT DEFAULT 1 COMMENT '优先级：1普通 2置顶 3紧急',
    publish_at TIMESTAMP NULL COMMENT '发布时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户期待表 (wishes)
CREATE TABLE wishes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) COMMENT '用户名',
    title VARCHAR(200) NOT NULL COMMENT '期待标题',
    description TEXT COMMENT '详细描述',
    category ENUM('course', 'feature') DEFAULT 'course' COMMENT '类型：课程期待/功能建议',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态：1待处理 2已采纳 3已上线',
    admin_reply TEXT COMMENT '管理员回复',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户点赞关联表 (wish_likes)
CREATE TABLE wish_likes (
    wish_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (wish_id, user_id)
);

-- 初始化测试数据
-- 1. 用户数据
-- 密码均为：123456
INSERT INTO users (username, email, password, role, status, vip_expire_date) VALUES
('admin', 'admin@example.com', '$2a$10$DfzpQactDyfo8/GgYBdgCO3pG40H9bOKXsqMfodresD6I7SCFnqiq', 'ADMIN', 1, NULL),
('user1', 'user1@example.com', '$2a$10$DfzpQactDyfo8/GgYBdgCO3pG40H9bOKXsqMfodresD6I7SCFnqiq', 'USER', 1, NULL),
('vip1', 'vip1@example.com', '$2a$10$DfzpQactDyfo8/GgYBdgCO3pG40H9bOKXsqMfodresD6I7SCFnqiq', 'USER', 1, '2026-12-31'),
('test', 'test@example.com', '$2a$10$DfzpQactDyfo8/GgYBdgCO3pG40H9bOKXsqMfodresD6I7SCFnqiq', 'USER', 1, NULL);

-- 初始化支付记录
INSERT INTO payments (user_id, order_no, amount, months, status, pay_time, expire_date, created_at) VALUES
(3, 'VIP202605230001', 99.00, 12, 'SUCCESS', '2026-05-20 10:00:00', '2026-12-31', '2026-05-20 10:00:00');

-- 2. 学习路线数据
INSERT INTO learning_paths (title, category_id, description, cover_image, difficulty, duration_weeks, is_vip_only, status, sort_order, created_by) VALUES
('前端开发学习路线', 1, '从HTML/CSS基础到React/Vue框架的完整前端开发学习路径', 'https://example.com/frontend.jpg', 'BEGINNER', 8, 0, 'PUBLISHED', 1, 1),
('后端开发学习路线', 2, '从Java基础到Spring Boot框架的完整后端开发学习路径', 'https://example.com/backend.jpg', 'BEGINNER', 10, 0, 'PUBLISHED', 2, 1),
('数据库学习路线', 3, '从SQL基础到NoSQL与数据库生态的完整学习路径', 'https://example.com/database.jpg', 'BEGINNER', 6, 0, 'PUBLISHED', 3, 1);

-- 3. 学习阶段数据
INSERT INTO path_stages (path_id, title, description, duration_days, sort_order, is_locked) VALUES
-- 前端开发学习路线阶段
(1, '前端基础（HTML/CSS/JavaScript）', '学习HTML基础标签、CSS选择器和布局、JavaScript基础语法和DOM操作', 30, 1, 0),
(1, '前端框架与工程化', '学习Vue.js或React框架、前端工程化（Webpack/Vite）、组件化开发和状态管理', 20, 2, 0),
(1, '前端进阶与专项提升', '学习前端性能优化、TypeScript、浏览器原理和前端安全', 10, 3, 0),
-- 后端开发学习路线阶段
(2, 'Java基础', '学习Java语法基础、面向对象编程、集合框架、多线程和IO流', 30, 1, 0),
(2, 'JavaWeb与数据库', '学习MySQL数据库、JDBC、Servlet/JSP、Tomcat和Git版本控制', 20, 2, 0),
(2, '企业级框架', '学习Spring框架、SpringMVC、MyBatis、Spring Boot和Spring Cloud', 20, 3, 0),
-- 数据库学习路线阶段
(3, 'SQL基础', '学习SQL语法基础、数据查询、多表连接、分组查询与聚合函数', 14, 1, 0),
(3, 'MySQL进阶', '学习索引优化、事务与并发、存储过程与触发器、数据库设计', 21, 2, 0),
(3, 'NoSQL与数据库生态', '学习Redis缓存、MongoDB、数据库集群、数据备份与恢复', 21, 3, 0);

-- 4. 学习单元数据
INSERT INTO learning_units (stage_id, title, type, content_type, external_url, platform, author, duration_minutes, description, sort_order, is_vip_only, status) VALUES
-- 前端开发学习路线 - 前端基础阶段
(1, '尚硅谷HTML5+CSS3零基础教程', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1XJ411X7Ud', 'B站', '尚硅谷', 600, '系统全面的HTML5和CSS3教程，适合零基础入门', 1, 0, 1),
(1, 'JavaScript基础语法教程', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1Sy4y1C7ha', 'B站', '尚硅谷', 400, '详细讲解JavaScript核心概念和语法', 2, 0, 1),
-- 前端开发学习路线 - 前端框架与工程化阶段
(2, '尚硅谷Vue3教程', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1Zy4y1K7SH', 'B站', '尚硅谷', 500, '系统讲解Vue3核心特性和实战', 1, 0, 1),
(2, 'React零基础教程', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1wy4y1D7JT', 'B站', '尚硅谷', 450, '从基础到进阶，包含Hooks等新特性', 2, 0, 1),
-- 前端开发学习路线 - 前端进阶与专项提升阶段
(3, '前端性能优化实战', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1aE411e7fe', 'B站', '尚硅谷', 300, '深入讲解性能优化策略和实践', 1, 0, 1),
(3, 'TypeScript入门教程', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1Xy4y1v7S2', 'B站', '尚硅谷', 250, '系统学习TypeScript核心概念', 2, 0, 1),
-- 后端开发学习路线 - Java基础阶段
(4, '尚硅谷Java入门视频教程', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1Kb411W75N', 'B站', '尚硅谷', 1470, '147小时完整教程，包含5个练习项目', 1, 0, 1),
(4, '韩顺平Java基础教程', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV17F411T7Ao', 'B站', '韩顺平', 800, '讲解生动有趣，适合零基础', 2, 0, 1),
-- 后端开发学习路线 - JavaWeb与数据库阶段
(5, '尚硅谷JavaWeb零基础入门完整版', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1Y7411K7zz', 'B站', '尚硅谷', 460, '46小时教程，包含网上书城项目', 1, 0, 1),
(5, 'MySQL基础教程', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV12b411K7Zu', 'B站', '尚硅谷', 300, '详细讲解MySQL核心概念和操作', 2, 0, 1),
-- 后端开发学习路线 - 企业级框架阶段
(6, '遇见狂神说Spring5教程', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1WE411d7Dv', 'B站', '狂神说', 200, '简洁易懂，适合快速上手', 1, 0, 1),
(6, 'SpringBoot最新教程', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1PE411i7CV', 'B站', '尚硅谷', 300, '详细讲解SpringBoot核心特性', 2, 0, 1),
-- 数据库学习路线 - SQL基础阶段
(7, '数据分析SQL零基础入门', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1DA411e7QU', 'B站', '尚硅谷', 200, '专为数据分析人员设计，实战性强', 1, 0, 1),
-- 数据库学习路线 - MySQL进阶阶段
(8, 'MySQL高级教程', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV12b411K7Zu', 'B站', '尚硅谷', 300, '深入讲解MySQL高级特性', 1, 0, 1),
(8, '数据库系统概论', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1UQ4y1P7rX', 'B站', '尚硅谷', 400, '系统讲解数据库原理', 2, 0, 1),
-- 数据库学习路线 - NoSQL与数据库生态阶段
(9, 'Redis入门到精通', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1S54y1R7SB', 'B站', '尚硅谷', 300, '详细讲解Redis核心功能和应用', 1, 0, 1),
(9, 'MongoDB基础教程', 'VIDEO', 'EXTERNAL', 'https://www.bilibili.com/video/BV1U4411v7Ug', 'B站', '尚硅谷', 250, '系统学习MongoDB特性', 2, 0, 1);

-- 5. 用户学习进度数据
INSERT INTO user_progress (user_id, unit_id, path_id, status, start_time, complete_time, last_study_time, study_duration) VALUES
(2, 1, 1, 'COMPLETED', '2026-04-01 09:00:00', '2026-04-02 10:30:00', '2026-04-02 10:30:00', 120),
(2, 2, 1, 'IN_PROGRESS', '2026-04-03 08:00:00', NULL, '2026-04-10 14:00:00', 60),
(3, 1, 1, 'COMPLETED', '2026-04-01 10:00:00', '2026-04-01 12:00:00', '2026-04-01 12:00:00', 90),
(3, 2, 1, 'COMPLETED', '2026-04-02 14:00:00', '2026-04-02 16:30:00', '2026-04-02 16:30:00', 120),
(3, 3, 1, 'IN_PROGRESS', '2026-04-04 09:00:00', NULL, '2026-04-10 15:00:00', 45);

-- 6. 评论数据
INSERT INTO comments (user_id, target_type, target_id, content, parent_id, likes_count, status) VALUES
(2, 'PATH', 1, '这是一个非常好的前端学习路线！', NULL, 5, 1),
(3, 'PATH', 1, '感谢分享，内容很详细', NULL, 3, 1),
(2, 'UNIT', 1, '视频讲解很清晰', NULL, 2, 1);

-- 7. 收藏数据
INSERT INTO favorites (user_id, target_type, target_id) VALUES
(2, 'PATH', 1),
(2, 'UNIT', 1),
(3, 'PATH', 1),
(3, 'PATH', 3);

-- 8. 标签数据
INSERT INTO tags (name, category, usage_count) VALUES
('HTML', '前端', 10),
('CSS', '前端', 8),
('JavaScript', '前端', 12),
('Java', '后端', 8),
('Spring Boot', '后端', 6),
('Docker', 'DevOps', 4);

-- 9. 路线-标签关联数据
INSERT INTO path_tags (path_id, tag_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 4),
(2, 5),
(3, 6);