package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型: 1-满减券, 2-折扣券, 3-无门槛券
     */
    private Integer type;

    /**
     * 优惠值（满减金额或折扣率）
     */
    private BigDecimal value;

    /**
     * 使用门槛（满X元可用）
     */
    private BigDecimal minAmount;

    /**
     * 发放总量
     */
    private Integer totalCount;

    /**
     * 已使用数量
     */
    private Integer usedCount;

    /**
     * 每人限领数量
     */
    private Integer perLimit;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
