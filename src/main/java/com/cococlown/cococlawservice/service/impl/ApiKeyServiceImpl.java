package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cococlown.cococlawservice.entity.ApiKey;
import com.cococlown.cococlawservice.mapper.ApiKeyMapper;
import com.cococlown.cococlawservice.service.ApiKeyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * API密钥Service实现
 */
@Service
public class ApiKeyServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKey> implements ApiKeyService {

    private static final String KEY_PREFIX = "sk_coco_";

    @Override
    @Transactional
    public String createApiKey(Long userId, String name, String groupName, String note) {
        // 生成唯一密钥
        String rawKey = KEY_PREFIX + UUID.randomUUID().toString().replace("-", "");
        String prefix = rawKey.substring(0, 15) + "...";

        ApiKey apiKey = new ApiKey();
        apiKey.setUserId(userId);
        apiKey.setName(name);
        apiKey.setApiKey(rawKey);
        apiKey.setPrefix(prefix);
        apiKey.setGroupName(groupName);
        apiKey.setNote(note);
        apiKey.setStatus(1);
        apiKey.setCreateTime(LocalDateTime.now());

        this.save(apiKey);

        // 返回完整密钥（只此一次）
        return rawKey;
    }

    @Override
    public List<ApiKey> getUserApiKeys(Long userId) {
        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiKey::getUserId, userId)
               .orderByDesc(ApiKey::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public boolean deleteApiKey(Long userId, Long apiKeyId) {
        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiKey::getUserId, userId)
               .eq(ApiKey::getId, apiKeyId);
        return this.remove(wrapper);
    }

    @Override
    @Transactional
    public boolean toggleApiKeyStatus(Long userId, Long apiKeyId, Integer status) {
        ApiKey apiKey = this.getOne(
            new LambdaQueryWrapper<ApiKey>()
                .eq(ApiKey::getUserId, userId)
                .eq(ApiKey::getId, apiKeyId)
        );
        if (apiKey == null) {
            return false;
        }
        apiKey.setStatus(status);
        return this.updateById(apiKey);
    }

    @Override
    public Long validateApiKey(String apiKey) {
        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiKey::getApiKey, apiKey)
               .eq(ApiKey::getStatus, 1);
        ApiKey key = this.getOne(wrapper);
        return key != null ? key.getUserId() : null;
    }

    @Override
    public void updateUsageInfo(Long apiKeyId, String ip) {
        ApiKey apiKey = this.getById(apiKeyId);
        if (apiKey != null) {
            apiKey.setLastUsedAt(LocalDateTime.now());
            apiKey.setLastUsedIp(ip);
            this.updateById(apiKey);
        }
    }
}
