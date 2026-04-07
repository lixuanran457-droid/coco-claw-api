package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.entity.Order;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 根据ID获取订单
     */
    Order getOrderById(Long id);

    /**
     * 创建订单
     */
    boolean createOrder(Order order);
}
