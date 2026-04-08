package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单DTO
 */
@Data
public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String orderNo;
    private Long skillId;
    private String skillName;
    private String skillIcon;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private String paymentMethod;
    private String tradeNo;
    private Integer status;
    private String statusName;
    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;
    private String remark;
    private String refundReason;
    private LocalDateTime payTime;
    private LocalDateTime refundApplyTime;
    private LocalDateTime refundTime;
    private LocalDateTime completeTime;
    private LocalDateTime expireTime;
    private LocalDateTime createTime;
}
