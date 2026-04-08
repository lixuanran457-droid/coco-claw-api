package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 地址DTO
 */
@Data
public class AddressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String receiverName;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private String postalCode;
    private Integer isDefault;
    private String tag;
    
    /**
     * 完整地址
     */
    private String fullAddress;
}
