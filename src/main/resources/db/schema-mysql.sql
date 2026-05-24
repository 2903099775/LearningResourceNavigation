-- ================================================================
-- Learning Nav MySQL Schema (MySQL 5.7 compatible)
-- Database: learning_nav
-- ================================================================

DROP TABLE IF EXISTS `wish_likes`;
DROP TABLE IF EXISTS `wishes`;
DROP TABLE IF EXISTS `user_notifications`;
DROP TABLE IF EXISTS `announcements`;
DROP TABLE IF EXISTS `feedbacks`;
DROP TABLE IF EXISTS `comments`;
DROP TABLE IF EXISTS `favorites`;
DROP TABLE IF EXISTS `user_progress`;
DROP TABLE IF EXISTS `user_notes`;
DROP TABLE IF EXISTS `learning_units`;
DROP TABLE IF EXISTS `path_stages`;
DROP TABLE IF EXISTS `path_tags`;
DROP TABLE IF EXISTS `tags`;
DROP TABLE IF EXISTS `payments`;
DROP TABLE IF EXISTS `learning_paths`;
DROP TABLE IF EXISTS `learning_subcategories`;
DROP TABLE IF EXISTS `learning_categories`;
DROP TABLE IF EXISTS `users`;

-- ================================================================
-- 1. users
-- ================================================================
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `avatar` VARCHAR(500) DEFAULT NULL,
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER',
  `vip_expire_date` DATE DEFAULT NULL,
  `status` INT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_username` (`username`),
  INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 2. learning_categories (学习资源类别)
-- ================================================================
CREATE TABLE `learning_categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL UNIQUE,
  `description` TEXT,
  `icon` VARCHAR(500) DEFAULT NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  `status` INT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_name` (`name`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 3. learning_subcategories (学习子域)
-- ================================================================
CREATE TABLE `learning_subcategories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `category_id` BIGINT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `icon` VARCHAR(500) DEFAULT NULL,
  `cover_image` VARCHAR(500) DEFAULT NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  `status` INT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_status` (`status`),
  CONSTRAINT `fk_subcategories_category` FOREIGN KEY (`category_id`) REFERENCES `learning_categories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 4. learning_paths (学习单元/路线)
-- ================================================================
CREATE TABLE `learning_paths` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(200) NOT NULL,
  `category_id` BIGINT DEFAULT NULL,
  `subcategory_id` BIGINT DEFAULT NULL,
  `description` TEXT,
  `cover_image` VARCHAR(500) DEFAULT NULL,
  `difficulty` VARCHAR(20) DEFAULT NULL,
  `duration_weeks` INT DEFAULT NULL,
  `is_vip_only` INT NOT NULL DEFAULT 0,
  `status` VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
  `sort_order` INT NOT NULL DEFAULT 0,
  `created_by` BIGINT DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_category` (`category_id`),
  INDEX `idx_subcategory` (`subcategory_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_created_by` (`created_by`),
  CONSTRAINT `fk_learning_paths_category` FOREIGN KEY (`category_id`) REFERENCES `learning_categories` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_learning_paths_subcategory` FOREIGN KEY (`subcategory_id`) REFERENCES `learning_subcategories` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 4. tags
-- ================================================================
CREATE TABLE `tags` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(50) NOT NULL,
  `category` VARCHAR(50) DEFAULT NULL,
  `usage_count` INT NOT NULL DEFAULT 0,
  INDEX `idx_name` (`name`),
  INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 5. path_tags (关联表)
