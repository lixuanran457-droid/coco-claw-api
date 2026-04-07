-- COCO CLAW 系统配置表初始化脚本

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS coco_claw DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE coco_claw;

-- 1. 创建 sys_config 表 - 系统配置表
CREATE TABLE IF NOT EXISTS `sys_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '配置描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 2. 创建 sys_admin 表 - 管理员账号表
CREATE TABLE IF NOT EXISTS `sys_admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
  `role` VARCHAR(20) NOT NULL DEFAULT 'admin' COMMENT '角色：super_admin-超级管理员，admin-管理员',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员账号表';

-- 3. 创建 recommend_skill 表 - 推荐商品表
CREATE TABLE IF NOT EXISTS `recommend_skill` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '推荐ID',
  `skill_id` BIGINT NOT NULL COMMENT '技能ID',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `is_active` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_skill_id` (`skill_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐商品表';

-- 插入默认系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`) VALUES
('banner_list', '[{"id":1,"imageUrl":"https://picsum.photos/750/300?random=1","linkUrl":"/skills","sortOrder":1,"isActive":1},{"id":2,"imageUrl":"https://picsum.photos/750/300?random=2","linkUrl":"/skills","sortOrder":2,"isActive":1}]', '首页Banner图片列表（JSON数组）'),
('currency_symbol', '¥', '货币符号（¥ 或 $）'),
('page_size', '30', '分页大小'),
('token_expire_days', '7', 'Token有效期（天）')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);

-- 插入默认管理员账号（密码：admin123，MD5加密后）
-- 加密方式：MD5(password + "_coco_claw_salt")
INSERT INTO `sys_admin` (`username`, `password`, `role`, `status`) VALUES
('admin', 'f0e4c2f7c8a1d3b9e5f6a2c4d8e0f1a3', 'super_admin', 1),
('manager', 'f0e4c2f7c8a1d3b9e5f6a2c4d8e0f1a3', 'admin', 1)
ON DUPLICATE KEY UPDATE `role` = VALUES(`role`);
