-- 成就表 (achievements)
CREATE TABLE IF NOT EXISTS achievements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL COMMENT '成就代码',
    name VARCHAR(100) NOT NULL COMMENT '成就名称',
    description VARCHAR(200) COMMENT '成就描述',
    icon VARCHAR(50) COMMENT '成就图标emoji',
    points INT DEFAULT 0 COMMENT '成就点数',
    condition_type VARCHAR(50) NOT NULL COMMENT '条件类型：FIRST_LEARNING/CONSECUTIVE_DAYS/COMPLETED_UNITS/NOTE_COUNT/COMPLETED_PATHS/COMMENT_COUNT/VIP_MEMBER/HELP_LEARNERS',
    condition_value INT DEFAULT 0 COMMENT '条件值',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户成就表 (user_achievements)
CREATE TABLE IF NOT EXISTS user_achievements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    achievement_id BIGINT NOT NULL COMMENT '成就ID',
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '解锁时间',
    UNIQUE KEY uk_user_achievement (user_id, achievement_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_id ON user_achievements(user_id);
CREATE INDEX idx_achievement_id ON user_achievements(achievement_id);

-- 初始化成就数据
INSERT INTO achievements (code, name, description, icon, points, condition_type, condition_value) VALUES
('FIRST_LEARNING', '初出茅庐', '完成第一次学习', '🌟', 10, 'FIRST_LEARNING', 1),
('CONSECUTIVE_7_DAYS', '持之以恒', '连续学习7天', '🔥', 50, 'CONSECUTIVE_DAYS', 7),
('COMPLETED_10_UNITS', '学霸之路', '完成10个学习单元', '📚', 100, 'COMPLETED_UNITS', 10),
('NOTE_20', '笔记达人', '撰写20篇笔记', '📝', 80, 'NOTE_COUNT', 20),
('COMPLETED_3_PATHS', '路线大师', '完成3条学习路线', '🗺️', 150, 'COMPLETED_PATHS', 3),
('COMMENT_50', '社区之星', '发表50条评论', '💬', 120, 'COMMENT_COUNT', 50),
('VIP_MEMBER', 'VIP专属', '开通VIP会员', '💎', 200, 'VIP_MEMBER', 1),
('HELP_10_LEARNERS', '知识分享者', '帮助10位学习者', '🎓', 180, 'HELP_LEARNERS', 10);

-- 注意：用户成就数据将通过用户行为动态解锁，不在此预置
-- 成就解锁触发点：
-- 1. 完成学习单元 (LearningProgressServiceImpl.completeUnit)
-- 2. 创建笔记 (NoteServiceImpl.createNote)
-- 3. 发表评论 (CommentServiceImpl.createComment)
-- 4. 开通VIP (PaymentServiceImpl.paymentNotify)