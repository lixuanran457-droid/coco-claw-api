package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.dto.PaymentCreateDTO;
import com.cococlown.cococlawservice.dto.PaymentDTO;

import java.util.Map;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付订单
     */
    PaymentDTO createPayment(Long userId, PaymentCreateDTO dto);

    /**
     * 查询支付状态
     */
    PaymentDTO getPaymentStatus(Long orderId);

    /**
     * 第三方支付回调（支付宝/微信）
     */
    void handlePaymentCallback(String paymentMethod, Map<String, String> params);

    /**
     * 取消支付
     */
    boolean cancelPayment(Long orderId);

    /**
     * 申请退款
     */
    boolean applyRefund(Long orderId, String reason);

    /**
     * 处理退款
     */
    boolean processRefund(Long orderId);
}
