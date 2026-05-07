-- =====================================================
-- COCO-CLAW 数据库补充脚本 - API密钥和使用记录
-- =====================================================

USE coco_claw;

-- =====================================================
-- 6. API密钥表
-- =====================================================
DROP TABLE IF EXISTS `api_key`;
CREATE TABLE `api_key` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '密钥ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(100) NOT NULL COMMENT '密钥名称',
    `api_key` VARCHAR(64) NOT NULL COMMENT 'API密钥',
    `prefix` VARCHAR(20) NOT NULL COMMENT '密钥前缀（用于显示）',
    `group_name` VARCHAR(50) DEFAULT NULL COMMENT '分组名称',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `last_used_at` DATETIME DEFAULT NULL COMMENT '最后使用时间',
    `last_used_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后使用IP',
    `expires_at` DATETIME DEFAULT NULL COMMENT '过期时间',
    `note` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_api_key` (`api_key`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_group_name` (`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API密钥表';

-- =====================================================
-- 7. 使用记录表
-- =====================================================
DROP TABLE IF EXISTS `usage_record`;
CREATE TABLE `usage_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `api_key_id` BIGINT NOT NULL COMMENT 'API密钥ID',
    `model` VARCHAR(50) NOT NULL COMMENT '使用的模型',
    `input_tokens` INT DEFAULT 0 COMMENT '输入Token数',
    `output_tokens` INT DEFAULT 0 COMMENT '输出Token数',
    `total_tokens` INT DEFAULT 0 COMMENT '总Token数',
    `cost` DECIMAL(10,4) DEFAULT 0.0000 COMMENT '消费金额',
    `latency_ms` INT DEFAULT 0 COMMENT '响应延迟(毫秒)',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT '请求IP',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT 'User-Agent',
    `request_id` VARCHAR(64) DEFAULT NULL COMMENT '请求ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-失败, 1-成功',
    `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_api_key_id` (`api_key_id`),
    KEY `idx_model` (`model`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='使用记录表';

-- =====================================================
-- 8. 充值记录表
-- =====================================================
DROP TABLE IF EXISTS `recharge_record`;
CREATE TABLE `recharge_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '充值订单号',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '充值金额',
    `token_amount` INT DEFAULT 0 COMMENT '充值Token数量',
    `payment_method` VARCHAR(20) DEFAULT NULL COMMENT '支付方式: alipay, wechat, card',
    `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态: 0-待支付, 1-已支付, 2-已退款',
    `transaction_id` VARCHAR(100) DEFAULT NULL COMMENT '第三方交易流水号',
    `paid_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_payment_status` (`payment_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值记录表';

-- =====================================================
-- 9. 兑换码表
-- =====================================================
DROP TABLE IF EXISTS `redeem_code`;
CREATE TABLE `redeem_code` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `code` VARCHAR(50) NOT NULL COMMENT '兑换码',
    `type` TINYINT DEFAULT 1 COMMENT '类型: 1-Token, 2-套餐',
    `token_amount` INT DEFAULT 0 COMMENT 'Token数量',
    `package_id` BIGINT DEFAULT NULL COMMENT '套餐ID',
    `package_name` VARCHAR(100) DEFAULT NULL COMMENT '套餐名称',
    `package_days` INT DEFAULT NULL COMMENT '套餐天数',
    `max_use_count` INT DEFAULT 1 COMMENT '最大使用次数',
    `used_count` INT DEFAULT 0 COMMENT '已使用次数',
    `user_id` BIGINT DEFAULT NULL COMMENT '绑定的用户ID',
    `bind_time` DATETIME DEFAULT NULL COMMENT '绑定时间',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-可用, 2-已用完, 3-已过期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='兑换码表';

-- =====================================================
-- 10. 用户Token余额表
-- =====================================================
DROP TABLE IF EXISTS `user_token`;
CREATE TABLE `user_token` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `balance` INT DEFAULT 0 COMMENT 'Token余额',
    `total_consumed` INT DEFAULT 0 COMMENT '累计消耗',
    `total_recharged` INT DEFAULT 0 COMMENT '累计充值',
    `freeze_amount` INT DEFAULT 0 COMMENT '冻结数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_balance` (`balance`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户Token余额表';

-- =====================================================
-- 11. 套餐表
-- =====================================================
DROP TABLE IF EXISTS `package`;
CREATE TABLE `package` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
    `name` VARCHAR(100) NOT NULL COMMENT '套餐名称',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标',
    `description` TEXT COMMENT '描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `token_amount` INT DEFAULT 0 COMMENT '包含Token数量',
    `duration_days` INT DEFAULT 30 COMMENT '有效期(天)',
    `daily_limit` INT DEFAULT 0 COMMENT '每日限制(0=不限)',
    `models` VARCHAR(500) DEFAULT NULL COMMENT '可用模型(JSON数组)',
    `features` TEXT COMMENT '功能特点(JSON数组)',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='套餐表';

-- =====================================================
-- 12. 用户订阅表
-- =====================================================
DROP TABLE IF EXISTS `user_subscription`;
CREATE TABLE `user_subscription` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订阅ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `package_id` BIGINT NOT NULL COMMENT '套餐ID',
    `package_name` VARCHAR(100) DEFAULT NULL COMMENT '套餐名称(冗余)',
    `token_quota` INT DEFAULT 0 COMMENT 'Token配额',
    `token_used` INT DEFAULT 0 COMMENT '已使用',
    `token_remaining` INT DEFAULT 0 COMMENT '剩余',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已过期, 1-生效中',
    `auto_renew` TINYINT DEFAULT 0 COMMENT '自动续费: 0-否, 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_active` (`user_id`, `status`),
    KEY `idx_package_id` (`package_id`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户订阅表';

-- =====================================================
-- 13. 第三方账号绑定表
-- =====================================================
DROP TABLE IF EXISTS `user_binding`;
CREATE TABLE `user_binding` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `provider` VARCHAR(50) NOT NULL COMMENT '第三方平台: github, dingtalk, asktoken',
    `provider_user_id` VARCHAR(100) NOT NULL COMMENT '第三方用户ID',
    `access_token` VARCHAR(500) DEFAULT NULL COMMENT '访问令牌',
    `refresh_token` VARCHAR(500) DEFAULT NULL COMMENT '刷新令牌',
    `token_expires_at` DATETIME DEFAULT NULL COMMENT '令牌过期时间',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '第三方昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '第三方头像',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-失效, 1-有效',
    `bind_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_provider` (`user_id`, `provider`),
    UNIQUE KEY `uk_provider_user` (`provider`, `provider_user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方账号绑定表';
