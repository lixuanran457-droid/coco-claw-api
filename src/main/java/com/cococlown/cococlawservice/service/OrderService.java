package com.cococlown.cococlawservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cococlown.cococlawservice.dto.OrderCreateDTO;
import com.cococlown.cococlawservice.dto.OrderDTO;
import com.cococlown.cococlawservice.dto.OrderQueryDTO;
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
    Order createOrder(Long userId, OrderCreateDTO dto);

    /**
     * 获取用户订单列表（分页）
     */
    IPage<OrderDTO> getUserOrderPage(Long userId, OrderQueryDTO query);

    /**
     * 获取订单详情
     */
    OrderDTO getOrderDetail(Long userId, Long orderId);

    /**
     * 取消订单
     */
    boolean cancelOrder(Long userId, Long orderId);

    /**
     * 确认收货
     */
    boolean confirmReceive(Long userId, Long orderId);

    /**
     * 申请退款
     */
    boolean applyRefund(Long userId, Long orderId, String reason);

    /**
     * 删除订单（逻辑删除）
     */
    boolean deleteOrder(Long userId, Long orderId);

    /**
     * 后台：获取所有订单（分页）
     */
    IPage<OrderDTO> getAdminOrderPage(OrderQueryDTO query);

    /**
     * 后台：更新订单状态
     */
    boolean updateOrderStatus(Long orderId, Integer status);

    /**
     * 后台：处理退款
     */
    boolean processRefund(Long orderId);

    // ==================== 游客订单查询 ====================

    /**
     * 游客：发送查询验证码到邮箱
     */
    void sendGuestQueryCaptcha(String email);

    /**
     * 游客：通过邮箱查询订单
     */
    IPage<OrderDTO> getGuestOrderPage(String email, String captcha, OrderQueryDTO query);
}
