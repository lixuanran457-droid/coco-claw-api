package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息DTO
 */
@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private Integer gender;
    private String birthday;
    private String bio;
    private BigDecimal balance;
    private Integer status;
    private LocalDateTime createTime;
}
