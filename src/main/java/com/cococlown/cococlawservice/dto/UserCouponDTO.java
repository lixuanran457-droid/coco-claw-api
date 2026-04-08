package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券DTO
 */
@Data
public class UserCouponDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long couponId;
    private String name;
    private Integer type;
    private String typeName;
    private BigDecimal value;
    private BigDecimal minAmount;
    private LocalDateTime receiveTime;
    private LocalDateTime useTime;
    private LocalDateTime expireTime;
    private Integer status;
    private Boolean canUse;
}
