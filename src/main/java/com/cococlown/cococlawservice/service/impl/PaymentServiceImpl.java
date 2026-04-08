package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cococlown.cococlawservice.dto.PaymentCreateDTO;
import com.cococlown.cococlawservice.dto.PaymentDTO;
import com.cococlown.cococlawservice.entity.Order;
import com.cococlown.cococlawservice.entity.Payment;
import com.cococlown.cococlawservice.entity.Skill;
import com.cococlown.cococlawservice.mapper.OrderMapper;
import com.cococlown.cococlawservice.mapper.PaymentMapper;
import com.cococlown.cococlawservice.mapper.SkillMapper;
import com.cococlown.cococlawservice.service.CartService;
import com.cococlown.cococlawservice.service.PaymentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付服务实现类
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SkillMapper skillMapper;

    @Autowired
    private CartService cartService;

    /**
     * 支付过期时间（分钟）
     */
    private static final int PAYMENT_EXPIRE_MINUTES = 30;

    @Override
    @Transactional
    public PaymentDTO createPayment(Long userId, PaymentCreateDTO dto) {
        // TODO: 实际项目中应调用订单服务创建订单
        // 这里简化处理，直接创建Payment记录
        
        // 生成订单号
        String orderNo = generateOrderNo();
        
        // 计算金额（简化版，需要整合订单服务计算总价）
        BigDecimal amount = calculateAmount(userId, dto);
        
        // 创建支付记录
        Payment payment = new Payment();
        payment.setOrderNo(orderNo);
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(0); // 待支付
        
        LocalDateTime now = LocalDateTime.now();
        payment.setCreateTime(now);
        payment.setExpireTime(now.plusMinutes(PAYMENT_EXPIRE_MINUTES));
        
        paymentMapper.insert(payment);
        
        // 生成支付链接/二维码（模拟）
        String payUrl = generatePayUrl(payment, dto.getPaymentMethod());
        payment.setPayUrl(payUrl);
        paymentMapper.updateById(payment);
        
        PaymentDTO result = new PaymentDTO();
        BeanUtils.copyProperties(payment, result);
        result.setOrderNo(orderNo);
        
        return result;
    }

    @Override
    public PaymentDTO getPaymentStatus(Long orderId) {
        Payment payment = paymentMapper.selectById(orderId);
        if (payment == null) {
            // 通过订单号查询
            LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Payment::getOrderNo, orderId.toString());
            payment = paymentMapper.selectOne(wrapper);
        }
        
        if (payment == null) {
            throw new RuntimeException("支付记录不存在");
        }
        
        PaymentDTO dto = new PaymentDTO();
        BeanUtils.copyProperties(payment, dto);
        return dto;
    }

    @Override
    @Transactional
    public void handlePaymentCallback(String paymentMethod, Map<String, String> params) {
        // TODO: 实际项目中需要验证签名和处理回调
        // 这里简化处理，直接更新支付状态
        
        String orderNo = params.get("out_trade_no"); // 商家订单号
        String tradeNo = params.get("trade_no");     // 支付宝/微信交易号
        String status = params.get("trade_status"); // 交易状态
        
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderNo, orderNo);
        Payment payment = paymentMapper.selectOne(wrapper);
        
        if (payment == null) {
            throw new RuntimeException("支付记录不存在");
        }
        
        // 判断支付状态
        if ("TRADE_SUCCESS".equals(status) || "PAY_SUCCESS".equals(status)) {
            payment.setStatus(2); // 已支付
            payment.setPayTime(LocalDateTime.now());
            payment.setTradeNo(tradeNo);
            
            // 更新订单状态
            Order order = orderMapper.selectById(payment.getOrderId());
            if (order != null) {
                order.setStatus(2); // 已支付
                order.setPayTime(LocalDateTime.now());
                orderMapper.updateById(order);
            }
        } else if ("TRADE_CLOSED".equals(status)) {
            payment.setStatus(3); // 支付失败/关闭
        }
        
        paymentMapper.updateById(payment);
    }

    @Override
    @Transactional
    public boolean cancelPayment(Long orderId) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getId, orderId);
        Payment payment = paymentMapper.selectOne(wrapper);
        
        if (payment == null || payment.getStatus() != 0) {
            return false;
        }
        
        payment.setStatus(3); // 支付取消
        return paymentMapper.updateById(payment) > 0;
    }

    @Override
    @Transactional
    public boolean applyRefund(Long orderId) {
        return applyRefund(orderId, "用户申请退款");
    }

    @Override
    @Transactional
    public boolean applyRefund(Long orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 3) { // 只能退款已完成的订单
            return false;
        }
        
        // 更新订单状态为退款中
        order.setStatus(4); // 退款中
        order.setRefundReason(reason);
        order.setRefundApplyTime(LocalDateTime.now());
        
        // 更新支付记录
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderId, orderId);
        Payment payment = paymentMapper.selectOne(wrapper);
        if (payment != null) {
            payment.setStatus(4); // 退款中
            paymentMapper.updateById(payment);
        }
        
        return orderMapper.updateById(order) > 0;
    }

    @Override
    @Transactional
    public boolean processRefund(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 4) {
            return false;
        }
        
        // TODO: 实际项目中需要调用支付平台退款接口
        
        // 更新订单状态
        order.setStatus(5); // 已退款
        order.setRefundTime(LocalDateTime.now());
        
        // 更新支付记录
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderId, orderId);
        Payment payment = paymentMapper.selectOne(wrapper);
        if (payment != null) {
            payment.setStatus(5); // 已退款
            paymentMapper.updateById(payment);
        }
        
        // 返回技能给用户（根据业务需求）
        // TODO: 实现数字商品发放
        
        return orderMapper.updateById(order) > 0;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "COCO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
               + String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * 计算支付金额
     */
    private BigDecimal calculateAmount(Long userId, PaymentCreateDTO dto) {
        BigDecimal total = BigDecimal.ZERO;
        
        // 从购物车购买
        if (dto.getCartIds() != null && !dto.getCartIds().isEmpty()) {
            var cartList = cartService.getCartList(userId);
            for (var cart : cartList) {
                if (dto.getCartIds().contains(cart.getId())) {
                    total = total.add(cart.getTotalPrice());
                }
            }
        }
        
        // 直接购买
        if (dto.getSkillIds() != null && !dto.getSkillIds().isEmpty()) {
            for (Long skillId : dto.getSkillIds()) {
                Skill skill = skillMapper.selectById(skillId);
                if (skill != null) {
                    total = total.add(skill.getPrice());
                }
            }
        }
        
        // TODO: 减去优惠券金额
        
        return total;
    }

    /**
     * 生成支付链接/二维码（模拟）
     */
    private String generatePayUrl(Payment payment, String method) {
        // TODO: 实际项目中应调用支付宝/微信SDK生成真实支付链接
        if ("alipay".equals(method)) {
            return "https://openapi.alipay.com/gateway.do?out_trade_no=" + payment.getOrderNo();
        } else if ("wechat".equals(method)) {
            return "weixin://wxpay/bizpayurl?pr=" + payment.getOrderNo();
        }
        return "mock://pay/" + payment.getOrderNo();
    }
}
