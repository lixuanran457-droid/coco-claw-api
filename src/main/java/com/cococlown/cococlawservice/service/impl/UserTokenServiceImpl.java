package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cococlown.cococlawservice.entity.UserToken;
import com.cococlown.cococlawservice.mapper.UserTokenMapper;
import com.cococlown.cococlawservice.service.UserTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户Token Service实现
 */
@Service
public class UserTokenServiceImpl extends ServiceImpl<UserTokenMapper, UserToken> implements UserTokenService {

    @Override
    public UserToken getUserToken(Long userId) {
        UserToken userToken = this.getOne(
            new LambdaQueryWrapper<UserToken>()
                .eq(UserToken::getUserId, userId)
        );

        if (userToken == null) {
            initUserToken(userId);
            userToken = this.getOne(
                new LambdaQueryWrapper<UserToken>()
                    .eq(UserToken::getUserId, userId)
            );
        }

        return userToken;
    }

    @Override
    @Transactional
    public boolean deductToken(Long userId, Integer amount) {
        UserToken userToken = getUserToken(userId);
        if (userToken == null || userToken.getBalance() < amount) {
            return false;
        }

        userToken.setBalance(userToken.getBalance() - amount);
        userToken.setTotalConsumed(userToken.getTotalConsumed() + amount);
        return this.updateById(userToken);
    }

    @Override
    @Transactional
    public boolean addToken(Long userId, Integer amount) {
        UserToken userToken = getUserToken(userId);
        if (userToken == null) {
            initUserToken(userId);
            userToken = getUserToken(userId);
        }

        if (userToken != null) {
            userToken.setBalance(userToken.getBalance() + amount);
            userToken.setTotalRecharged(userToken.getTotalRecharged() + amount);
            return this.updateById(userToken);
        }

        return false;
    }

    @Override
    public Integer getAvailableBalance(Long userId) {
        UserToken userToken = getUserToken(userId);
        return userToken != null ? userToken.getBalance() : 0;
    }

    @Override
    public boolean initUserToken(Long userId) {
        UserToken existing = this.getOne(
            new LambdaQueryWrapper<UserToken>()
                .eq(UserToken::getUserId, userId)
        );

        if (existing != null) {
            return true;
        }

        UserToken userToken = new UserToken();
        userToken.setUserId(userId);
        userToken.setBalance(0);
        userToken.setTotalConsumed(0);
        userToken.setTotalRecharged(0);
        userToken.setFreezeAmount(0);

        return this.save(userToken);
    }
}
