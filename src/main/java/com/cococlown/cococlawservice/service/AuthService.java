package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.dto.AuthResponseDTO;
import com.cococlown.cococlawservice.dto.LoginDTO;
import com.cococlown.cococlawservice.dto.RegisterDTO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    AuthResponseDTO login(LoginDTO loginDTO);

    /**
     * 用户注册
     */
    AuthResponseDTO register(RegisterDTO registerDTO);

    /**
     * 发送验证码
     */
    boolean sendCaptcha(String phone);

    /**
     * 刷新Token
     */
    String refreshToken(String oldToken);

    /**
     * 登出
     */
    void logout(String token);
}
