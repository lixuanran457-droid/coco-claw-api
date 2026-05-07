package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.entity.Package;
import com.cococlown.cococlawservice.entity.UserSubscription;
import com.cococlown.cococlawservice.entity.UserToken;
import com.cococlown.cococlawservice.service.PackageService;
import com.cococlown.cococlawservice.service.UserTokenService;
import com.cococlown.cococlawservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Token管理Controller
 */
@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
@Tag(name = "Token管理")
public class TokenController {

    private final UserTokenService userTokenService;
    private final PackageService packageService;

    @GetMapping("/balance")
    @Operation(summary = "获取Token余额")
    public Result<UserToken> getBalance(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        UserToken userToken = userTokenService.getUserToken(userId);
        return Result.success(userToken);
    }

    @GetMapping("/packages")
    @Operation(summary = "获取套餐列表")
    public Result<List<Package>> getPackages() {
        List<Package> packages = packageService.getAvailablePackages();
        return Result.success(packages);
    }

    @GetMapping("/subscription")
    @Operation(summary = "获取当前订阅")
    public Result<UserSubscription> getSubscription(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        UserSubscription subscription = packageService.getUserSubscription(userId);
        return Result.success(subscription);
    }

    @PostMapping("/subscribe")
    @Operation(summary = "订阅套餐")
    public Result<Void> subscribe(
            @RequestBody Map<String, Long> body,
            HttpServletRequest request) {

        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        Long packageId = body.get("packageId");
        if (packageId == null) {
            return Result.error("请选择套餐");
        }

        boolean success = packageService.subscribePackage(userId, packageId);
        if (success) {
            return Result.success(null, "订阅成功");
        } else {
            return Result.error("订阅失败");
        }
    }

    @GetMapping("/dashboard")
    @Operation(summary = "获取用户面板数据")
    public Result<Map<String, Object>> getDashboard(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        Map<String, Object> dashboard = new HashMap<>();

        // Token余额
        UserToken userToken = userTokenService.getUserToken(userId);
        dashboard.put("tokenBalance", userToken != null ? userToken.getBalance() : 0);
        dashboard.put("totalConsumed", userToken != null ? userToken.getTotalConsumed() : 0);

        // 当前订阅
        UserSubscription subscription = packageService.getUserSubscription(userId);
        dashboard.put("subscription", subscription);
        if (subscription != null) {
            dashboard.put("packageDays", calculateRemainingDays(subscription.getExpireTime()));
        } else {
            dashboard.put("packageDays", 0);
        }

        return Result.success(dashboard);
    }

    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return JwtUtil.getUserId(token);
        }
        return null;
    }

    private long calculateRemainingDays(java.time.LocalDateTime expireTime) {
        if (expireTime == null) {
            return 0;
        }
        long days = java.time.Duration.between(java.time.LocalDateTime.now(), expireTime).toDays();
        return Math.max(0, days);
    }
}
