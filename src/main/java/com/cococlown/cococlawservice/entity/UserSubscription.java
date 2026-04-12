package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_subscription")
public class UserSubscription {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long packageId;

    private String orderNo;

    /**
     * 订阅名称（冗余存储）
     */
    private String name;

    /**
     * 规则ID（冗余存储）
     */
    private String ruleId;

    /**
     * 每日额度
     */
    private BigDecimal dailyQuota;

    /**
     * 今日已用
     */
    private BigDecimal dailyUsed;

    /**
     * 今日剩余
     */
    private BigDecimal dailyRemain;

    /**
     * 每日额度重置时间
     */
    private LocalDateTime dailyResetAt;

    /**
     * 总额度
     */
    private BigDecimal totalQuota;

    /**
     * 已用总额度
     */
    private BigDecimal totalUsed;

    /**
     * 剩余总额度
     */
    private BigDecimal totalRemain;

    /**
     * PENDING-待激活 ACTIVE-激活 EXPIRED-过期 DEPLETED-耗尽 DISABLED-禁用
     */
    private String status;

    /**
     * 1-当前使用中 0-未使用
     */
    private Integer isCurrent;

    /**
     * 技术团队租户ID
     */
    private String tenantId;

    /**
     * API Key（技术团队返回）
     */
    private String apiKey;

    /**
     * API 地址
     */
    private String baseUrl;

    /**
     * 开始时间
     */
    private LocalDateTime startedAt;

    /**
     * 过期时间
     */
    private LocalDateTime expireAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String packageName;

    @TableField(exist = false)
    private String unit;
}
