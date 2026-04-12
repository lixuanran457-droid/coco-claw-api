package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("balance_config")
public class BalanceConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 汇率（1元=?美元）
     */
    private BigDecimal exchangeRate;

    /**
     * 最小充值金额（元）
     */
    private BigDecimal minRecharge;

    /**
     * 最大充值金额（元）
     */
    private BigDecimal maxRecharge;

    /**
     * 1-启用 0-禁用
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
