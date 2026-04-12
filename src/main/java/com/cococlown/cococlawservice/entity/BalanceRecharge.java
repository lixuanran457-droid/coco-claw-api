package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("balance_recharge")
public class BalanceRecharge {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String orderNo;

    /**
     * 人民币金额
     */
    private BigDecimal amountCny;

    /**
     * 美元金额
     */
    private BigDecimal amountUsd;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 技术团队租户ID
     */
    private String tenantId;

    /**
     * PENDING-待支付 PAID-已支付 COMPLETED-已完成 FAILED-失败
     */
    private String status;

    /**
     * 支付方式 ALIPAY/WECHAT
     */
    private String payType;

    /**
     * 支付时间
     */
    private LocalDateTime paidAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
