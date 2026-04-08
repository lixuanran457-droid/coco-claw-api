package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.dto.*;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录（邮箱+密码 或 邮箱+验证码）
     */
    AuthResponseDTO login(LoginDTO loginDTO);

    /**
     * 用户注册（邮箱注册）
     */
    AuthResponseDTO register(RegisterDTO registerDTO);

    /**
     * 发送验证码（用于登录/注册）
     */
    boolean sendCaptcha(String email);

    /**
     * 刷新Token
     */
    String refreshToken(String oldToken);

    /**
     * 登出
     */
    void logout(String token);

    /**
     * 发送重置密码邮件
     */
    void sendResetPasswordEmail(String email);

    /**
     * 重置密码（通过Token）
     */
    void resetPassword(ResetPasswordDTO dto);

    /**
     * 验证重置密码Token是否有效
     */
    boolean verifyResetToken(String token);
}
