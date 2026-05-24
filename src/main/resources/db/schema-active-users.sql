-- 活跃用户统计表（月度）
-- 记录每月用户发布帖子数量，用于排行榜展示
CREATE TABLE IF NOT EXISTS monthly_active_users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    avatar VARCHAR(255) COMMENT '头像URL',
    year_month VARCHAR(7) NOT NULL COMMENT '年月，格式：YYYY-MM',
    post_count INT DEFAULT 0 COMMENT '本月发帖数量',
    comment_count INT DEFAULT 0 COMMENT '本月评论数量',
    total_score INT DEFAULT 0 COMMENT '综合得分（帖子数*2 + 评论数）',
    rank_position INT COMMENT '排名位置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_month (user_id, year_month),
    INDEX idx_year_month (year_month),
    INDEX idx_total_score (total_score),
    INDEX idx_rank_position (rank_position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='月度活跃用户统计表';

-- 查询示例：获取当前月份的活跃用户排行榜（前10名）
-- SELECT 
--     mau.*,
--     u.avatar,
--     (mau.post_count * 2 + mau.comment_count) as activity_score
-- FROM monthly_active_users mau
-- LEFT JOIN users u ON mau.user_id = u.id
-- WHERE mau.year_month = DATE_FORMAT(NOW(), '%Y-%m')
-- ORDER BY activity_score DESC, mau.post_count DESC
-- LIMIT 10;