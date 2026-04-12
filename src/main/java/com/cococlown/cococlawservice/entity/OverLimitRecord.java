package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("over_limit_record")
public class OverLimitRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long subscriptionId;

    private String apiKey;

    /**
     * DAILY-日限 MONTHLY-月限
     */
    private String overType;

    /**
     * 限制额度
     */
    private BigDecimal limitAmount;

    /**
     * 已用额度
     */
    private BigDecimal usedAmount;

    /**
     * NOTIFY-通知 DISABLE-禁用
     */
    private String action;

    /**
     * 1-已处理 0-未处理
     */
    private Integer handled;

    /**
     * 处理时间
     */
    private LocalDateTime handledAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
