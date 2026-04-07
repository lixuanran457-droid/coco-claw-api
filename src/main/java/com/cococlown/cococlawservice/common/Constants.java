package com.cococlown.cococlawservice.common;

/**
 * 通用常量
 */
public class Constants {

    /**
     * SKILL状态
     */
    public static class SkillStatus {
        /**
         * 下架
         */
        public static final Integer OFFLINE = 0;
        /**
         * 上架
         */
        public static final Integer ONLINE = 1;
        /**
         * 精选
         */
        public static final Integer FEATURED = 2;
    }

    /**
     * 订单状态
     */
    public static class OrderStatus {
        /**
         * 待支付
         */
        public static final Integer PENDING = 0;
        /**
         * 已支付
         */
        public static final Integer PAID = 1;
        /**
         * 已完成
         */
        public static final Integer COMPLETED = 2;
        /**
         * 已取消
         */
        public static final Integer CANCELLED = 3;
    }

    /**
     * 用户状态
     */
    public static class UserStatus {
        /**
         * 禁用
         */
        public static final Integer DISABLED = 0;
        /**
         * 启用
         */
        public static final Integer ENABLED = 1;
    }

    /**
     * 分类状态
     */
    public static class CategoryStatus {
        /**
         * 禁用
         */
        public static final Integer DISABLED = 0;
        /**
         * 启用
         */
        public static final Integer ENABLED = 1;
    }
}
