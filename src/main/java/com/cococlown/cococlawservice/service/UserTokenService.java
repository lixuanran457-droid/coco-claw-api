package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.dto.*;
import com.cococlown.cococlawservice.entity.BalanceRecharge;
import com.cococlown.cococlawservice.entity.UserBalance;
import com.cococlown.cococlawservice.entity.UserSubscription;
import java.util.List;

public interface UserTokenService {

    /**
     * 获取用户所有资源（订阅 + 余额）
     */
    UserResourcesDTO getUserResources(Long userId);

    /**
     * 获取用户订阅列表
     */
    List<UserSubscription> getUserSubscriptions(Long userId);

    /**
     * 获取用户余额
     */
    UserBalance getUserBalance(Long userId);

    /**
     * 获取用户API Key
     */
    String getUserApiKey(Long userId);

    /**
     * 切换当前使用的资源
     */
    boolean switchResource(Long userId, SwitchResourceRequest request);

    /**
     * 同步额度（技术团队回调）
     */
    boolean syncUsage(SyncUsageRequest request);

    /**
     * 绑定规则成功回调
     */
    void onBindSuccess(String apiKey, String tenantId, String baseUrl);

    /**
     * 获取余额充值记录
     */
    List<BalanceRecharge> getRechargeHistory(Long userId);

    /**
     * 创建余额充值订单
     */
    BalanceRecharge createRechargeOrder(Long userId, Double amountCny);

    /**
     * 充值订单支付成功
     */
    boolean onRechargePaid(String orderNo);

    /**
     * 获取用户订阅详情
     */
    UserSubscription getSubscriptionDetail(Long subscriptionId);
}
