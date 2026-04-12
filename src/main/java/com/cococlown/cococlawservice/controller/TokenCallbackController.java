package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.SyncUsageRequest;
import com.cococlown.cococlawservice.service.UserTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token-callback")
public class TokenCallbackController {

    private static final Logger log = LoggerFactory.getLogger(TokenCallbackController.class);

    @Autowired
    private UserTokenService userTokenService;

    /**
     * 技术团队同步使用量回调
     * POST /api/token-callback/sync-usage
     */
    @PostMapping("/sync-usage")
    public Result<String> syncUsage(@RequestBody SyncUsageRequest request) {
        log.info("收到额度同步请求: apiKey={}, dailyUsed={}, monthlyUsed={}",
                request.getApiKey(), request.getDailyUsed(), request.getMonthlyUsed());

        try {
            boolean success = userTokenService.syncUsage(request);
            if (success) {
                return Result.success("同步成功");
            }
            return Result.error("同步失败，未找到对应订阅");
        } catch (Exception e) {
            log.error("额度同步异常", e);
            return Result.error("同步异常: " + e.getMessage());
        }
    }

    /**
     * 技术团队绑定规则成功回调
     * POST /api/token-callback/bind-success
     */
    @PostMapping("/bind-success")
    public Result<String> bindSuccess(@RequestBody BindSuccessRequest request) {
        log.info("收到绑定成功回调: apiKey={}, tenantId={}", request.getApiKey(), request.getTenantId());

        try {
            userTokenService.onBindSuccess(request.getApiKey(), request.getTenantId(), request.getBaseUrl());
            return Result.success("处理成功");
        } catch (Exception e) {
            log.error("绑定成功回调处理异常", e);
            return Result.error("处理异常: " + e.getMessage());
        }
    }

    /**
     * 技术团队超限通知回调
     * POST /api/token-callback/over-limit
     */
    @PostMapping("/over-limit")
    public Result<String> overLimit(@RequestBody OverLimitRequest request) {
        log.warn("收到超限通知: apiKey={}, overType={}, used={}, limit={}",
                request.getApiKey(), request.getOverType(), request.getUsedAmount(), request.getLimitAmount());

        // TODO: 记录超限通知，可以发送邮件/短信通知用户
        return Result.success("已收到通知");
    }

    // 内部类定义请求参数
    @lombok.Data
    public static class BindSuccessRequest {
        private String apiKey;
        private String tenantId;
        private String baseUrl;
    }

    @lombok.Data
    public static class OverLimitRequest {
        private String apiKey;
        private String tenantId;
        private String overType;  // DAILY/MONTHLY
        private java.math.BigDecimal usedAmount;
        private java.math.BigDecimal limitAmount;
    }
}
