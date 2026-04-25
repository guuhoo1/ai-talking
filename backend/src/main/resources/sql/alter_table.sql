-- 修改ai_knowledge表，添加principle和notes字段
ALTER TABLE `ai_knowledge` 
ADD COLUMN `principle` TEXT,
ADD COLUMN `notes` TEXT;