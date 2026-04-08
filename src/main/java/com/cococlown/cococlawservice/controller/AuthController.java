package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.AuthResponseDTO;
import com.cococlown.cococlawservice.dto.LoginDTO;
import com.cococlown.cococlawservice.dto.RegisterDTO;
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
     * 用户登录
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
     * 用户注册
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
     * 发送验证码
     */
    @ApiOperation("发送验证码")
    @PostMapping("/captcha")
    public Result<Boolean> sendCaptcha(@RequestParam String phone) {
        try {
            boolean success = authService.sendCaptcha(phone);
            return Result.success("验证码已发送", success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
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
