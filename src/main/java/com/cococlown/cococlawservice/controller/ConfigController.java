package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.entity.*;
import com.cococlown.cococlawservice.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置控制器 - 前端可配置数据接口
 */
@Api(tags = "系统配置")
@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = "*")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    /**
     * 获取首页所有配置数据
     */
    @ApiOperation("获取首页配置")
    @GetMapping("/home")
    public Result<Map<String, Object>> getHomeConfig() {
        return Result.success(configService.getHomeConfig());
    }

    /**
     * 获取Banner列表
     */
    @ApiOperation("获取Banner列表")
    @GetMapping("/banners")
    public Result<List<HomeBanner>> getBanners() {
        return Result.success(configService.getBanners());
    }

    /**
     * 获取功能入口列表
     */
    @ApiOperation("获取功能入口")
    @GetMapping("/features")
    public Result<List<HomeFeature>> getFeatures() {
        return Result.success(configService.getFeatures());
    }

    /**
     * 获取精选推荐
     */
    @ApiOperation("获取精选推荐")
    @GetMapping("/recommendations")
    public Result<List<HomeRecommend>> getRecommendations() {
        return Result.success(configService.getRecommendations());
    }

    /**
     * 获取FAQ列表
     */
    @ApiOperation("获取FAQ")
    @GetMapping("/faqs")
    public Result<List<Faq>> getFaqs() {
        return Result.success(configService.getFaqs());
    }

    /**
     * 获取单个系统参数
     */
    @ApiOperation("获取系统参数")
    @GetMapping("/param/{key}")
    public Result<String> getSystemParam(@PathVariable String key) {
        return Result.success(configService.getSystemParam(key));
    }

    /**
     * 获取所有系统参数
     */
    @ApiOperation("获取所有系统参数")
    @GetMapping("/params")
    public Result<Map<String, String>> getAllSystemParams() {
        return Result.success(configService.getAllSystemParams());
    }
}
