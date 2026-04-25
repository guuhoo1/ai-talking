-- 创建ai_knowledge表
CREATE TABLE IF NOT EXISTS `ai_knowledge` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `summary` TEXT NOT NULL,
  `core_code` TEXT,
  `tech_tags` VARCHAR(255),
  `scene` VARCHAR(255),
  `principle` TEXT,
  `notes` TEXT,
  `reusable` BOOLEAN DEFAULT TRUE,
  `source_message_id` BIGINT NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_source_message` (`source_message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;