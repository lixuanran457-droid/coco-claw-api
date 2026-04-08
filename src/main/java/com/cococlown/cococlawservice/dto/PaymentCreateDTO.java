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
     * 订单ID列表（从购物车购买）
     */
    private List<Long> cartIds;

    /**
     * 技能ID列表（直接购买）
     */
    private List<Long> skillIds;

    /**
     * 优惠券ID
     */
    private Long couponId;
}
