package com.cococlown.cococlawservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cococlown.cococlawservice.entity.UserToken;

/**
 * 用户Token Service接口
 */
public interface UserTokenService extends IService<UserToken> {

    /**
     * 获取用户Token余额
     * @param userId 用户ID
     * @return 用户Token信息
     */
    UserToken getUserToken(Long userId);

    /**
     * 扣除Token
     * @param userId 用户ID
     * @param amount 数量
     * @return 是否成功
     */
    boolean deductToken(Long userId, Integer amount);

    /**
     * 增加Token
     * @param userId 用户ID
     * @param amount 数量
     * @return 是否成功
     */
    boolean addToken(Long userId, Integer amount);

    /**
     * 获取用户可用余额（考虑订阅）
     * @param userId 用户ID
     * @return 可用数量
     */
    Integer getAvailableBalance(Long userId);

    /**
     * 初始化用户Token账户
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean initUserToken(Long userId);
}
