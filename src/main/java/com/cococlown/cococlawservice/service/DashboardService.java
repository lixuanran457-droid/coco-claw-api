package com.cococlown.cococlawservice.service;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 获取统计数据
     */
    Map<String, Object> getStats();

    /**
     * 获取订单趋势
     */
    List<Map<String, Object>> getOrderTrend(Integer days);

    /**
     * 获取用户增长
     */
    List<Map<String, Object>> getUserGrowth(Integer days);

    /**
     * 获取热销技能
     */
    List<Map<String, Object>> getTopSkills(Integer limit);

    /**
     * 获取最近订单
     */
    List<Map<String, Object>> getRecentOrders(Integer limit);
}
