-- 用户表 (users)
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    avatar VARCHAR(255) COMMENT '头像URL',
    role ENUM('USER', 'VIP', 'ADMIN') DEFAULT 'USER' COMMENT '角色',
    vip_expire_date DATE COMMENT 'VIP到期日',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 学习路线表 (learning_paths)
CREATE TABLE learning_paths (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL COMMENT '路线标题',
    category_id BIGINT COMMENT '分类ID',
    description TEXT COMMENT '路线描述',
    cover_image VARCHAR(255) COMMENT '封面图',
    difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') COMMENT '难度',
    duration_weeks INT COMMENT '预计周数',
    is_vip_only TINYINT DEFAULT 0 COMMENT '是否VIP专属',
    status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') DEFAULT 'DRAFT',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_by BIGINT COMMENT '创建者ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_category_id (category_id),
    INDEX idx_created_by (created_by)
);

-- 学习阶段表 (path_stages)
CREATE TABLE path_stages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    path_id BIGINT NOT NULL COMMENT '所属路线ID',
    title VARCHAR(100) NOT NULL COMMENT '阶段标题',
    description TEXT COMMENT '阶段描述',
    duration_days INT COMMENT '预计天数',
    sort_order INT DEFAULT 0,
    is_locked TINYINT DEFAULT 0 COMMENT '是否锁定',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_path_id (path_id)
);

-- 学习单元/资源表 (learning_units)
CREATE TABLE learning_units (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stage_id BIGINT NOT NULL COMMENT '所属阶段ID',
    title VARCHAR(100) NOT NULL COMMENT '单元标题',
    type ENUM('VIDEO', 'ARTICLE', 'DOC', 'PROJECT') COMMENT '资源类型',
    content_type ENUM('EXTERNAL', 'INTERNAL') DEFAULT 'EXTERNAL' COMMENT '内容类型',
    external_url VARCHAR(500) COMMENT '外部链接',
    platform VARCHAR(50) COMMENT '平台名称(B站/慕课网等)',
    author VARCHAR(100) COMMENT '资源作者',
    duration_minutes INT COMMENT '时长(分钟)',
    description TEXT COMMENT '描述',
    sort_order INT DEFAULT 0,
    is_vip_only TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_stage_id (stage_id)
);

-- 用户学习进度表 (user_progress)
CREATE TABLE user_progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    unit_id BIGINT NOT NULL,
    path_id BIGINT NOT NULL,
    status ENUM('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'NOT_STARTED',
    start_time TIMESTAMP COMMENT '开始时间',
    complete_time TIMESTAMP COMMENT '完成时间',
    last_study_time TIMESTAMP NULL COMMENT '最近学习时间',
    study_duration INT DEFAULT 0 COMMENT '学习时长(秒)',
    UNIQUE KEY uk_user_unit (user_id, unit_id),
    INDEX idx_user_path (user_id, path_id),
    INDEX idx_path (path_id)
);

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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 收藏表 (favorites)
CREATE TABLE favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_type ENUM('PATH', 'UNIT', 'NOTE') COMMENT '收藏类型',
    target_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_target (user_id, target_type, target_id)
);

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
    resolved_at TIMESTAMP
);

-- 用户通知表 (user_notifications)
CREATE TABLE user_notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type VARCHAR(50) NOT NULL COMMENT '通知类型：PROGRESS_MILESTONE等',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    related_type VARCHAR(50) COMMENT '关联类型：PATH, UNIT等',
    related_id BIGINT COMMENT '关联ID',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0未读 1已读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    read_at TIMESTAMP COMMENT '阅读时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);

-- 初始化测试数据
-- 1. 用户数据
INSERT INTO users (username, email, password, role, status) VALUES
('admin', 'admin@example.com', '$2a$10$eWbJ3V5rV8U4q9G7Y7Z6aOe7f8g9h0i1j2k3l4m5n6o7p8q9r0s1t2u3v4w5x6y7z8', 'ADMIN', 1),
('user1', 'user1@example.com', '$2a$10$eWbJ3V5rV8U4q9G7Y7Z6aOe7f8g9h0i1j2k3l4m5n6o7p8q9r0s1t2u3v4w5x6y7z8', 'USER', 1),
('vip1', 'vip1@example.com', '$2a$10$eWbJ3V5rV8U4q9G7Y7Z6aOe7f8g9h0i1j2k3l4m5n6o7p8q9r0s1t2u3v4w5x6y7z8', 'VIP', 1);

-- 2. 学习路线数据
INSERT INTO learning_paths (title, category_id, description, cover_image, difficulty, duration_weeks, is_vip_only, status, sort_order, created_by) VALUES
('前端开发学习路线', 1, '从HTML/CSS基础到React/Vue框架的完整前端开发学习路径', 'https://example.com/frontend.jpg', 'BEGINNER', 12, 0, 'PUBLISHED', 1, 1),
('后端开发学习路线', 2, '从Java基础到Spring Boot框架的完整后端开发学习路径', 'https://example.com/backend.jpg', 'INTERMEDIATE', 16, 0, 'PUBLISHED', 2, 1),
('DevOps学习路线', 3, '从Linux基础到CI/CD的完整DevOps学习路径', 'https://example.com/devops.jpg', 'ADVANCED', 8, 1, 'PUBLISHED', 3, 1);

-- 3. 学习阶段数据
INSERT INTO path_stages (path_id, title, description, duration_days, sort_order, is_locked) VALUES
(1, 'HTML/CSS基础', '学习HTML标签和CSS样式基础', 14, 1, 0),
(1, 'JavaScript基础', '学习JavaScript语法和DOM操作', 21, 2, 0),
(1, '前端框架', '学习React和Vue框架', 28, 3, 0),
(2, 'Java基础', '学习Java语法和面向对象编程', 21, 1, 0),
(2, 'Spring Boot', '学习Spring Boot框架和RESTful API', 28, 2, 0),
(2, '数据库', '学习MySQL和Redis', 14, 3, 0),
(3, 'Linux基础', '学习Linux命令和系统管理', 14, 1, 0),
(3, '容器技术', '学习Docker和Kubernetes', 21, 2, 0),
(3, 'CI/CD', '学习Jenkins和GitLab CI', 14, 3, 0);

-- 4. 学习单元数据
INSERT INTO learning_units (stage_id, title, type, content_type, external_url, platform, author, duration_minutes, description, sort_order, is_vip_only, status) VALUES
(1, 'HTML基础', 'VIDEO', 'EXTERNAL', 'https://example.com/html-basics', 'B站', '张三', 60, 'HTML标签和结构基础', 1, 0, 1),
(1, 'CSS基础', 'VIDEO', 'EXTERNAL', 'https://example.com/css-basics', 'B站', '李四', 90, 'CSS选择器和样式基础', 2, 0, 1),
(2, 'JavaScript语法', 'ARTICLE', 'INTERNAL', NULL, NULL, '王五', 45, 'JavaScript变量、函数和对象', 1, 0, 1),
(2, 'DOM操作', 'VIDEO', 'EXTERNAL', 'https://example.com/dom-manipulation', '慕课网', '赵六', 75, 'JavaScript DOM操作基础', 2, 0, 1),
(3, 'React基础', 'VIDEO', 'EXTERNAL', 'https://example.com/react-basics', 'B站', '张三', 120, 'React组件和生命周期', 1, 0, 1),
(3, 'Vue基础', 'VIDEO', 'EXTERNAL', 'https://example.com/vue-basics', 'B站', '李四', 90, 'Vue指令和组件', 2, 1, 1);

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
