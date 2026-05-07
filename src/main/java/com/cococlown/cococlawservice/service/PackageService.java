package com.cococlown.cococlawservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cococlown.cococlawservice.entity.Package;
import java.util.List;

/**
 * 套餐Service接口
 */
public interface PackageService extends IService<Package> {

    /**
     * 获取可用的套餐列表
     * @return 套餐列表
     */
    List<Package> getAvailablePackages();

    /**
     * 订阅套餐
     * @param userId 用户ID
     * @param packageId 套餐ID
     * @return 是否成功
     */
    boolean subscribePackage(Long userId, Long packageId);

    /**
     * 获取用户的当前订阅
     * @param userId 用户ID
     * @return 订阅信息
     */
    com.cococlown.cococlawservice.entity.UserSubscription getUserSubscription(Long userId);

    /**
     * 切换用户订阅
     * @param userId 用户ID
     * @param packageId 套餐ID
     * @return 是否成功
     */
    boolean switchSubscription(Long userId, Long packageId);
}
