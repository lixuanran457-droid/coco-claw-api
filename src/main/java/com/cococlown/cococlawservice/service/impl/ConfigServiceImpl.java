package com.cococlown.cococlawservice.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cococlown.cococlawservice.entity.SysConfig;
import com.cococlown.cococlawservice.mapper.SysConfigMapper;
import com.cococlown.cococlawservice.service.ConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 系统配置服务实现类
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CACHE_PREFIX = "coco:config:";
    private static final long CACHE_EXPIRE_MINUTES = 30;

    // 默认配置
    private static final String DEFAULT_BANNER_LIST = "[]";
    private static final String DEFAULT_CURRENCY_SYMBOL = "¥";
    private static final int DEFAULT_PAGE_SIZE = 30;
    private static final int DEFAULT_TOKEN_EXPIRE_DAYS = 7;

    @PostConstruct
    public void init() {
        initDefaultConfigs();
    }

    @Override
    public List<SysConfig> getAllConfigs() {
        return configMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        // 先从缓存获取
        String cacheKey = CACHE_PREFIX + configKey;
        String cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(cachedValue)) {
            return cachedValue;
        }

        // 从数据库获取
        SysConfig config = configMapper.selectOne(
            new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, configKey)
        );
        
        String value = config != null ? config.getConfigValue() : defaultValue;
        
        // 写入缓存
        if (StringUtils.isNotBlank(value)) {
            redisTemplate.opsForValue().set(cacheKey, value, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        }
        
        return value;
    }

    @Override
    public Integer getConfigValueAsInt(String configKey, Integer defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public Boolean getConfigValueAsBoolean(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveConfig(SysConfig config) {
        // 检查是否已存在
        SysConfig existing = configMapper.selectOne(
            new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, config.getConfigKey())
        );
        
        if (existing != null) {
            config.setId(existing.getId());
            return configMapper.updateById(config) > 0;
        } else {
            return configMapper.insert(config) > 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveConfigs(List<SysConfig> configs) {
        for (SysConfig config : configs) {
            saveConfig(config);
        }
        // 清除缓存
        clearCache();
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConfig(Long id) {
        SysConfig config = configMapper.selectById(id);
        if (config != null) {
            // 清除缓存
            redisTemplate.delete(CACHE_PREFIX + config.getConfigKey());
        }
        return configMapper.deleteById(id) > 0;
    }

    @Override
    public void initDefaultConfigs() {
        // Banner列表
        if (getConfigValue("banner_list") == null) {
            SysConfig bannerConfig = new SysConfig();
            bannerConfig.setConfigKey("banner_list");
            bannerConfig.setConfigValue(DEFAULT_BANNER_LIST);
            bannerConfig.setDescription("首页Banner图片列表（JSON数组）");
            saveConfig(bannerConfig);
        }

        // 货币符号
        if (getConfigValue("currency_symbol") == null) {
            SysConfig currencyConfig = new SysConfig();
            currencyConfig.setConfigKey("currency_symbol");
            currencyConfig.setConfigValue(DEFAULT_CURRENCY_SYMBOL);
            currencyConfig.setDescription("货币符号（¥ 或 $）");
            saveConfig(currencyConfig);
        }

        // 分页大小
        if (getConfigValue("page_size") == null) {
            SysConfig pageSizeConfig = new SysConfig();
            pageSizeConfig.setConfigKey("page_size");
            pageSizeConfig.setConfigValue(String.valueOf(DEFAULT_PAGE_SIZE));
            pageSizeConfig.setDescription("分页大小");
            saveConfig(pageSizeConfig);
        }

        // Token有效期
        if (getConfigValue("token_expire_days") == null) {
            SysConfig tokenConfig = new SysConfig();
            tokenConfig.setConfigKey("token_expire_days");
            tokenConfig.setConfigValue(String.valueOf(DEFAULT_TOKEN_EXPIRE_DAYS));
            tokenConfig.setDescription("Token有效期（天）");
            saveConfig(tokenConfig);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getBannerList() {
        String value = getConfigValue("banner_list", DEFAULT_BANNER_LIST);
        try {
            JSONArray array = JSON.parseArray(value);
            List<Map<String, Object>> banners = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Map<String, Object> banner = new HashMap<>();
                banner.put("id", obj.getLong("id"));
                banner.put("imageUrl", obj.getString("imageUrl"));
                banner.put("linkUrl", obj.getString("linkUrl"));
                banner.put("sortOrder", obj.getInteger("sortOrder"));
                banner.put("isActive", obj.getInteger("isActive"));
                banners.add(banner);
            }
            return banners;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public String getCurrencySymbol() {
        return getConfigValue("currency_symbol", DEFAULT_CURRENCY_SYMBOL);
    }

    @Override
    public Integer getPageSize() {
        return getConfigValueAsInt("page_size", DEFAULT_PAGE_SIZE);
    }

    /**
     * 清除所有配置缓存
     */
    private void clearCache() {
        // 清除缓存前缀下的所有key
        redisTemplate.delete(redisTemplate.keys(CACHE_PREFIX + "*"));
    }
}
