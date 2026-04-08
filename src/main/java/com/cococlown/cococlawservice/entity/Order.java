package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（会员下单时必填，游客下单时为NULL）
     */
    private Long userId;

    /**
     * 游客邮箱（游客下单时必填，用于查询订单）
     */
    private String email;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 技能ID
     */
    private Long skillId;

    /**
     * 技能名称
     */
    private String skillName;

    /**
     * 技能图标
     */
    private String skillIcon;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实际支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付方式: alipay, wechat
     */
    private String paymentMethod;

    /**
     * 支付流水号
     */
    private String tradeNo;

    /**
     * 订单状态: 0-待支付, 1-支付中, 2-已支付, 3-已完成, 4-已取消, 5-退款中, 6-已退款
     */
    private Integer status;

    /**
     * 收货地址ID
     */
    private Long addressId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货地址
     */
    private String deliveryAddress;

    /**
     * 用户备注
     */
    private String remark;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 退款申请时间
     */
    private LocalDateTime refundApplyTime;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
