-- ============================================
-- COCO CLAW 系统数据库升级脚本
-- 更新时间: 2026-04-08
-- 说明: 新增购物车、支付、优惠券、地址等表
-- ============================================

-- 1. 购物车表
CREATE TABLE IF NOT EXISTS `cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `skill_id` BIGINT NOT NULL COMMENT '技能ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '购买时价格',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_skill_id` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 2. 支付记录表
CREATE TABLE IF NOT EXISTS `payment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_id` BIGINT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `payment_method` VARCHAR(20) NOT NULL COMMENT '支付方式: alipay, wechat',
    `trade_no` VARCHAR(64) COMMENT '第三方交易号',
    `amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '支付金额',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待支付, 1-支付中, 2-已支付, 3-支付失败, 4-退款中, 5-已退款',
    `pay_url` VARCHAR(500) COMMENT '支付链接/二维码',
    `pay_time` DATETIME COMMENT '支付时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `expire_time` DATETIME COMMENT '过期时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 3. 优惠券表
CREATE TABLE IF NOT EXISTS `coupon` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
    `name` VARCHAR(50) NOT NULL COMMENT '优惠券名称',
    `type` TINYINT NOT NULL COMMENT '类型: 1-满减券, 2-折扣券, 3-无门槛券',
    `value` DECIMAL(10,2) NOT NULL COMMENT '优惠值',
    `min_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '使用门槛（满X元可用）',
    `total_count` INT NOT NULL DEFAULT 0 COMMENT '发放总量',
    `used_count` INT NOT NULL DEFAULT 0 COMMENT '已使用数量',
    `per_limit` INT NOT NULL DEFAULT 1 COMMENT '每人限领数量',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 4. 用户优惠券表
CREATE TABLE IF NOT EXISTS `user_coupon` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户优惠券ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `coupon_id` BIGINT NOT NULL COMMENT '优惠券ID',
    `receive_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    `use_time` DATETIME COMMENT '使用时间',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-未使用, 1-已使用, 2-已过期',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_coupon_id` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- 5. 收货地址表
CREATE TABLE IF NOT EXISTS `address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `province` VARCHAR(20) NOT NULL COMMENT '省份',
    `city` VARCHAR(20) NOT NULL COMMENT '城市',
    `district` VARCHAR(20) NOT NULL COMMENT '区/县',
    `detail` VARCHAR(200) NOT NULL COMMENT '详细地址',
    `postal_code` VARCHAR(10) COMMENT '邮编',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认: 0-否, 1-是',
    `tag` VARCHAR(20) COMMENT '标签（如：家、公司）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 6. 更新用户表（如果列不存在则添加）
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `avatar` VARCHAR(255) COMMENT '头像' AFTER `email`;
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `nickname` VARCHAR(50) COMMENT '昵称' AFTER `avatar`;
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `gender` TINYINT DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女' AFTER `nickname`;
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `birthday` VARCHAR(20) COMMENT '生日' AFTER `gender`;
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `bio` VARCHAR(200) COMMENT '个人简介' AFTER `birthday`;
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '余额' AFTER `bio`;

-- 7. 更新订单表（如果列不存在则添加）
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `skill_id` BIGINT COMMENT '技能ID' AFTER `user_id`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `skill_name` VARCHAR(100) COMMENT '技能名称' AFTER `skill_id`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `skill_icon` VARCHAR(50) COMMENT '技能图标' AFTER `skill_name`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `quantity` INT DEFAULT 1 COMMENT '数量' AFTER `skill_icon`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `price` DECIMAL(10,2) COMMENT '单价' AFTER `quantity`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `coupon_id` BIGINT COMMENT '优惠券ID' AFTER `total_amount`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额' AFTER `coupon_id`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `pay_amount` DECIMAL(10,2) COMMENT '实际支付金额' AFTER `discount_amount`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `payment_method` VARCHAR(20) COMMENT '支付方式' AFTER `pay_amount`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `trade_no` VARCHAR(64) COMMENT '交易号' AFTER `payment_method`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `address_id` BIGINT COMMENT '收货地址ID' AFTER `status`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `receiver_name` VARCHAR(50) COMMENT '收货人' AFTER `address_id`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `receiver_phone` VARCHAR(20) COMMENT '收货电话' AFTER `receiver_name`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `delivery_address` VARCHAR(255) COMMENT '收货地址' AFTER `receiver_phone`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `remark` VARCHAR(200) COMMENT '备注' AFTER `delivery_address`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `refund_reason` VARCHAR(200) COMMENT '退款原因' AFTER `remark`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `pay_time` DATETIME COMMENT '支付时间' AFTER `refund_reason`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `refund_apply_time` DATETIME COMMENT '退款申请时间' AFTER `pay_time`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `refund_time` DATETIME COMMENT '退款时间' AFTER `refund_apply_time`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `complete_time` DATETIME COMMENT '完成时间' AFTER `refund_time`;
ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `expire_time` DATETIME COMMENT '过期时间' AFTER `complete_time`;

-- 8. 更新技能表（如果列不存在则添加）
ALTER TABLE `skill` ADD COLUMN IF NOT EXISTS `price_type` TINYINT DEFAULT 1 COMMENT '价格类型: 0-免费, 1-付费' AFTER `price`;

-- ============================================
-- 初始化测试数据
-- ============================================

-- 插入测试优惠券
INSERT INTO `coupon` (`name`, `type`, `value`, `min_amount`, `total_count`, `used_count`, `per_limit`, `start_time`, `end_time`, `status`) VALUES
('新人专享券', 1, 10.00, 50.00, 1000, 0, 1, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1),
('满100减20', 1, 20.00, 100.00, 500, 0, 2, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1),
('无门槛5元券', 3, 5.00, 0.00, 200, 0, 1, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 1);

-- 插入测试收货地址
INSERT INTO `address` (`user_id`, `receiver_name`, `phone`, `province`, `city`, `district`, `detail`, `postal_code`, `is_default`) VALUES
(1, '张三', '13800138000', '北京市', '北京市', '朝阳区', 'XX街道XX号', '100000', 1);

-- 插入测试购物车商品
INSERT INTO `cart` (`user_id`, `skill_id`, `quantity`, `price`) VALUES
(1, 1, 1, 99.00),
(1, 3, 1, 79.00);
