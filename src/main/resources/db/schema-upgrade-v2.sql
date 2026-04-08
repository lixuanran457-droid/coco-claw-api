-- ============================================
-- COCO CLAW 数据库升级脚本 V2
-- 更新日期: 2026-04-09
-- 变更说明:
--   1. 用户表：phone改为email作为登录凭证，移除balance余额字段
--   2. 订单表：添加email字段支持游客下单
--   3. 移除余额充值相关功能
-- ============================================

-- 1. 修改user表：将phone改为email，调整字段
-- 注意：如果phone列有数据需要先迁移

-- 先备份phone数据到备注（如果需要）
-- ALTER TABLE user ADD COLUMN phone_backup VARCHAR(20);

-- 修改email字段（如果已存在则调整）
ALTER TABLE user MODIFY COLUMN email VARCHAR(100) COMMENT '邮箱（登录凭证）';

-- 删除phone字段（如果存在）
ALTER TABLE user DROP COLUMN IF EXISTS phone;

-- 删除balance字段（如果存在）
ALTER TABLE user DROP COLUMN IF EXISTS balance;

-- 2. 修改order表：添加email字段支持游客下单
ALTER TABLE order ADD COLUMN IF NOT EXISTS email VARCHAR(100) COMMENT '游客邮箱（游客下单时填写）';

-- 3. 删除payment_type字段中的balance选项（如果有枚举）
-- MySQL不直接支持删除枚举值，需要重建表
-- 建议：直接修改业务代码中的校验逻辑即可

-- 4. 添加索引优化查询
CREATE INDEX IF NOT EXISTS idx_order_email ON `order`(email);
CREATE INDEX IF NOT EXISTS idx_order_user_id ON `order`(user_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON `order`(status);

-- 5. 创建游客查单验证码Redis Key的说明（仅供参考，实际存储在Redis中）
-- Key格式: guest_query:{email}
-- Value: 6位验证码
-- 过期时间: 5分钟

-- 6. 创建密码重置Token Redis Key的说明（仅供参考）
-- Key格式: reset_password:{token}
-- Value: Hash {userId, email}
-- 过期时间: 30分钟

-- 7. 创建验证码Redis Key的说明
-- Key格式: captcha:{email}
-- Value: 6位验证码
-- 过期时间: 5分钟

-- ============================================
-- 验证脚本
-- ============================================

-- 查看user表结构
-- DESC user;

-- 查看order表结构
-- DESC `order`;
