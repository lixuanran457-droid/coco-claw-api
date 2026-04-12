package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.*;
import com.cococlown.cococlawservice.entity.BalanceRecharge;
import com.cococlown.cococlawservice.entity.TokenPackage;
import com.cococlown.cococlawservice.entity.UserBalance;
import com.cococlown.cococlawservice.entity.UserSubscription;
import com.cococlown.cococlawservice.service.TokenPackageService;
import com.cococlown.cococlawservice.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Autowired
    private TokenPackageService tokenPackageService;

    @Autowired
    private UserTokenService userTokenService;

    /**
     * 获取可用的套餐列表
     */
    @GetMapping("/packages")
    public Result<List<TokenPackageDTO>> getPackages() {
        return Result.success(tokenPackageService.getAvailablePackages());
    }

    /**
     * 获取套餐详情
     */
    @GetMapping("/packages/{id}")
    public Result<TokenPackageDTO> getPackageDetail(@PathVariable Long id) {
        TokenPackageDTO packageDetail = tokenPackageService.getPackageDetail(id);
        if (packageDetail == null) {
            return Result.error("套餐不存在");
        }
        return Result.success(packageDetail);
    }

    /**
     * 订阅套餐（创建订单）
     */
    @PostMapping("/subscribe")
    public Result<BalanceRecharge> subscribe(@Valid @RequestBody SubscribeRequest request,
                                              HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return Result.error("请先登录");
        }

        TokenPackage tokenPackage = tokenPackageService.getById(request.getPackageId());
        if (tokenPackage == null) {
            return Result.error("套餐不存在");
        }

        // TODO: 创建订阅订单，跳转到支付页面
        // 这里暂时返回成功，实际需要对接支付系统
        return Result.success(null);
    }

    /**
     * 充值余额（创建订单）
     */
    @PostMapping("/recharge-balance")
    public Result<BalanceRecharge> rechargeBalance(@Valid @RequestBody RechargeBalanceRequest request,
                                                    HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return Result.error("请先登录");
        }

        BalanceRecharge order = userTokenService.createRechargeOrder(userId, request.getAmount());
        return Result.success(order);
    }

    /**
     * 获取用户所有资源
     */
    @GetMapping("/user/resources")
    public Result<UserResourcesDTO> getUserResources(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return Result.error("请先登录");
        }

        UserResourcesDTO resources = userTokenService.getUserResources(userId);
        return Result.success(resources);
    }

    /**
     * 获取用户订阅列表
     */
    @GetMapping("/user/subscriptions")
    public Result<List<UserSubscription>> getUserSubscriptions(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return Result.error("请先登录");
        }

        List<UserSubscription> subscriptions = userTokenService.getUserSubscriptions(userId);
        return Result.success(subscriptions);
    }

    /**
     * 获取用户余额
     */
    @GetMapping("/user/balance")
    public Result<UserBalance> getUserBalance(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return Result.error("请先登录");
        }

        UserBalance balance = userTokenService.getUserBalance(userId);
        return Result.success(balance);
    }

    /**
     * 获取用户API Key
     */
    @GetMapping("/user/api-key")
    public Result<String> getUserApiKey(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return Result.error("请先登录");
        }

        String apiKey = userTokenService.getUserApiKey(userId);
        return Result.success(apiKey);
    }

    /**
     * 切换当前使用的资源
     */
    @PostMapping("/user/switch")
    public Result<String> switchResource(@Valid @RequestBody SwitchResourceRequest request,
                                          HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return Result.error("请先登录");
        }

        boolean success = userTokenService.switchResource(userId, request);
        if (success) {
            return Result.success("切换成功");
        }
        return Result.error("切换失败");
    }

    /**
     * 获取充值记录
     */
    @GetMapping("/user/recharge-history")
    public Result<List<BalanceRecharge>> getRechargeHistory(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return Result.error("请先登录");
        }

        List<BalanceRecharge> history = userTokenService.getRechargeHistory(userId);
        return Result.success(history);
    }

    /**
     * 获取订阅详情
     */
    @GetMapping("/user/subscription/{id}")
    public Result<UserSubscription> getSubscriptionDetail(@PathVariable Long id,
                                                          HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return Result.error("请先登录");
        }

        UserSubscription subscription = userTokenService.getSubscriptionDetail(id);
        if (subscription == null || !subscription.getUserId().equals(userId)) {
            return Result.error("订阅不存在");
        }
        return Result.success(subscription);
    }

    // 从请求中获取用户ID（模拟，实际从Token获取）
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // 实际应该从JWT Token或Session中获取
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr != null) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}
