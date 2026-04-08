package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.*;
import com.cococlown.cococlawservice.filter.JwtAuthenticationFilter;
import com.cococlown.cococlawservice.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证控制器 - P0核心模块
 */
@Api(tags = "用户认证")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${jwt.expiration:86400000}")
    private Long tokenExpiration;

    /**
     * 用户登录（邮箱+密码 或 邮箱+验证码）
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        try {
            AuthResponseDTO authResponse = authService.login(loginDTO);
            
            // 设置httpOnly Cookie存储Token
            setAuthCookie(response, authResponse.getAccessToken());
            
            return Result.success("登录成功", authResponse);
        } catch (RuntimeException e) {
            return Result.error(401, e.getMessage());
        }
    }

    /**
     * 用户注册（邮箱注册）
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<AuthResponseDTO> register(@RequestBody RegisterDTO registerDTO, HttpServletResponse response) {
        try {
            AuthResponseDTO authResponse = authService.register(registerDTO);
            
            // 设置httpOnly Cookie存储Token
            setAuthCookie(response, authResponse.getAccessToken());
            
            return Result.success("注册成功", authResponse);
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
    public Result<String> refreshToken(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                        HttpServletResponse response) {
        try {
            // 从Header或Cookie获取Token
            String token = extractToken(authHeader);
            String newToken = authService.refreshToken(token);
            
            // 更新Cookie中的Token
            setAuthCookie(response, newToken);
            
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
    public Result<Boolean> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                  HttpServletResponse response) {
        try {
            String token = extractToken(authHeader);
            authService.logout(token);
            
            // 清除Cookie
            clearAuthCookie(response);
            
            return Result.success("登出成功", true);
        } catch (Exception e) {
            // 即使出错也要清除Cookie
            clearAuthCookie(response);
            return Result.success("登出成功", true);
        }
    }

    /**
     * 获取当前登录用户信息（从Token解析）
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/me")
    public Result<AuthResponseDTO.UserInfoDTO> getCurrentUser() {
        try {
            // 从SecurityContext获取当前用户信息
            Object principal = org.springframework.security.core.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
            
            if (principal instanceof Long) {
                Long userId = (Long) principal;
                String email = (String) org.springframework.security.core.SecurityContextHolder
                    .getContext().getAuthentication().getDetails();
                
                AuthResponseDTO.UserInfoDTO userInfo = new AuthResponseDTO.UserInfoDTO();
                userInfo.setId(userId);
                userInfo.setEmail(email);
                
                return Result.success(userInfo);
            }
            
            return Result.error(401, "未登录");
        } catch (Exception e) {
            return Result.error(401, "未登录");
        }
    }

    /**
     * 设置认证Cookie（httpOnly，防止XSS）
     */
    private void setAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(JwtAuthenticationFilter.TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(true); // 防止XSS攻击
        cookie.setSecure(true); // HTTPS only（开发环境可设为false）
        cookie.setPath("/");
        cookie.setMaxAge((int) (tokenExpiration / 1000)); // 与Token有效期一致
        cookie.setSameSite("Lax"); // 防止CSRF
        response.addCookie(cookie);
    }

    /**
     * 清除认证Cookie
     */
    private void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(JwtAuthenticationFilter.TOKEN_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 立即过期
        cookie.setSameSite("Lax");
        response.addCookie(cookie);
    }

    /**
     * 从Header提取Token
     */
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}
