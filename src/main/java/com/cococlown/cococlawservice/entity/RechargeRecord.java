package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值记录表
 */
@Data
@TableName("recharge_record")
public class RechargeRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 充值订单号 */
    private String orderNo;

    /** 充值金额 */
    private BigDecimal amount;

    /** 充值Token数量 */
    private Integer tokenAmount;

    /** 支付方式: alipay, wechat, card */
    private String paymentMethod;

    /** 支付状态: 0-待支付, 1-已支付, 2-已退款 */
    private Integer paymentStatus;

    /** 第三方交易流水号 */
    private String transactionId;

    /** 支付时间 */
    private LocalDateTime paidTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除: 0-未删除, 1-已删除 */
    @TableLogic
    private Integer deleted;
}
