package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 支付创建DTO
 */
@Data
public class PaymentCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付方式: alipay, wechat
     */
    private String paymentMethod;

    /**
     * 技能ID（直接购买单个商品）
     */
    private Long skillId;

    /**
     * 游客邮箱（游客下单时必填）
     */
    private String email;

    /**
     * 优惠券ID（可选）
     */
    private Long couponId;
}
