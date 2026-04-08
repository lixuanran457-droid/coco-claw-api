package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录DTO
 */
@Data
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 邮箱（登录凭证）
     */
    private String email;

    /**
     * 密码（非必填，支持验证码登录）
     */
    private String password;

    /**
     * 验证码（邮箱验证码登录时使用）
     */
    private String captchaCode;
}
