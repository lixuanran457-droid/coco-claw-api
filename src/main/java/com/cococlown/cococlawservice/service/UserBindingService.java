package com.cococlown.cococlawservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cococlown.cococlawservice.entity.UserBinding;
import java.util.List;

/**
 * 第三方绑定Service接口
 */
public interface UserBindingService extends IService<UserBinding> {

    /**
     * 绑定第三方账号
     * @param userId 用户ID
     * @param provider 平台
     * @param providerUserId 第三方用户ID
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     * @param tokenExpiresAt 令牌过期时间
     * @param nickname 昵称
     * @param avatar 头像
     * @return 是否成功
     */
    boolean bindAccount(Long userId, String provider, String providerUserId,
                        String accessToken, String refreshToken,
                        java.time.LocalDateTime tokenExpiresAt,
                        String nickname, String avatar);

    /**
     * 获取用户的绑定列表
     * @param userId 用户ID
     * @return 绑定列表
     */
    List<UserBinding> getUserBindings(Long userId);

    /**
     * 解绑第三方账号
     * @param userId 用户ID
     * @param provider 平台
     * @return 是否成功
     */
    boolean unbindAccount(Long userId, String provider);

    /**
     * 根据第三方信息获取绑定
     * @param provider 平台
     * @param providerUserId 第三方用户ID
     * @return 绑定信息
     */
    UserBinding getBindingByProviderUser(String provider, String providerUserId);

    /**
     * 检查是否已绑定
     * @param userId 用户ID
     * @param provider 平台
     * @return 是否已绑定
     */
    boolean isBound(Long userId, String provider);
}
