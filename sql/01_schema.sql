-- =====================================================
-- COCO-CLAW 数据库建表脚本
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS coco_claw DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE coco_claw;

-- =====================================================
-- 1. 分类表
-- =====================================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- =====================================================
-- 2. SKILL表
-- =====================================================
DROP TABLE IF EXISTS `skill`;
CREATE TABLE `skill` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'SKILL ID',
    `name` VARCHAR(100) NOT NULL COMMENT 'SKILL名称',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标URL',
    `icon_bg` VARCHAR(20) DEFAULT NULL COMMENT '图标背景色',
    `short_desc` VARCHAR(200) DEFAULT NULL COMMENT '简短描述',
    `description` TEXT COMMENT '详细描述',
    `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `rating` DECIMAL(3,2) DEFAULT 5.00 COMMENT '评分',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `likes` INT DEFAULT 0 COMMENT '点赞数',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `security_level` INT DEFAULT 1 COMMENT '安全等级: 1-低, 2-中, 3-高',
    `features` TEXT COMMENT '功能特点(JSON格式)',
    `usage` TEXT COMMENT '使用说明',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-下架, 1-上架, 2-精选',
    `featured` TINYINT DEFAULT 0 COMMENT '是否精选: 0-否, 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_featured` (`featured`),
    KEY `idx_sales` (`sales`),
    KEY `idx_rating` (`rating`),
    FULLTEXT KEY `ft_keyword` (`name`, `short_desc`, `description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='SKILL表';

-- =====================================================
-- 3. 用户表
-- =====================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `uk_phone` (`phone`),
    KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 4. 订单表
-- =====================================================
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
    `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
    `status` TINYINT DEFAULT 0 COMMENT '订单状态: 0-待支付, 1-已支付, 2-已完成, 3-已取消',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- =====================================================
-- 5. 订单项表
-- =====================================================
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `skill_id` BIGINT NOT NULL COMMENT 'SKILL ID',
    `price` DECIMAL(10,2) NOT NULL COMMENT '购买时的价格',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_skill_id` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';
