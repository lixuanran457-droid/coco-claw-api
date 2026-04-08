package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_coupon")
public class UserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 领取时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime receiveTime;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 状态: 0-未使用, 1-已使用, 2-已过期
     */
    private Integer status;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
