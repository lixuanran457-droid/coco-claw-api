package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_balance")
public class UserBalance {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 当前余额（美元）
     */
    private BigDecimal balance;

    /**
     * 累计充值
     */
    private BigDecimal totalRecharged;

    /**
     * 累计消费
     */
    private BigDecimal totalConsumed;

    /**
     * 余额计费规则
     */
    private String ruleId;

    /**
     * 技术团队租户ID
     */
    private String tenantId;

    /**
     * 1-正常 0-禁用
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
