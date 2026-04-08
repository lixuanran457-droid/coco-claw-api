package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单创建DTO
 */
@Data
public class OrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 技能ID列表
     */
    private List<Long> skillIds;

    /**
     * 收货地址ID
     */
    private Long addressId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 用户备注
     */
    private String remark;

    /**
     * 支付方式
     */
    private String paymentMethod;
}
