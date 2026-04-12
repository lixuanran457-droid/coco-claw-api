package com.cococlown.cococlawservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.entity.*;
import com.cococlown.cococlawservice.mapper.*;
import com.cococlown.cococlawservice.service.TokenPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/token")
public class AdminTokenController {

    @Autowired
    private TokenPackageService tokenPackageService;

    @Autowired
    private TokenPackageMapper packageMapper;

    @Autowired
    private UserSubscriptionMapper subscriptionMapper;

    @Autowired
    private UserBalanceMapper balanceMapper;

    @Autowired
    private BalanceRechargeMapper rechargeMapper;

    @Autowired
    private QuotaSyncLogMapper syncLogMapper;

    // ==================== 套餐管理 ====================

    /**
     * 获取套餐列表
     */
    @GetMapping("/package/list")
    public Result<Page<TokenPackage>> getPackageList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        Page<TokenPackage> pageResult = new Page<>(page, limit);
        Page<TokenPackage> result = tokenPackageService.page(pageResult,
            new LambdaQueryWrapper<TokenPackage>()
                .orderByDesc(TokenPackage::getSortOrder)
        );
        return Result.success(result);
    }

    /**
     * 创建套餐
     */
    @PostMapping("/package")
    public Result<String> createPackage(@RequestBody TokenPackage tokenPackage) {
        boolean success = tokenPackageService.createPackage(tokenPackage);
        return success ? Result.success("创建成功") : Result.error("创建失败");
    }

    /**
     * 更新套餐
     */
    @PutMapping("/package/{id}")
    public Result<String> updatePackage(@PathVariable Long id, @RequestBody TokenPackage tokenPackage) {
        tokenPackage.setId(id);
        boolean success = tokenPackageService.updatePackage(tokenPackage);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除套餐
     */
    @DeleteMapping("/package/{id}")
    public Result<String> deletePackage(@PathVariable Long id) {
        boolean success = tokenPackageService.deletePackage(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量导入套餐
     */
    @PostMapping("/package/import")
    public Result<String> importPackages(@RequestBody List<TokenPackage> packages) {
        for (TokenPackage pkg : packages) {
            pkg.setConfigSource("IMPORT");
            tokenPackageService.createPackage(pkg);
        }
        return Result.success("导入成功，共" + packages.size() + "条");
    }

    // ==================== 用户订阅管理 ====================

    /**
     * 获取用户订阅列表
     */
    @GetMapping("/subscription/list")
    public Result<Page<Map<String, Object>>> getSubscriptionList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status) {
        Page<UserSubscription> pageResult = new Page<>(page, limit);
        LambdaQueryWrapper<UserSubscription> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(UserSubscription::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(UserSubscription::getStatus, status);
        }
        wrapper.orderByDesc(UserSubscription::getCreatedAt);
        Page<UserSubscription> result = subscriptionMapper.selectPage(pageResult, wrapper);

        // 转换为包含用户信息的Map
        Page<Map<String, Object>> mapResult = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (UserSubscription sub : result.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sub.getId());
            map.put("userId", sub.getUserId());
            map.put("name", sub.getName());
            map.put("ruleId", sub.getRuleId());
            map.put("dailyQuota", sub.getDailyQuota());
            map.put("dailyUsed", sub.getDailyUsed());
            map.put("totalQuota", sub.getTotalQuota());
            map.put("totalUsed", sub.getTotalUsed());
            map.put("status", sub.getStatus());
            map.put("isCurrent", sub.getIsCurrent());
            map.put("expireAt", sub.getExpireAt());
            map.put("createdAt", sub.getCreatedAt());
            records.add(map);
        }
        mapResult.setRecords(records);

        return Result.success(mapResult);
    }

    // ==================== 用户余额管理 ====================

    /**
     * 获取用户余额列表
     */
    @GetMapping("/balance/list")
    public Result<Page<Map<String, Object>>> getBalanceList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Long userId) {
        Page<UserBalance> pageResult = new Page<>(page, limit);
        LambdaQueryWrapper<UserBalance> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(UserBalance::getUserId, userId);
        }
        wrapper.orderByDesc(UserBalance::getUpdatedAt);
        Page<UserBalance> result = balanceMapper.selectPage(pageResult, wrapper);

        Page<Map<String, Object>> mapResult = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (UserBalance bal : result.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", bal.getId());
            map.put("userId", bal.getUserId());
            map.put("balance", bal.getBalance());
            map.put("totalRecharged", bal.getTotalRecharged());
            map.put("totalConsumed", bal.getTotalConsumed());
            map.put("status", bal.getStatus());
            map.put("updatedAt", bal.getUpdatedAt());
            records.add(map);
        }
        mapResult.setRecords(records);

        return Result.success(mapResult);
    }

    // ==================== 充值记录管理 ====================

    /**
     * 获取充值记录列表
     */
    @GetMapping("/recharge/list")
    public Result<Page<BalanceRecharge>> getRechargeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status) {
        Page<BalanceRecharge> pageResult = new Page<>(page, limit);
        LambdaQueryWrapper<BalanceRecharge> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(BalanceRecharge::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(BalanceRecharge::getStatus, status);
        }
        wrapper.orderByDesc(BalanceRecharge::getCreatedAt);
        Page<BalanceRecharge> result = rechargeMapper.selectPage(pageResult, wrapper);
        return Result.success(result);
    }

    // ==================== 同步日志管理 ====================

    /**
     * 获取同步日志
     */
    @GetMapping("/sync-log")
    public Result<Page<QuotaSyncLog>> getSyncLog(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String apiKey) {
        Page<QuotaSyncLog> pageResult = new Page<>(page, limit);
        LambdaQueryWrapper<QuotaSyncLog> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(QuotaSyncLog::getUserId, userId);
        }
        if (apiKey != null) {
            wrapper.eq(QuotaSyncLog::getApiKey, apiKey);
        }
        wrapper.orderByDesc(QuotaSyncLog::getCreatedAt);
        Page<QuotaSyncLog> result = syncLogMapper.selectPage(pageResult, wrapper);
        return Result.success(result);
    }

    // ==================== 统计 ====================

    /**
     * 获取TOKEN业务统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 订阅总数
        long subscriptionCount = subscriptionMapper.selectCount(null);
        stats.put("subscriptionCount", subscriptionCount);

        // 活跃订阅数
        long activeSubscriptionCount = subscriptionMapper.selectCount(
            new LambdaQueryWrapper<UserSubscription>().eq(UserSubscription::getStatus, "ACTIVE")
        );
        stats.put("activeSubscriptionCount", activeSubscriptionCount);

        // 余额总金额
        List<UserBalance> balances = balanceMapper.selectList(null);
        java.math.BigDecimal totalBalance = balances.stream()
            .map(UserBalance::getBalance)
            .filter(b -> b != null)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        stats.put("totalBalance", totalBalance);

        // 今日充值金额
        java.time.LocalDateTime todayStart = java.time.LocalDateTime.now().toLocalDate().atStartOfDay();
        List<BalanceRecharge> todayRecharges = rechargeMapper.selectList(
            new LambdaQueryWrapper<BalanceRecharge>()
                .ge(BalanceRecharge::getCreatedAt, todayStart)
                .eq(BalanceRecharge::getStatus, "COMPLETED")
        );
        java.math.BigDecimal todayAmount = todayRecharges.stream()
            .map(BalanceRecharge::getAmountCny)
            .filter(a -> a != null)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        stats.put("todayRechargeAmount", todayAmount);

        return Result.success(stats);
    }
}
