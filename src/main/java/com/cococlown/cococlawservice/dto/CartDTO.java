package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车DTO
 */
@Data
public class CartDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long skillId;
    private String skillName;
    private String skillIcon;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private Integer stock;
}
