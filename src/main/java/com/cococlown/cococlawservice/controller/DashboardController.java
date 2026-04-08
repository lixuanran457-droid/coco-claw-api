package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘控制器 - 统计和概览
 */
@Api(tags = "仪表盘统计")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取统计数据
     */
    @ApiOperation("获取统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = dashboardService.getStats();
        return Result.success(stats);
    }

    /**
     * 获取订单趋势数据
     */
    @ApiOperation("获取订单趋势")
    @GetMapping("/order-trend")
    public Result<List<Map<String, Object>>> getOrderTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        List<Map<String, Object>> trend = dashboardService.getOrderTrend(days);
        return Result.success(trend);
    }

    /**
     * 获取用户增长数据
     */
    @ApiOperation("获取用户增长")
    @GetMapping("/user-growth")
    public Result<List<Map<String, Object>>> getUserGrowth(
            @RequestParam(defaultValue = "7") Integer days) {
        List<Map<String, Object>> growth = dashboardService.getUserGrowth(days);
        return Result.success(growth);
    }

    /**
     * 获取热销技能TOP10
     */
    @ApiOperation("获取热销技能TOP10")
    @GetMapping("/top-skills")
    public Result<List<Map<String, Object>>> getTopSkills() {
        List<Map<String, Object>> skills = dashboardService.getTopSkills(10);
        return Result.success(skills);
    }

    /**
     * 获取最近订单
     */
    @ApiOperation("获取最近订单")
    @GetMapping("/recent-orders")
    public Result<List<Map<String, Object>>> getRecentOrders(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> orders = dashboardService.getRecentOrders(limit);
        return Result.success(orders);
    }
}
