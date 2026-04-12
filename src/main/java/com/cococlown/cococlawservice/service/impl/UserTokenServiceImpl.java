package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cococlown.cococlawservice.dto.*;
import com.cococlown.cococlawservice.entity.*;
import com.cococlown.cococlawservice.mapper.*;
import com.cococlown.cococlawservice.service.UserTokenService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserTokenServiceImpl implements UserTokenService {

    private final UserSubscriptionMapper subscriptionMapper;
    private final UserBalanceMapper balanceMapper;
    private final UserApiKeyMapper apiKeyMapper;
    private final BalanceRechargeMapper rechargeMapper;
    private final QuotaSyncLogMapper syncLogMapper;
    private final BalanceConfigMapper configMapper;

    public UserTokenServiceImpl(UserSubscriptionMapper subscriptionMapper,
                                 UserBalanceMapper balanceMapper,
                                 UserApiKeyMapper apiKeyMapper,
                                 BalanceRechargeMapper rechargeMapper,
                                 QuotaSyncLogMapper syncLogMapper,
                                 BalanceConfigMapper configMapper) {
        this.subscriptionMapper = subscriptionMapper;
        this.balanceMapper = balanceMapper;
        this.apiKeyMapper = apiKeyMapper;
        this.rechargeMapper = rechargeMapper;
        this.syncLogMapper = syncLogMapper;
        this.configMapper = configMapper;
    }

    @Override
    public UserResourcesDTO getUserResources(Long userId) {
        UserResourcesDTO dto = new UserResourcesDTO();

        // 获取API Key
        LambdaQueryWrapper<UserApiKey> apiKeyWrapper = new LambdaQueryWrapper<>();
        apiKeyWrapper.eq(UserApiKey::getUserId, userId);
        apiKeyWrapper.eq(UserApiKey::getDeleted, 0);
        UserApiKey apiKey = apiKeyMapper.selectOne(apiKeyWrapper);
        if (apiKey != null) {
            UserApiKeyDTO apiKeyDTO = new UserApiKeyDTO();
            BeanUtils.copyProperties(apiKey, apiKeyDTO);
            dto.setApiKey(apiKeyDTO);
        }

        // 获取订阅列表
        List<UserSubscription> subscriptions = getUserSubscriptions(userId);
        List<UserSubscriptionDTO> subscriptionDTOs = new ArrayList<>();
        for (UserSubscription sub : subscriptions) {
            UserSubscriptionDTO subDto = new UserSubscriptionDTO();
            BeanUtils.copyProperties(sub, subDto);
            // 计算剩余天数
            if (sub.getExpireAt() != null) {
                long days = ChronoUnit.DAYS.between(LocalDateTime.now(), sub.getExpireAt());
                subDto.setDaysRemaining((int) Math.max(0, days));
            }
            subscriptionDTOs.add(subDto);
        }
        dto.setSubscriptions(subscriptionDTOs);

        // 获取余额
        UserBalance balance = getUserBalance(userId);
        if (balance != null) {
            UserBalanceDTO balanceDTO = new UserBalanceDTO();
            BeanUtils.copyProperties(balance, balanceDTO);
            dto.setBalance(balanceDTO);
        }

        return dto;
    }

    @Override
    public List<UserSubscription> getUserSubscriptions(Long userId) {
        LambdaQueryWrapper<UserSubscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSubscription::getUserId, userId);
        wrapper.in(UserSubscription::getStatus, "ACTIVE", "PENDING");
        wrapper.orderByDesc(UserSubscription::getCreatedAt);
        return subscriptionMapper.selectList(wrapper);
    }

    @Override
    public UserBalance getUserBalance(Long userId) {
        LambdaQueryWrapper<UserBalance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBalance::getUserId, userId);
        return balanceMapper.selectOne(wrapper);
    }

    @Override
    public String getUserApiKey(Long userId) {
        LambdaQueryWrapper<UserApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserApiKey::getUserId, userId);
        wrapper.eq(UserApiKey::getDeleted, 0);
        UserApiKey apiKey = apiKeyMapper.selectOne(wrapper);
        return apiKey != null ? apiKey.getApiKey() : null;
    }

    @Override
    @Transactional
    public boolean switchResource(Long userId, SwitchResourceRequest request) {
        // 先取消所有当前使用状态
        LambdaQueryWrapper<UserSubscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSubscription::getUserId, userId);
        wrapper.eq(UserSubscription::getIsCurrent, 1);
        List<UserSubscription> currentList = subscriptionMapper.selectList(wrapper);
        for (UserSubscription sub : currentList) {
            sub.setIsCurrent(0);
            subscriptionMapper.updateById(sub);
        }

        // 更新API Key的当前使用类型
        LambdaQueryWrapper<UserApiKey> apiKeyWrapper = new LambdaQueryWrapper<>();
        apiKeyWrapper.eq(UserApiKey::getUserId, userId);
        apiKeyWrapper.eq(UserApiKey::getDeleted, 0);
        UserApiKey apiKey = apiKeyMapper.selectOne(apiKeyWrapper);
        if (apiKey == null) {
            return false;
        }

        apiKey.setCurrentType(request.getType());
        if ("SUBSCRIPTION".equals(request.getType()) && request.getSubscriptionId() != null) {
            apiKey.setCurrentSubscriptionId(request.getSubscriptionId());
            // 设置该订阅为当前使用
            UserSubscription subscription = subscriptionMapper.selectById(request.getSubscriptionId());
            if (subscription != null) {
                subscription.setIsCurrent(1);
                subscriptionMapper.updateById(subscription);
            }
        } else {
            apiKey.setCurrentSubscriptionId(null);
        }
        apiKeyMapper.updateById(apiKey);

        // TODO: 通知技术团队切换规则

        return true;
    }

    @Override
    @Transactional
    public boolean syncUsage(SyncUsageRequest request) {
        // 根据API Key查找用户订阅
        LambdaQueryWrapper<UserSubscription> subWrapper = new LambdaQueryWrapper<>();
        subWrapper.eq(UserSubscription::getApiKey, request.getApiKey());
        UserSubscription subscription = subscriptionMapper.selectOne(subWrapper);
        if (subscription == null) {
            // 可能是余额模式，记录日志
            log.warn("未找到订阅: apiKey={}", request.getApiKey());
            return false;
        }

        // 记录同步日志
        QuotaSyncLog log = new QuotaSyncLog();
        log.setUserId(subscription.getUserId());
        log.setSubscriptionId(subscription.getId());
        log.setApiKey(request.getApiKey());
        log.setSyncType("PUSH");
        log.setBeforeDailyUsed(subscription.getDailyUsed());
        log.setBeforeTotalUsed(subscription.getTotalUsed());
        log.setAfterDailyUsed(request.getDailyUsed());
        log.setAfterTotalUsed(request.getMonthlyUsed());
        log.setStatus(1);
        syncLogMapper.insert(log);

        // 更新订阅额度
        subscription.setDailyUsed(request.getDailyUsed() != null ? request.getDailyUsed() : BigDecimal.ZERO);
        subscription.setDailyRemain(request.getDailyRemain());
        subscription.setTotalUsed(request.getMonthlyUsed() != null ? request.getMonthlyUsed() : BigDecimal.ZERO);
        if (request.getMonthlyUsed() != null && subscription.getTotalQuota() != null) {
            subscription.setTotalRemain(subscription.getTotalQuota().subtract(request.getMonthlyUsed()));
        }

        // 更新状态
        if ("DISABLED".equals(request.getStatus())) {
            subscription.setStatus("DISABLED");
        }

        subscriptionMapper.updateById(subscription);

        // 更新API Key的最后同步时间
        LambdaQueryWrapper<UserApiKey> apiKeyWrapper = new LambdaQueryWrapper<>();
        apiKeyWrapper.eq(UserApiKey::getApiKey, request.getApiKey());
        UserApiKey apiKey = apiKeyMapper.selectOne(apiKeyWrapper);
        if (apiKey != null) {
            apiKey.setLastSyncAt(LocalDateTime.now());
            apiKeyMapper.updateById(apiKey);
        }

        return true;
    }

    @Override
    @Transactional
    public void onBindSuccess(String apiKey, String tenantId, String baseUrl) {
        // 根据API Key查找并更新订阅
        LambdaQueryWrapper<UserSubscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSubscription::getApiKey, apiKey);
        UserSubscription subscription = subscriptionMapper.selectOne(wrapper);

        if (subscription != null) {
            subscription.setTenantId(tenantId);
            subscription.setBaseUrl(baseUrl);
            subscription.setStatus("ACTIVE");
            subscription.setStartedAt(LocalDateTime.now());
            subscriptionMapper.updateById(subscription);
        }

        // 更新API Key
        LambdaQueryWrapper<UserApiKey> apiKeyWrapper = new LambdaQueryWrapper<>();
        apiKeyWrapper.eq(UserApiKey::getApiKey, apiKey);
        UserApiKey userApiKey = apiKeyMapper.selectOne(apiKeyWrapper);
        if (userApiKey != null) {
            userApiKey.setTenantId(tenantId);
            userApiKey.setBaseUrl(baseUrl);
            userApiKey.setStatus("ACTIVE");
            apiKeyMapper.updateById(userApiKey);
        }
    }

    @Override
    public List<BalanceRecharge> getRechargeHistory(Long userId) {
        LambdaQueryWrapper<BalanceRecharge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BalanceRecharge::getUserId, userId);
        wrapper.orderByDesc(BalanceRecharge::getCreatedAt);
        return rechargeMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public BalanceRecharge createRechargeOrder(Long userId, Double amountCny) {
        // 获取汇率配置
        BalanceConfig config = configMapper.selectById(1L);
        BigDecimal exchangeRate = config != null ? config.getExchangeRate() : new BigDecimal("0.0700");
        BigDecimal amountUsd = new BigDecimal(amountCny).multiply(exchangeRate).setScale(2, RoundingMode.DOWN);

        // 创建充值订单
        BalanceRecharge recharge = new BalanceRecharge();
        recharge.setUserId(userId);
        recharge.setOrderNo("BR" + System.currentTimeMillis());
        recharge.setAmountCny(new BigDecimal(amountCny));
        recharge.setAmountUsd(amountUsd);
        recharge.setExchangeRate(exchangeRate);
        recharge.setStatus("PENDING");
        rechargeMapper.insert(recharge);

        return recharge;
    }

    @Override
    @Transactional
    public boolean onRechargePaid(String orderNo) {
        BalanceRecharge recharge = rechargeMapper.selectOne(
            new LambdaQueryWrapper<BalanceRecharge>()
                .eq(BalanceRecharge::getOrderNo, orderNo)
        );

        if (recharge == null || !"PENDING".equals(recharge.getStatus())) {
            return false;
        }

        // 更新订单状态
        recharge.setStatus("COMPLETED");
        recharge.setPaidAt(LocalDateTime.now());
        rechargeMapper.updateById(recharge);

        // 增加用户余额
        UserBalance balance = getUserBalance(recharge.getUserId());
        if (balance == null) {
            balance = new UserBalance();
            balance.setUserId(recharge.getUserId());
            balance.setBalance(recharge.getAmountUsd());
            balance.setTotalRecharged(recharge.getAmountUsd());
            balance.setStatus(1);
            balanceMapper.insert(balance);
        } else {
            balance.setBalance(balance.getBalance().add(recharge.getAmountUsd()));
            balance.setTotalRecharged(balance.getTotalRecharged().add(recharge.getAmountUsd()));
            balanceMapper.updateById(balance);
        }

        return true;
    }

    @Override
    public UserSubscription getSubscriptionDetail(Long subscriptionId) {
        return subscriptionMapper.selectById(subscriptionId);
    }
}
