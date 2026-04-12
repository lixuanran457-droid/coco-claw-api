package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("token_package")
public class TokenPackage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal originalPrice;

    /**
     * 规则ID（技术团队实现）
     */
    private String ruleId;

    /**
     * SUBSCRIPTION-订阅 BALANCE-余额
     */
    private String ruleType;

    /**
     * 每日额度
     */
    private BigDecimal dailyQuota;

    /**
     * 总额度
     */
    private BigDecimal totalQuota;

    /**
     * 单位
     */
    private String unit;

    /**
     * 有效期天数
     */
    private Integer durationDays;

    /**
     * DAY-天 MONTH-月 YEAR-年
     */
    private String durationType;

    /**
     * MANUAL-手动 IMPORT-导入
     */
    private String configSource;

    /**
     * 完整配置JSON
     */
    private String configJson;

    /**
     * 1-启用 0-禁用
     */
    private Integer status;

    /**
     * 1-推荐 0-普通
     */
    private Integer isRecommended;

    /**
     * 排序
     */
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
