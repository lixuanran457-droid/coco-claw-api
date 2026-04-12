-- ============================================
-- COCO CLAW TOKEN 商城数据库脚本
-- 创建时间: 2026-04-12
-- ============================================

-- 1. TOKEN 套餐表
CREATE TABLE IF NOT EXISTS `token_package` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '套餐名称',
    `description` VARCHAR(500) COMMENT '套餐描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '售价（元）',
    `original_price` DECIMAL(10,2) COMMENT '原价（元）',
    
    -- 规则配置
    `rule_id` VARCHAR(100) NOT NULL COMMENT '规则ID（技术团队实现）',
    `rule_type` VARCHAR(20) DEFAULT 'SUBSCRIPTION' COMMENT 'SUBSCRIPTION-订阅 BALANCE-余额',
    
    -- 额度配置
    `daily_quota` DECIMAL(10,2) COMMENT '每日额度（如：15美元）',
    `total_quota` DECIMAL(10,2) COMMENT '总额度（如：450美元）',
    `unit` VARCHAR(10) DEFAULT 'USD' COMMENT '单位',
    
    -- 有效期
    `duration_days` INT COMMENT '有效期天数',
    `duration_type` VARCHAR(20) DEFAULT 'MONTH' COMMENT 'DAY-天 MONTH-月 YEAR-年',
    
    -- 配置方式
    `config_source` VARCHAR(20) DEFAULT 'MANUAL' COMMENT 'MANUAL-手动 IMPORT-导入',
    `config_json` JSON COMMENT '完整配置JSON',
    
    -- 状态和排序
    `status` TINYINT DEFAULT 1 COMMENT '1-启用 0-禁用',
    `is_recommended` TINYINT DEFAULT 0 COMMENT '1-推荐 0-普通',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='TOKEN套餐表';

-- 2. 用户订阅表
CREATE TABLE IF NOT EXISTS `user_subscription` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `package_id` BIGINT COMMENT '套餐ID',
    `order_no` VARCHAR(64) COMMENT '订单号',
    
    -- 订阅信息
    `name` VARCHAR(100) COMMENT '订阅名称（冗余存储）',
    `rule_id` VARCHAR(100) COMMENT '规则ID（冗余存储）',
    
    -- 额度信息
    `daily_quota` DECIMAL(10,2) COMMENT '每日额度',
    `daily_used` DECIMAL(10,2) DEFAULT 0 COMMENT '今日已用',
    `daily_remain` DECIMAL(10,2) COMMENT '今日剩余',
    `daily_reset_at` DATETIME COMMENT '每日额度重置时间',
    
    `total_quota` DECIMAL(10,2) COMMENT '总额度',
    `total_used` DECIMAL(10,2) DEFAULT 0 COMMENT '已用总额度',
    `total_remain` DECIMAL(10,2) COMMENT '剩余总额度',
    
    -- 状态
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING-待激活 ACTIVE-激活 EXPIRED-过期 DEPLETED-耗尽 DISABLED-禁用',
    `is_current` TINYINT DEFAULT 0 COMMENT '1-当前使用中 0-未使用',
    
    -- 租户信息（技术团队返回）
    `tenant_id` VARCHAR(100) COMMENT '技术团队租户ID',
    `api_key` VARCHAR(255) COMMENT 'API Key（技术团队返回）',
    `base_url` VARCHAR(500) COMMENT 'API 地址',
    
    -- 时间
    `started_at` DATETIME COMMENT '开始时间',
    `expire_at` DATETIME COMMENT '过期时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户订阅表';

-- 3. 用户余额表
CREATE TABLE IF NOT EXISTS `user_balance` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    
    `balance` DECIMAL(10,2) DEFAULT 0 COMMENT '当前余额（美元）',
    `total_recharged` DECIMAL(10,2) DEFAULT 0 COMMENT '累计充值',
    `total_consumed` DECIMAL(10,2) DEFAULT 0 COMMENT '累计消费',
    
    -- 余额配置
    `rule_id` VARCHAR(100) DEFAULT 'balance_pay' COMMENT '余额计费规则',
    
    -- 租户信息
    `tenant_id` VARCHAR(100) COMMENT '技术团队租户ID',
    
    -- 状态
    `status` TINYINT DEFAULT 1 COMMENT '1-正常 0-禁用',
    
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户余额表';

