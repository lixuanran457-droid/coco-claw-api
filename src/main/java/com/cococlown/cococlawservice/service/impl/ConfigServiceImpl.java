package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cococlown.cococlawservice.entity.*;
import com.cococlown.cococlawservice.mapper.*;
import com.cococlown.cococlawservice.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private HomeBannerMapper bannerMapper;

    @Autowired
    private HomeFeatureMapper featureMapper;

    @Autowired
    private HomeRecommendMapper recommendMapper;

    @Autowired
    private FaqMapper faqMapper;

    @Autowired
    private SystemParamsMapper paramsMapper;

    @Override
    public Map<String, Object> getHomeConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("banners", getBanners());
        config.put("features", getFeatures());
        config.put("recommendations", getRecommendations());
        config.put("faqs", getFaqs());
        config.put("systemParams", getAllSystemParams());
        return config;
    }

    @Override
    public List<HomeBanner> getBanners() {
        LambdaQueryWrapper<HomeBanner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeBanner::getStatus, 1)
               .orderByAsc(HomeBanner::getSortOrder);
        return bannerMapper.selectList(wrapper);
    }

    @Override
    public List<HomeFeature> getFeatures() {
        LambdaQueryWrapper<HomeFeature> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeFeature::getStatus, 1)
               .orderByAsc(HomeFeature::getSortOrder);
        return featureMapper.selectList(wrapper);
    }

    @Override
    public List<HomeRecommend> getRecommendations() {
        LambdaQueryWrapper<HomeRecommend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeRecommend::getStatus, 1)
               .orderByAsc(HomeRecommend::getSortOrder);
        return recommendMapper.selectList(wrapper);
    }

    @Override
    public List<Faq> getFaqs() {
        LambdaQueryWrapper<Faq> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Faq::getStatus, 1)
               .orderByAsc(Faq::getSortOrder);
        return faqMapper.selectList(wrapper);
    }

    @Override
    public String getSystemParam(String key) {
        LambdaQueryWrapper<SystemParams> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemParams::getParamKey, key)
               .eq(SystemParams::getStatus, 1);
        SystemParams param = paramsMapper.selectOne(wrapper);
        return param != null ? param.getParamValue() : null;
    }

    @Override
    public Map<String, String> getAllSystemParams() {
        LambdaQueryWrapper<SystemParams> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemParams::getStatus, 1);
        List<SystemParams> params = paramsMapper.selectList(wrapper);
        return params.stream()
                .collect(Collectors.toMap(
                        SystemParams::getParamKey,
                        SystemParams::getParamValue,
                        (v1, v2) -> v1
                ));
    }
}
