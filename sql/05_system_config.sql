-- =====================================================
-- COCO-CLAW 系统配置表
-- 用于存储前端可配置的运营数据
-- =====================================================

USE coco_claw;

-- =====================================================
-- 1. 系统配置表 (Key-Value 格式)
-- =====================================================
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值(JSON格式)',
    `config_type` VARCHAR(50) DEFAULT 'string' COMMENT '配置类型: string/json/number/boolean',
    `group_key` VARCHAR(50) DEFAULT 'default' COMMENT '配置分组',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '配置描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`),
    KEY `idx_group_key` (`group_key`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =====================================================
-- 2. 首页Banner表
-- =====================================================
DROP TABLE IF EXISTS `home_banner`;
CREATE TABLE `home_banner` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'BannerID',
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
    `link_url` VARCHAR(500) DEFAULT NULL COMMENT '跳转链接',
    `link_type` VARCHAR(20) DEFAULT 'none' COMMENT '链接类型: none/url/skill/category',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页Banner表';

-- =====================================================
-- 3. 首页功能入口表
-- =====================================================
DROP TABLE IF EXISTS `home_feature`;
CREATE TABLE `home_feature` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '功能ID',
    `name` VARCHAR(50) NOT NULL COMMENT '名称',
    `icon` VARCHAR(100) NOT NULL COMMENT '图标(Emoji或URL)',
    `link_url` VARCHAR(500) DEFAULT NULL COMMENT '跳转链接',
    `link_type` VARCHAR(20) DEFAULT 'none' COMMENT '链接类型: none/url/page',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页功能入口表';

-- =====================================================
-- 4. 首页精选推荐表
-- =====================================================
DROP TABLE IF EXISTS `home_recommend`;
CREATE TABLE `home_recommend` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '推荐ID',
    `title` VARCHAR(100) NOT NULL COMMENT '推荐标题',
    `item_type` VARCHAR(50) NOT NULL COMMENT '推荐类型: skill/package',
    `item_id` BIGINT NOT NULL COMMENT '关联ID(skill_id或package_id)',
    `subtitle` VARCHAR(200) DEFAULT NULL COMMENT '副标题',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标(可选)',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_item_type` (`item_type`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页精选推荐表';

-- =====================================================
-- 5. FAQ表
-- =====================================================
DROP TABLE IF EXISTS `faq`;
CREATE TABLE `faq` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'FAQ ID',
    `question` VARCHAR(255) NOT NULL COMMENT '问题',
    `answer` TEXT NOT NULL COMMENT '答案',
    `category` VARCHAR(50) DEFAULT 'general' COMMENT '分类',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FAQ表';

-- =====================================================
-- 6. 系统参数表 (用于前端环境配置)
-- =====================================================
DROP TABLE IF EXISTS `system_params`;
CREATE TABLE `system_params` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '参数ID',
    `param_key` VARCHAR(100) NOT NULL COMMENT '参数键',
    `param_value` VARCHAR(500) NOT NULL COMMENT '参数值',
    `param_type` VARCHAR(20) DEFAULT 'string' COMMENT '参数类型',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '参数描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_param_key` (`param_key`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统参数表';

-- =====================================================
-- 初始化默认数据
-- =====================================================

-- 功能入口
INSERT INTO `home_feature` (`name`, `icon`, `link_url`, `link_type`, `sort_order`) VALUES
('充值中心', '💎', '/pages/recharge', 'page', 1),
('套餐购买', '📦', '/pages/packages', 'page', 2),
('使用教程', '📱', '/pages/tutorial', 'page', 3),
('在线客服', '💁', '/pages/support', 'page', 4);

-- FAQ
INSERT INTO `faq` (`question`, `answer`, `category`, `sort_order`) VALUES
('Token 是什么？如何计费？', 'Token是大语言模型处理文本的基本单位。一般1000个Token约等于750个英文单词或500个中文字符。我们按照实际消耗的Token数量进行计费，用多少付多少。', 'billing', 1),
('如何获取 API Key？', '登录后进入"我的Tokens"页面，点击"获取Token"即可。购买套餐后会获得专属的API Key，支持OpenAI、Claude等主流接口格式。', 'api', 2),
('支持哪些支付方式？', '我们支持支付宝、微信支付两种主流支付方式。支付完成后自动到账，无需人工审核。', 'payment', 3),
('如何申请退款？', '如果对服务不满意，可在购买后7天内申请退款。登录后进入"我的订单"页面，点击对应订单申请退款即可。', 'refund', 4),
('有免费试用吗？', '新用户首充可获得额外10%赠送。同时我们提供最低9.9元的体验套餐，适合首次尝试。', 'general', 5);

-- 系统参数
INSERT INTO `system_params` (`param_key`, `param_value`, `param_type`, `description`) VALUES
('APP_NAME', 'COCO CLAW', 'string', '应用名称'),
('APP_SLOGAN', '真正便宜的 TOKEN', 'string', '应用标语'),
('MOCK_MODE', 'false', 'boolean', '是否启用Mock模式'),
('CUSTOMER_SERVICE_URL', '', 'string', '客服链接'),
('DOCUMENT_URL', 'https://www.feishu.cn', 'string', '文档链接');

-- Banner占位 (可后续添加)
-- INSERT INTO `home_banner` ...
