-- =============================================
-- COCO CLAW 数据库脚本
-- 创建日期: 2026-04-09
-- 描述: 创建user_skill表用于技能交付
-- =============================================

-- 创建用户技能表
CREATE TABLE IF NOT EXISTS `user_skill` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID（注册用户）',
    `email` VARCHAR(255) DEFAULT NULL COMMENT '邮箱（游客）',
    `order_id` BIGINT DEFAULT NULL COMMENT '订单ID',
    `skill_id` BIGINT NOT NULL COMMENT '技能ID',
    `skill_name` VARCHAR(255) NOT NULL COMMENT '技能名称',
    `skill_api_key` VARCHAR(512) DEFAULT NULL COMMENT '技能API Key',
    `usage_count` INT DEFAULT 0 COMMENT '已使用次数',
    `max_usage_count` INT DEFAULT 0 COMMENT '最大使用次数（0表示无限制）',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_email` (`email`),
    KEY `idx_skill_id` (`skill_id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户技能表';

-- =============================================
-- 修改skill表添加API相关字段（如果不存在）
-- =============================================

ALTER TABLE `skill` 
ADD COLUMN IF NOT EXISTS `api_key` VARCHAR(512) DEFAULT NULL COMMENT 'API Key' AFTER `description`,
ADD COLUMN IF NOT EXISTS `api_endpoint` VARCHAR(512) DEFAULT NULL COMMENT 'API端点' AFTER `api_key`,
ADD COLUMN IF NOT EXISTS `documentation_url` VARCHAR(512) DEFAULT NULL COMMENT 'API文档URL' AFTER `api_endpoint`,
ADD COLUMN IF NOT EXISTS `max_usage_count` INT DEFAULT 0 COMMENT '最大使用次数（0表示无限制）' AFTER `documentation_url`;

-- =============================================
-- 修改order表添加退款相关字段（如果不存在）
-- =============================================

ALTER TABLE `order` 
ADD COLUMN IF NOT EXISTS `refund_reason` VARCHAR(512) DEFAULT NULL COMMENT '退款原因' AFTER `trade_no`,
ADD COLUMN IF NOT EXISTS `refund_apply_time` DATETIME DEFAULT NULL COMMENT '退款申请时间' AFTER `refund_reason`,
ADD COLUMN IF NOT EXISTS `refund_time` DATETIME DEFAULT NULL COMMENT '退款完成时间' AFTER `refund_apply_time`;
