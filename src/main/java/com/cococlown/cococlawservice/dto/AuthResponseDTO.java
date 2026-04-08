package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 认证响应DTO
 */
@Data
public class AuthResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问Token
     */
    private String accessToken;

    /**
     * Token类型
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserInfoDTO user;

    @Data
    public static class UserInfoDTO implements Serializable {
        private Long id;
        private String username;
        private String phone;
        private String email;
        private Integer status;
    }
}
