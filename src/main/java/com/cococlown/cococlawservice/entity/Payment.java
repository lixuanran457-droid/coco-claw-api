package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("payment")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付方式: alipay, wechat
     */
    private String paymentMethod;

    /**
     * 第三方交易号
     */
    private String tradeNo;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付状态: 0-待支付, 1-支付中, 2-已支付, 3-支付失败, 4-退款中, 5-已退款
     */
    private Integer status;

    /**
     * 支付链接/二维码
     */
    private String payUrl;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
