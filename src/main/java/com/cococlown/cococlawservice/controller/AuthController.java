package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.*;
import com.cococlown.cococlawservice.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器 - P0核心模块
 */
@Api(tags = "用户认证")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录（邮箱+密码 或 邮箱+验证码）
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            AuthResponseDTO response = authService.login(loginDTO);
            return Result.success("登录成功", response);
        } catch (RuntimeException e) {
            return Result.error(401, e.getMessage());
        }
    }

    /**
     * 用户注册（邮箱注册）
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<AuthResponseDTO> register(@RequestBody RegisterDTO registerDTO) {
        try {
            AuthResponseDTO response = authService.register(registerDTO);
            return Result.success("注册成功", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 发送验证码（用于登录/注册）
     */
    @ApiOperation("发送验证码")
    @PostMapping("/captcha")
    public Result<Boolean> sendCaptcha(@RequestParam String email) {
        try {
            boolean success = authService.sendCaptcha(email);
            return Result.success("验证码已发送", success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 忘记密码 - 发送重置邮件
     */
    @ApiOperation("忘记密码 - 发送重置邮件")
    @PostMapping("/forget-password")
    public Result<String> forgetPassword(@RequestBody ForgetPasswordDTO dto) {
        try {
            authService.sendResetPasswordEmail(dto.getEmail());
            // 统一返回成功，防止枚举攻击
            return Result.success("如果该邮箱已注册，我们将发送密码重置链接", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 重置密码（通过Token）
     */
    @ApiOperation("重置密码")
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody ResetPasswordDTO dto) {
        try {
            authService.resetPassword(dto);
            return Result.success("密码重置成功", null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 验证重置Token是否有效
     */
    @ApiOperation("验证重置Token")
    @GetMapping("/verify-reset-token")
    public Result<Boolean> verifyResetToken(@RequestParam String token) {
        boolean valid = authService.verifyResetToken(token);
        return Result.success(valid);
    }

    /**
     * 刷新Token
     */
    @ApiOperation("刷新Token")
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            String newToken = authService.refreshToken(token);
            return Result.success("Token已刷新", newToken);
        } catch (RuntimeException e) {
            return Result.error(401, e.getMessage());
        }
    }

    /**
     * 登出
     */
    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Result<Boolean> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            authService.logout(token);
            return Result.success("登出成功", true);
        } catch (Exception e) {
            return Result.success("登出成功", true);
        }
    }
}
