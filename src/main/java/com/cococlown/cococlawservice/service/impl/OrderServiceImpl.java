package com.cococlown.cococlawservice.service.impl;

import com.cococlown.cococlawservice.common.Constants;
import com.cococlown.cococlawservice.entity.Order;
import com.cococlown.cococlawservice.mapper.OrderMapper;
import com.cococlown.cococlawservice.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrder(Order order) {
        // 生成订单编号
        if (StringUtils.isBlank(order.getOrderNo())) {
            order.setOrderNo(generateOrderNo());
        }
        // 设置默认状态
        if (order.getStatus() == null) {
            order.setStatus(Constants.OrderStatus.PENDING);
        }
        return orderMapper.insert(order) > 0;
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
