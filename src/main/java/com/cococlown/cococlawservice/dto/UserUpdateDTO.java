package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新DTO
 */
@Data
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别: 0-未知, 1-男, 2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 个人简介
     */
    private String bio;
}
