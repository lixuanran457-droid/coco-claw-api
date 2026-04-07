package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.entity.SysConfig;
import com.cococlown.cococlawservice.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置控制器
 */
@Api(tags = "系统配置")
@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    /**
     * 获取所有配置（后台管理）
     */
    @ApiOperation("获取所有配置")
    @GetMapping("/list")
    public Result<List<SysConfig>> getAllConfigs() {
        List<SysConfig> configs = configService.getAllConfigs();
        return Result.success(configs);
    }

    /**
     * 保存配置（后台管理）
     */
    @ApiOperation("保存配置")
    @PostMapping("/save")
    public Result<Boolean> saveConfig(@RequestBody SysConfig config) {
        boolean success = configService.saveConfig(config);
        if (success) {
            return Result.success("保存成功", true);
        }
        return Result.error("保存失败");
    }

    /**
     * 批量保存配置（后台管理）
     */
    @ApiOperation("批量保存配置")
    @PostMapping("/batch-save")
    public Result<Boolean> batchSaveConfigs(@RequestBody List<SysConfig> configs) {
        boolean success = configService.saveConfigs(configs);
        if (success) {
            return Result.success("保存成功", true);
        }
        return Result.error("保存失败");
    }

    /**
     * 删除配置（后台管理）
     */
    @ApiOperation("删除配置")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteConfig(@PathVariable Long id) {
        boolean success = configService.deleteConfig(id);
        if (success) {
            return Result.success("删除成功", true);
        }
        return Result.error("删除失败");
    }

    /**
     * 获取Banner列表
     */
    @ApiOperation("获取Banner列表")
    @GetMapping("/banner")
    public Result<List<Map<String, Object>>> getBannerList() {
        List<Map<String, Object>> banners = configService.getBannerList();
        return Result.success(banners);
    }

    /**
     * 保存Banner列表
     */
    @ApiOperation("保存Banner列表")
    @PostMapping("/banner")
    public Result<Boolean> saveBannerList(@RequestBody List<Map<String, Object>> banners) {
        SysConfig config = new SysConfig();
        config.setConfigKey("banner_list");
        config.setConfigValue(com.alibaba.fastjson2.JSON.toJSONString(banners));
        config.setDescription("首页Banner图片列表（JSON数组）");
        boolean success = configService.saveConfig(config);
        if (success) {
            return Result.success("保存成功", true);
        }
        return Result.error("保存失败");
    }

    /**
     * 获取货币符号
     */
    @ApiOperation("获取货币符号")
    @GetMapping("/currency")
    public Result<String> getCurrencySymbol() {
        String symbol = configService.getCurrencySymbol();
        return Result.success(symbol);
    }

    /**
     * 设置货币符号
     */
    @ApiOperation("设置货币符号")
    @PostMapping("/currency")
    public Result<Boolean> setCurrencySymbol(@RequestParam String symbol) {
        SysConfig config = new SysConfig();
        config.setConfigKey("currency_symbol");
        config.setConfigValue(symbol);
        config.setDescription("货币符号（¥ 或 $）");
        boolean success = configService.saveConfig(config);
        if (success) {
            return Result.success("设置成功", true);
        }
        return Result.error("设置失败");
    }

    /**
     * 获取分页大小
     */
    @ApiOperation("获取分页大小")
    @GetMapping("/page-size")
    public Result<Integer> getPageSize() {
        Integer pageSize = configService.getPageSize();
        return Result.success(pageSize);
    }

    /**
     * 设置分页大小
     */
    @ApiOperation("设置分页大小")
    @PostMapping("/page-size")
    public Result<Boolean> setPageSize(@RequestParam Integer pageSize) {
        SysConfig config = new SysConfig();
        config.setConfigKey("page_size");
        config.setConfigValue(String.valueOf(pageSize));
        config.setDescription("分页大小");
        boolean success = configService.saveConfig(config);
        if (success) {
            return Result.success("设置成功", true);
        }
        return Result.error("设置失败");
    }

    /**
     * 获取Token有效期
     */
    @ApiOperation("获取Token有效期")
    @GetMapping("/token-expire")
    public Result<Integer> getTokenExpireDays() {
        Integer days = configService.getConfigValueAsInt("token_expire_days", 7);
        return Result.success(days);
    }

    /**
     * 设置Token有效期
     */
    @ApiOperation("设置Token有效期")
    @PostMapping("/token-expire")
    public Result<Boolean> setTokenExpireDays(@RequestParam Integer days) {
        SysConfig config = new SysConfig();
        config.setConfigKey("token_expire_days");
        config.setConfigValue(String.valueOf(days));
        config.setDescription("Token有效期（天）");
        boolean success = configService.saveConfig(config);
        if (success) {
            return Result.success("设置成功", true);
        }
        return Result.error("设置失败");
    }
}
