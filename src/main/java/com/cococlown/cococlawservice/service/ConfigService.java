package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.entity.*;
import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 */
public interface ConfigService {

    /**
     * 获取首页所有配置数据
     */
    Map<String, Object> getHomeConfig();

    /**
     * 获取Banner列表
     */
    List<HomeBanner> getBanners();

    /**
     * 获取功能入口列表
     */
    List<HomeFeature> getFeatures();

    /**
     * 获取精选推荐列表
     */
    List<HomeRecommend> getRecommendations();

    /**
     * 获取FAQ列表
     */
    List<Faq> getFaqs();

    /**
     * 获取系统参数
     */
    String getSystemParam(String key);

    /**
     * 获取所有系统参数
     */
    Map<String, String> getAllSystemParams();
}
