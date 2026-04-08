package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cococlown.cococlawservice.entity.Order;
import com.cococlown.cococlawservice.entity.Skill;
import com.cococlown.cococlawservice.entity.User;
import com.cococlown.cococlawservice.mapper.OrderMapper;
import com.cococlown.cococlawservice.mapper.SkillMapper;
import com.cococlown.cococlawservice.mapper.UserMapper;
import com.cococlown.cococlawservice.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仪表盘服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SkillMapper skillMapper;

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        // 今日订单数
        long todayOrders = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
            .ge(Order::getCreateTime, todayStart));

        // 今日营收
        BigDecimal todayRevenue = orderMapper.selectTodayRevenue(todayStart);
        if (todayRevenue == null) todayRevenue = BigDecimal.ZERO;

        // 总用户数
        long totalUsers = userMapper.selectCount(null);

        // 总技能数
        long totalSkills = skillMapper.selectCount(new LambdaQueryWrapper<Skill>()
            .eq(Skill::getStatus, 1));

        stats.put("todayOrders", todayOrders);
        stats.put("todayRevenue", todayRevenue);
        stats.put("totalUsers", totalUsers);
        stats.put("totalSkills", totalSkills);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getOrderTrend(Integer days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            long orders = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .ge(Order::getCreateTime, dayStart)
                .lt(Order::getCreateTime, dayEnd));

            BigDecimal revenue = orderMapper.selectTodayRevenue(dayStart);
            if (revenue == null) revenue = BigDecimal.ZERO;

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("orders", orders);
            dayData.put("revenue", revenue);
            trend.add(dayData);
        }
        return trend;
    }

    @Override
    public List<Map<String, Object>> getUserGrowth(Integer days) {
        List<Map<String, Object>> growth = new ArrayList<>();
        long total = userMapper.selectCount(null);

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            long newUsers = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .ge(User::getCreateTime, dayStart)
                .lt(User::getCreateTime, dayEnd));

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("newUsers", newUsers);
            dayData.put("total", total);
            growth.add(dayData);
        }
        return growth;
    }

    @Override
    public List<Map<String, Object>> getTopSkills(Integer limit) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Skill::getStatus, 1)
               .orderByDesc(Skill::getSalesCount)
               .last("LIMIT " + limit);

        List<Skill> skills = skillMapper.selectList(wrapper);
        return skills.stream().map(skill -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", skill.getId());
            map.put("name", skill.getName());
            map.put("category", skill.getCategory());
            map.put("sales", skill.getSalesCount());
            map.put("price", skill.getPrice());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getRecentOrders(Integer limit) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getCreateTime)
               .last("LIMIT " + limit);

        List<Order> orders = orderMapper.selectList(wrapper);
        return orders.stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("amount", order.getAmount());
            map.put("status", getStatusName(order.getStatus()));
            map.put("createTime", order.getCreateTime().toString());
            return map;
        }).collect(Collectors.toList());
    }

    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已完成";
            case 3: return "已取消";
            case 4: return "退款中";
            case 5: return "已退款";
            default: return "未知";
        }
    }
}