-- 4. 用户 API Key 表
CREATE TABLE IF NOT EXISTS `user_api_key` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    
    -- API 信息（技术团队返回）
    `api_key` VARCHAR(255) COMMENT 'API Key',
    `base_url` VARCHAR(500) COMMENT 'API 地址',
    `tenant_id` VARCHAR(100) COMMENT '技术团队租户ID',
    
    -- 当前使用的资源
    `current_type` VARCHAR(20) DEFAULT 'SUBSCRIPTION' COMMENT 'SUBSCRIPTION-订阅 BALANCE-余额',
    `current_subscription_id` BIGINT COMMENT '当前使用的订阅ID（null表示使用余额）',
    
    -- 状态
    `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE-激活 DISABLED-禁用',
    
    -- 同步信息
    `last_sync_at` DATETIME COMMENT '最后同步时间',
    
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_api_key` (`api_key`),
    UNIQUE INDEX `idx_user_active` (`user_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户API Key表';

-- 5. 余额充值记录表
CREATE TABLE IF NOT EXISTS `balance_recharge` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT COMMENT '用户ID',
    `order_no` VARCHAR(64) UNIQUE COMMENT '订单号',
    
    `amount_cny` DECIMAL(10,2) NOT NULL COMMENT '人民币金额',
    `amount_usd` DECIMAL(10,2) NOT NULL COMMENT '美元金额',
    `exchange_rate` DECIMAL(10,4) DEFAULT 0 COMMENT '汇率',
    
    -- 技术团队返回
    `tenant_id` VARCHAR(100) COMMENT '技术团队租户ID',
    
    -- 状态
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING-待支付 PAID-已支付 COMPLETED-已完成 FAILED-失败',
    
    -- 支付信息
    `pay_type` VARCHAR(20) COMMENT '支付方式 ALIPAY/WECHAT',
    `paid_at` DATETIME COMMENT '支付时间',
    
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='余额充值记录表';

-- 6. 额度同步日志表
CREATE TABLE IF NOT EXISTS `quota_sync_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT COMMENT '用户ID',
    `subscription_id` BIGINT COMMENT '订阅ID',
    `api_key` VARCHAR(255) COMMENT 'API Key',
    
    `sync_type` VARCHAR(20) COMMENT 'PUSH-技术团队推送 PULL-我们拉取',
    
    -- 同步前
    `before_daily_used` DECIMAL(10,2) COMMENT '同步前今日已用',
    `before_total_used` DECIMAL(10,2) COMMENT '同步前已用总额',
    
    -- 同步后
    `after_daily_used` DECIMAL(10,2) COMMENT '同步后今日已用',
    `after_total_used` DECIMAL(10,2) COMMENT '同步后已用总额',
    
    -- 原始数据
    `raw_data` TEXT COMMENT '原始同步数据',
    
    -- 状态
    `status` TINYINT DEFAULT 1 COMMENT '1-成功 0-失败',
    `error_msg` VARCHAR(500) COMMENT '错误信息',
    
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='额度同步日志表';

-- 7. 超限通知记录表
CREATE TABLE IF NOT EXISTS `over_limit_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT COMMENT '用户ID',
    `subscription_id` BIGINT COMMENT '订阅ID',
    `api_key` VARCHAR(255) COMMENT 'API Key',
    
    `over_type` VARCHAR(20) COMMENT 'DAILY-日限 MONTHLY-月限',
    `limit_amount` DECIMAL(10,2) COMMENT '限制额度',
    `used_amount` DECIMAL(10,2) COMMENT '已用额度',
    
    `action` VARCHAR(20) COMMENT 'NOTIFY-通知 DISABLE-禁用',
    `handled` TINYINT DEFAULT 0 COMMENT '1-已处理 0-未处理',
    `handled_at` DATETIME COMMENT '处理时间',
    
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_handled` (`handled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='超限通知记录表';

-- 8. 修改现有订单表，添加订单类型
ALTER TABLE `order` ADD COLUMN `order_type` VARCHAR(20) DEFAULT 'SKILL' COMMENT 'SKILL-技能订单 TOKEN_SUBSCRIPTION-订阅 TOKEN_BALANCE-余额充值';

-- ============================================
-- 初始数据：默认 TOKEN 套餐
-- ============================================
INSERT INTO `token_package` (`name`, `description`, `price`, `original_price`, `rule_id`, `rule_type`, `daily_quota`, `total_quota`, `unit`, `duration_days`, `duration_type`, `sort_order`, `status`, `is_recommended`) VALUES
('入门版', '15美元/天 | 月卡', 200.00, 299.00, 'starter_monthly', 'SUBSCRIPTION', 15.00, 450.00, 'USD', 30, 'MONTH', 1, 1, 1),
('进阶版', '30美元/天 | 月卡', 500.00, 699.00, 'pro_monthly', 'SUBSCRIPTION', 30.00, 900.00, 'USD', 30, 'MONTH', 2, 1, 0),
('专业版', '80美元/天 | 月卡', 1000.00, 1399.00, 'enterprise_monthly', 'SUBSCRIPTION', 80.00, 2400.00, 'USD', 30, 'MONTH', 3, 1, 0),
('年度特惠', '20美元/天 | 年卡', 1800.00, 2499.00, 'starter_yearly', 'SUBSCRIPTION', 20.00, 7300.00, 'USD', 365, 'YEAR', 4, 1, 0);

-- ============================================
-- 初始数据：余额充值汇率配置
-- ============================================
CREATE TABLE IF NOT EXISTS `balance_config` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `exchange_rate` DECIMAL(10,4) DEFAULT 0.0700 COMMENT '汇率（1元=?美元）',
    `min_recharge` DECIMAL(10,2) DEFAULT 10.00 COMMENT '最小充值金额（元）',
    `max_recharge` DECIMAL(10,2) DEFAULT 10000.00 COMMENT '最大充值金额（元）',
    `status` TINYINT DEFAULT 1 COMMENT '1-启用 0-禁用',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='余额配置表';

INSERT INTO `balance_config` (`exchange_rate`, `min_recharge`, `max_recharge`, `status`) VALUES
(0.0700, 10.00, 10000.00, 1);
