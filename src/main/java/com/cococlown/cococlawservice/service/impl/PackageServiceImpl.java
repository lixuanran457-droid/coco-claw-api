package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cococlown.cococlawservice.entity.Package;
import com.cococlown.cococlawservice.entity.UserSubscription;
import com.cococlown.cococlawservice.mapper.PackageMapper;
import com.cococlown.cococlawservice.mapper.UserSubscriptionMapper;
import com.cococlown.cococlawservice.service.PackageService;
import com.cococlown.cococlawservice.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 套餐Service实现
 */
@Service
public class PackageServiceImpl extends ServiceImpl<PackageMapper, Package> implements PackageService {

    @Autowired
    private UserSubscriptionMapper userSubscriptionMapper;

    @Autowired
    private UserTokenService userTokenService;

    @Override
    public List<Package> getAvailablePackages() {
        return this.list(
            new LambdaQueryWrapper<Package>()
                .eq(Package::getStatus, 1)
                .orderByAsc(Package::getSortOrder)
        );
    }

    @Override
    @Transactional
    public boolean subscribePackage(Long userId, Long packageId) {
        Package pkg = this.getById(packageId);
        if (pkg == null || pkg.getStatus() != 1) {
            return false;
        }

        // 检查是否有生效中的订阅
        UserSubscription existing = userSubscriptionMapper.selectActiveByUserId(userId);
        if (existing != null) {
            // 已有订阅，累加时间
            existing.setTokenQuota(existing.getTokenQuota() + pkg.getTokenAmount());
            existing.setTokenRemaining(existing.getTokenRemaining() + pkg.getTokenAmount());
            existing.setExpireTime(existing.getExpireTime().plusDays(pkg.getDurationDays()));
            userSubscriptionMapper.updateById(existing);
        } else {
            // 创建新订阅
            UserSubscription subscription = new UserSubscription();
            subscription.setUserId(userId);
            subscription.setPackageId(packageId);
            subscription.setPackageName(pkg.getName());
            subscription.setTokenQuota(pkg.getTokenAmount());
            subscription.setTokenUsed(0);
            subscription.setTokenRemaining(pkg.getTokenAmount());
            subscription.setStartTime(LocalDateTime.now());
            subscription.setExpireTime(LocalDateTime.now().plusDays(pkg.getDurationDays()));
            subscription.setStatus(1);
            userSubscriptionMapper.insert(subscription);
        }

        // 初始化用户Token（如果不存在）
        userTokenService.initUserToken(userId);

        return true;
    }

    @Override
    public UserSubscription getUserSubscription(Long userId) {
        return userSubscriptionMapper.selectActiveByUserId(userId);
    }

    @Override
    @Transactional
    public boolean switchSubscription(Long userId, Long packageId) {
        // 先停用当前订阅
        UserSubscription current = getUserSubscription(userId);
        if (current != null) {
            current.setStatus(0);
            userSubscriptionMapper.updateById(current);
        }

        // 订阅新套餐
        return subscribePackage(userId, packageId);
    }
}