-- ================================================================
CREATE TABLE `path_tags` (
  `path_id` BIGINT NOT NULL,
  `tag_id` BIGINT NOT NULL,
  PRIMARY KEY (`path_id`, `tag_id`),
  INDEX `idx_tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 6. path_stages
-- ================================================================
CREATE TABLE `path_stages` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `path_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT,
  `duration_days` INT DEFAULT NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  `is_locked` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_path` (`path_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 7. learning_units
-- ================================================================
CREATE TABLE `learning_units` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `stage_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `type` VARCHAR(50) DEFAULT NULL,
  `content_type` VARCHAR(50) DEFAULT NULL,
  `external_url` VARCHAR(500) DEFAULT NULL,
  `platform` VARCHAR(100) DEFAULT NULL,
  `author` VARCHAR(100) DEFAULT NULL,
  `duration_minutes` INT DEFAULT NULL,
  `description` TEXT,
  `sort_order` INT NOT NULL DEFAULT 0,
  `is_vip_only` INT NOT NULL DEFAULT 0,
  `status` INT NOT NULL DEFAULT 1,
  `view_count` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_stage` (`stage_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_platform` (`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 8. user_notes
-- ================================================================
CREATE TABLE `user_notes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `unit_id` BIGINT DEFAULT NULL,
  `path_id` BIGINT DEFAULT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT,
  `is_favorite` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user` (`user_id`),
  INDEX `idx_unit` (`unit_id`),
  INDEX `idx_path` (`path_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 9. user_progress
-- ================================================================
CREATE TABLE `user_progress` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `unit_id` BIGINT NOT NULL,
  `path_id` BIGINT NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
  `start_time` DATETIME DEFAULT NULL,
  `complete_time` DATETIME DEFAULT NULL,
  `last_study_time` DATETIME NULL COMMENT '最近学习时间',
  `study_duration` INT DEFAULT 0 COMMENT '学习时长(分钟)',
  `visit_count` INT DEFAULT 0 COMMENT '访问次数',
  `rating` INT DEFAULT 0 COMMENT '评分',
  `is_favorite` INT DEFAULT 0 COMMENT '是否收藏',
  INDEX `idx_user_unit` (`user_id`, `unit_id`),
  INDEX `idx_user_path` (`user_id`, `path_id`),
  UNIQUE KEY `uk_user_unit` (`user_id`, `unit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 10. favorites
-- ================================================================
CREATE TABLE `favorites` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `target_type` VARCHAR(50) NOT NULL,
  `target_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 11. comments
-- ================================================================
CREATE TABLE `comments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `username` VARCHAR(50) DEFAULT NULL,
  `target_type` VARCHAR(50) NOT NULL,
  `target_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `parent_id` BIGINT DEFAULT NULL,
  `likes_count` INT NOT NULL DEFAULT 0,
  `status` INT NOT NULL DEFAULT 1,
  `is_top` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_target` (`target_type`, `target_id`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 12. payments
-- ================================================================
CREATE TABLE `payments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `order_no` VARCHAR(64) NOT NULL UNIQUE,
  `amount` DECIMAL(10,2) NOT NULL,
  `months` INT DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  `pay_time` DATETIME DEFAULT NULL,
  `expire_date` DATE DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_user` (`user_id`),
  INDEX `idx_order` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 13. feedbacks
-- ================================================================
CREATE TABLE `feedbacks` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT DEFAULT NULL,
  `type` VARCHAR(50) DEFAULT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT NOT NULL,
  `contact` VARCHAR(100) DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'OPEN',
  `reply` TEXT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `resolved_at` DATETIME DEFAULT NULL,
  INDEX `idx_user` (`user_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 14. user_notifications (用户通知表)
-- ================================================================
CREATE TABLE `user_notifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(50) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT,
  `related_type` VARCHAR(50) DEFAULT NULL,
  `related_id` BIGINT DEFAULT NULL,
  `is_read` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `read_at` DATETIME DEFAULT NULL,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_is_read` (`is_read`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 15. announcements (公告表)
-- ================================================================
CREATE TABLE `announcements` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `admin_id` BIGINT DEFAULT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT,
  `type` VARCHAR(50) DEFAULT 'announcement',
  `status` INT NOT NULL DEFAULT 0,
  `priority` INT NOT NULL DEFAULT 1,
  `publish_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 16. wishes (用户期待表)
-- ================================================================
CREATE TABLE `wishes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `username` VARCHAR(50) DEFAULT NULL,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT,
  `category` VARCHAR(50) DEFAULT 'course',
  `like_count` INT NOT NULL DEFAULT 0,
  `status` INT NOT NULL DEFAULT 1,
  `admin_reply` TEXT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 17. wish_likes (用户点赞关联表)
-- ================================================================
CREATE TABLE `wish_likes` (
  `wish_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`wish_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================================
-- 插入初始数据
-- ================================================================

-- 管理员账号 (密码: 123456 明文)
INSERT INTO `users` (`username`, `password`, `email`, `role`, `status`)
VALUES ('admin', '123456', 'admin@example.com', 'ADMIN', 1);

-- 第一层：学习领域
INSERT INTO `learning_categories` (`id`, `name`, `description`, `sort_order`, `status`)
VALUES
(1, '英语', '英语学习、口语、听力、阅读', 1, 1),
(2, '厨艺', '烹饪技巧、食谱分享、美食文化', 2, 1),
(3, '编程', '编程、软件开发、网络技术', 3, 1),
(4, '数学', '数学基础、高等数学、应用数学', 4, 1);

-- 第二层：学习子域
INSERT INTO `learning_subcategories` (`id`, `category_id`, `name`, `description`, `icon`, `sort_order`, `status`)
VALUES
(1, 1, '初中英语', '初中阶段英语学习，包括单词、语法、阅读', '📚', 1, 1),
(2, 1, '高中英语', '高中阶段英语学习，包括高考英语、口语', '📖', 2, 1),
(3, 1, '英语口语', '日常英语口语练习与提升', '🗣️', 3, 1),
(4, 2, '基础烹饪', '基本烹饪方法和技巧入门', '🍳', 1, 1),
(5, 2, '烘焙甜点', '蛋糕、饼干、面包等烘焙制作', '🍰', 2, 1),
(6, 3, 'Python', 'Python 编程语言从入门到精通', '🐍', 1, 1),
(7, 3, 'Java', 'Java 核心技术栈深度学习', '☕', 2, 1),
(8, 3, '前端开发', 'HTML/CSS/JavaScript 前端技术', '🌐', 3, 1),
(9, 4, '初等数学', '初中数学基础知识和解题技巧', '📐', 1, 1),
(10, 4, '高等数学', '高等数学核心概念与应用', '🔢', 2, 1);

-- 第三层：学习单元（原 learning_paths）
INSERT INTO `learning_paths` (`id`, `title`, `category_id`, `subcategory_id`, `description`, `difficulty`, `duration_weeks`, `is_vip_only`, `status`, `sort_order`, `created_by`)
VALUES
(1, '初中英语单词学习', 1, 1, '系统学习初中阶段必备英语单词', 'BEGINNER', 8, 0, 'PUBLISHED', 1, 1),
(2, '初中英语语法精讲', 1, 1, '初中英语语法体系全面讲解', 'BEGINNER', 6, 0, 'PUBLISHED', 2, 1),
(3, '高中英语单词突破', 1, 2, '高考必备3500词系统学习', 'INTERMEDIATE', 12, 0, 'PUBLISHED', 1, 1),
(4, '高中英语阅读理解', 1, 2, '高考英语阅读理解专项训练', 'INTERMEDIATE', 8, 1, 'PUBLISHED', 2, 1),
(5, '日常英语口语练习', 1, 3, '生活中常用英语口语表达', 'BEGINNER', 8, 0, 'PUBLISHED', 1, 1),
(6, '基础烹饪技巧', 2, 4, '学习基本的烹饪方法和技巧', 'BEGINNER', 4, 0, 'PUBLISHED', 1, 1),
(7, '家常菜谱大全', 2, 4, '经典家常菜的做法和学习', 'BEGINNER', 6, 0, 'PUBLISHED', 2, 1),
(8, '蛋糕烘焙入门', 2, 5, '从零开始学做蛋糕', 'BEGINNER', 4, 0, 'PUBLISHED', 1, 1),
(9, 'Python 全栈学习路径', 3, 6, '从零基础到 Python 全栈工程师的完整学习路线', 'BEGINNER', 16, 0, 'PUBLISHED', 1, 1),
(10, 'Java 核心技术', 3, 7, '深入学习 Java 核心技术栈，包括 Spring、Hibernate 等', 'INTERMEDIATE', 12, 0, 'PUBLISHED', 1, 1),
(11, '前端开发进阶', 3, 8, 'Vue + React + TypeScript 前端进阶路径', 'INTERMEDIATE', 8, 1, 'PUBLISHED', 1, 1),
(12, 'AI 与机器学习入门', 3, 6, '人工智能与机器学习从入门到实践', 'ADVANCED', 20, 1, 'PUBLISHED', 2, 1),
(13, '初中数学同步', 4, 9, '初中数学教材同步辅导', 'BEGINNER', 16, 0, 'PUBLISHED', 1, 1),
(14, '高等数学基础', 4, 10, '高等数学的核心概念和应用', 'ADVANCED', 12, 1, 'PUBLISHED', 1, 1);

-- 示例阶段数据（第四层：具体学习路线内的阶段）
INSERT INTO `path_stages` (`id`, `path_id`, `title`, `description`, `duration_days`, `sort_order`, `is_locked`)
VALUES
(1, 9, '第一阶段：Python 基础', 'Python 基础语法入门', 7, 1, 0),
(2, 9, '第二阶段：Python 进阶', '面向对象、异常处理、文件操作', 14, 2, 0),
(3, 9, '第三阶段：Web 开发', 'Flask/Django Web 开发', 21, 3, 0),
(4, 9, '第四阶段：项目实战', '综合项目开发', 28, 4, 0),
(5, 10, '第一阶段：Java 基础', 'Java 基础语法与面向对象', 14, 1, 0),
(6, 10, '第二阶段：Java Web', 'Servlet、JSP、Filter', 21, 2, 0),
(7, 11, '第一阶段：HTML/CSS/JS', '前端三件套基础', 14, 1, 0),
(8, 11, '第二阶段：Vue 3 实战', 'Vue 3 + Composition API', 21, 2, 0),
(9, 12, '第一阶段：数学基础', '线性代数与概率统计', 30, 1, 0),
(10, 12, '第二阶段：机器学习', 'Scikit-Learn 实战', 30, 2, 0),
(11, 1, '第一阶段：基础词汇', '初中核心词汇学习', 14, 1, 0),
(12, 1, '第二阶段：词汇拓展', '进阶词汇与短语', 21, 2, 0),
(13, 6, '第一阶段：刀工与火候', '基础烹饪技法', 7, 1, 0),
(14, 6, '第二阶段：调味技巧', '调味品的认识和使用', 14, 2, 0);

-- 示例学习单元数据
INSERT INTO `learning_units` (`id`, `stage_id`, `title`, `type`, `content_type`, `external_url`, `platform`, `author`, `duration_minutes`, `description`, `sort_order`, `is_vip_only`, `status`)
VALUES
(1, 1, 'Python 环境搭建', 'VIDEO', 'BILIBILI', 'https://www.bilibili.com/video/BV1xx411c7D8', 'Bilibili', '鱼皮', 30, '安装 Python、Pycharm、开发环境配置', 1, 0, 1),
(2, 1, '变量与数据类型', 'VIDEO', 'BILIBILI', 'https://www.bilibili.com/video/BV1xx411c7D9', 'Bilibili', '鱼皮', 45, 'Python 变量、字符串、数字、布尔类型', 2, 0, 1),
(3, 1, '条件判断与循环', 'VIDEO', 'BILIBILI', 'https://www.bilibili.com/video/BV1xx411c7E0', 'Bilibili', '鱼皮', 60, 'if 语句、for 循环、while 循环', 3, 0, 1),
(4, 1, '函数与模块', 'ARTICLE', 'CSDN', 'https://blog.csdn.net/python/article/details/123456', 'CSDN', '程序员鱼皮', 40, '函数定义、参数、返回值、模块导入', 4, 0, 1),
(5, 2, '面向对象编程', 'VIDEO', 'BILIBILI', 'https://www.bilibili.com/video/BV1xx411c7E1', 'Bilibili', '鱼皮', 90, '类、对象、继承、多态', 1, 0, 1),
(6, 2, '文件操作与异常', 'VIDEO', 'BILIBILI', 'https://www.bilibili.com/video/BV1xx411c7E2', 'Bilibili', '鱼皮', 75, '文件读写、try-except 异常处理', 2, 0, 1),
(7, 2, 'Python 标准库', 'ARTICLE', '官方文档', 'https://docs.python.org/3/library/', 'Official', 'Python官方', 60, 'os、sys、json、datetime 等常用模块', 3, 0, 1),
(8, 5, 'Java 环境配置', 'VIDEO', 'BILIBILI', 'https://www.bilibili.com/video/BV1xx411c7F0', 'Bilibili', '老马', 25, 'JDK 安装、IDEA 配置、第一个程序', 1, 0, 1),
(9, 5, 'Java 面向对象', 'VIDEO', 'BILIBILI', 'https://www.bilibili.com/video/BV1xx411c7F1', 'Bilibili', '老马', 120, '类、对象、封装、继承、多态', 2, 0, 1),
(10, 5, '集合框架', 'VIDEO', 'BILIBILI', 'https://www.bilibili.com/video/BV1xx411c7F2', 'Bilibili', '老马', 90, 'List、Set、Map 集合详解', 3, 0, 1),
(11, 11, '初一核心词汇', 'VIDEO', 'BILIBILI', 'https://www.bilibili.com/video/example1', 'Bilibili', '英语老师', 45, '初一上册必备英语词汇', 1, 0, 1),
(12, 11, '初二核心词汇', 'VIDEO', 'BILIBILI', 'https://www.bilibili.com/video/example2', 'Bilibili', '英语老师', 50, '初二上册必备英语词汇', 2, 0, 1),
(13, 13, '切菜基础技法', 'VIDEO', 'BILIBILI', 'https://www.bilibili.com/video/cook1', 'Bilibili', '大厨', 30, '切丝、切片、切丁等基础刀工', 1, 0, 1),
(14, 13, '火候控制技巧', 'VIDEO', 'BILIBILI', 'https://www.bilibili.com/video/cook2', 'Bilibili', '大厨', 25, '不同菜品的火候掌握', 2, 0, 1);
