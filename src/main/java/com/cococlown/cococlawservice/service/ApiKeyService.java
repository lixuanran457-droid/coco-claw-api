package com.cococlown.cococlawservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cococlown.cococlawservice.entity.ApiKey;
import java.util.List;

/**
 * API密钥Service接口
 */
public interface ApiKeyService extends IService<ApiKey> {

    /**
     * 创建API密钥
     * @param userId 用户ID
     * @param name 密钥名称
     * @param groupName 分组名称
     * @param note 备注
     * @return 完整的密钥（只返回一次）
     */
    String createApiKey(Long userId, String name, String groupName, String note);

    /**
     * 获取用户的API密钥列表
     * @param userId 用户ID
     * @return 密钥列表（不包含完整密钥）
     */
    List<ApiKey> getUserApiKeys(Long userId);

    /**
     * 删除API密钥
     * @param userId 用户ID
     * @param apiKeyId 密钥ID
     * @return 是否成功
     */
    boolean deleteApiKey(Long userId, Long apiKeyId);

    /**
     * 切换API密钥状态
     * @param userId 用户ID
     * @param apiKeyId 密钥ID
     * @param status 状态
     * @return 是否成功
     */
    boolean toggleApiKeyStatus(Long userId, Long apiKeyId, Integer status);

    /**
     * 验证API密钥
     * @param apiKey 密钥
     * @return 用户ID
     */
    Long validateApiKey(String apiKey);

    /**
     * 更新密钥使用信息
     * @param apiKeyId 密钥ID
     * @param ip IP地址
     */
    void updateUsageInfo(Long apiKeyId, String ip);
}
