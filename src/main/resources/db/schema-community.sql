-- Add new columns to users table
ALTER TABLE `users` ADD COLUMN `mute_status` INT NOT NULL DEFAULT 0 AFTER `status`;
ALTER TABLE `users` ADD COLUMN `mute_end_date` DATETIME DEFAULT NULL AFTER `mute_status`;

-- Add new columns to announcements table
ALTER TABLE `announcements` ADD COLUMN `likes_count` INT NOT NULL DEFAULT 0 AFTER `priority`;
ALTER TABLE `announcements` ADD COLUMN `is_pinned` INT NOT NULL DEFAULT 0 AFTER `likes_count`;

-- Create posts table
CREATE TABLE IF NOT EXISTS `posts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(200) DEFAULT NULL,
  `content` TEXT NOT NULL,
  `images` VARCHAR(1000) DEFAULT NULL,
  `likes_count` INT NOT NULL DEFAULT 0,
  `comments_count` INT NOT NULL DEFAULT 0,
  `status` INT NOT NULL DEFAULT 1,
  `is_pinned` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user` (`user_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_is_pinned` (`is_pinned`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create post_likes table
CREATE TABLE IF NOT EXISTS `post_likes` (
  `post_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`post_id`, `user_id`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create announcement_likes table
CREATE TABLE IF NOT EXISTS `announcement_likes` (
  `announcement_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`announcement_id`, `user_id`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;