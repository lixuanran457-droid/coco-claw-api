package com.cococlown.cococlawservice.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cococlown.cococlawservice.entity.Order;
import com.cococlown.cococlawservice.entity.Payment;
import com.cococlown.cococlawservice.mapper.OrderMapper;
import com.cococlown.cococlawservice.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 订单超时定时任务
 * 用于自动关闭超时未支付的订单
 */
@Component
public class OrderExpireTask {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    /**
     * 每分钟执行一次，检查超时订单
     * cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void closeExpiredOrders() {
        LocalDateTime now = LocalDateTime.now();
        
        // 查询所有超时未支付的订单（状态=0待支付，且过期时间小于当前时间）
        LambdaUpdateWrapper<Order> orderWrapper = new LambdaUpdateWrapper<>();
        orderWrapper.eq(Order::getStatus, 0)  // 待支付
                   .lt(Order::getExpireTime, now);  // 已过期
        
        // 更新订单状态为已取消
        Order updateOrder = new Order();
        updateOrder.setStatus(4);  // 已取消
        updateOrder.setUpdateTime(now);
        
        int updatedCount = orderMapper.update(updateOrder, orderWrapper);
        
        if (updatedCount > 0) {
            System.out.println("【COCO CLAW】自动关闭 " + updatedCount + " 个超时订单");
            
            // 同时更新对应的支付记录
            // 注意：这里简化处理，实际应该通过子查询匹配订单号
        }
    }

    /**
     * 每天凌晨2点执行，对账检查
     * 检查支付状态和订单状态不一致的情况
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void reconciliationCheck() {
        System.out.println("【COCO CLAW】开始执行每日对账检查");
        
        // TODO: 实现对账逻辑
        // 1. 查询所有已支付的支付记录
        // 2. 检查对应的订单状态是否一致
        // 3. 如有不一致，记录日志并尝试修复
        // 4. 发送对账报告给管理员
    }

    /**
     * 每月1日凌晨3点执行，清理过期数据
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    public void cleanupExpiredData() {
        System.out.println("【COCO CLAW】开始清理过期数据");
        
        // TODO: 实现清理逻辑
        // 1. 清理超过6个月的已取消/已退款订单（根据业务需求决定保留多久）
        // 2. 清理过期的重置密码Token
        // 3. 清理过期的验证码记录
    }
}
