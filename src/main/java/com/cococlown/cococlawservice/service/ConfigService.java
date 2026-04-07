package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.entity.SysConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 */
public interface ConfigService {

    /**
     * 获取所有配置
     */
    List<SysConfig> getAllConfigs();

    /**
     * 根据键获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据键获取配置值，如果不存在返回默认值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 根据键获取配置值，转换为整数
     */
    Integer getConfigValueAsInt(String configKey, Integer defaultValue);

    /**
     * 根据键获取配置值，转换为Boolean
     */
    Boolean getConfigValueAsBoolean(String configKey, Boolean defaultValue);

    /**
     * 保存或更新配置
     */
    boolean saveConfig(SysConfig config);

    /**
     * 批量保存或更新配置
     */
    boolean saveConfigs(List<SysConfig> configs);

    /**
     * 删除配置
     */
    boolean deleteConfig(Long id);

    /**
     * 初始化默认配置
     */
    void initDefaultConfigs();

    /**
     * 获取Banner列表
     */
    List<Map<String, Object>> getBannerList();

    /**
     * 获取货币符号
     */
    String getCurrencySymbol();

    /**
     * 获取分页大小
     */
    Integer getPageSize();
}
