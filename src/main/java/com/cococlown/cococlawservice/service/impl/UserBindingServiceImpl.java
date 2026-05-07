package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cococlown.cococlawservice.entity.UserBinding;
import com.cococlown.cococlawservice.mapper.UserBindingMapper;
import com.cococlown.cococlawservice.service.UserBindingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 第三方绑定Service实现
 */
@Service
public class UserBindingServiceImpl extends ServiceImpl<UserBindingMapper, UserBinding> implements UserBindingService {

    @Override
    @Transactional
    public boolean bindAccount(Long userId, String provider, String providerUserId,
                              String accessToken, String refreshToken,
                              LocalDateTime tokenExpiresAt,
                              String nickname, String avatar) {
        // 检查是否已存在绑定
        UserBinding existing = this.getOne(
            new LambdaQueryWrapper<UserBinding>()
                .eq(UserBinding::getUserId, userId)
                .eq(UserBinding::getProvider, provider)
        );

        if (existing != null) {
            // 更新现有绑定
            existing.setAccessToken(accessToken);
            existing.setRefreshToken(refreshToken);
            existing.setTokenExpiresAt(tokenExpiresAt);
            existing.setStatus(1);
            return this.updateById(existing);
        }

        // 创建新绑定
        UserBinding binding = new UserBinding();
        binding.setUserId(userId);
        binding.setProvider(provider);
        binding.setProviderUserId(providerUserId);
        binding.setAccessToken(accessToken);
        binding.setRefreshToken(refreshToken);
        binding.setTokenExpiresAt(tokenExpiresAt);
        binding.setNickname(nickname);
        binding.setAvatar(avatar);
        binding.setStatus(1);
        binding.setBindTime(LocalDateTime.now());

        return this.save(binding);
    }

    @Override
    public List<UserBinding> getUserBindings(Long userId) {
        return this.list(
            new LambdaQueryWrapper<UserBinding>()
                .eq(UserBinding::getUserId, userId)
                .eq(UserBinding::getStatus, 1)
        );
    }

    @Override
    @Transactional
    public boolean unbindAccount(Long userId, String provider) {
        return this.remove(
            new LambdaQueryWrapper<UserBinding>()
                .eq(UserBinding::getUserId, userId)
                .eq(UserBinding::getProvider, provider)
        );
    }

    @Override
    public UserBinding getBindingByProviderUser(String provider, String providerUserId) {
        return this.getOne(
            new LambdaQueryWrapper<UserBinding>()
                .eq(UserBinding::getProvider, provider)
                .eq(UserBinding::getProviderUserId, providerUserId)
                .eq(UserBinding::getStatus, 1)
        );
    }

    @Override
    public boolean isBound(Long userId, String provider) {
        return this.count(
            new LambdaQueryWrapper<UserBinding>()
                .eq(UserBinding::getUserId, userId)
                .eq(UserBinding::getProvider, provider)
                .eq(UserBinding::getStatus, 1)
        ) > 0;
    }
}
