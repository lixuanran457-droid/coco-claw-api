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
import com.cococlown.cococlawservice.service.PaymentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    /**
     * 支付过期时间（分钟）
     */
    private static final int PAYMENT_EXPIRE_MINUTES = 30;

    @Override
    @Transactional
    public PaymentDTO createPayment(Long userId, PaymentCreateDTO dto) {
        // 校验参数
        if (dto.getSkillId() == null) {
            throw new RuntimeException("商品ID不能为空");
        }

        // 获取商品信息
        Skill skill = skillMapper.selectById(dto.getSkillId());
        if (skill == null) {
            throw new RuntimeException("商品不存在");
        }

        // 校验价格（免费商品直接创建订单，无需支付）
        if (skill.getPriceType() == 0 || skill.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            // 免费商品，创建已支付订单
            return createFreeOrder(userId, dto, skill);
        }

        // 校验邮箱（游客下单必须提供）
        if (userId == null && (dto.getEmail() == null || dto.getEmail().isEmpty())) {
            throw new RuntimeException("请填写邮箱地址");
        }

        // 生成订单号
        String orderNo = generateOrderNo();

        // 计算支付金额
        BigDecimal amount = skill.getPrice();

        // 创建订单记录
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setEmail(userId == null ? dto.getEmail() : null);
        order.setSkillId(skill.getId());
        order.setSkillName(skill.getName());
        order.setSkillIcon(skill.getIcon());
        order.setQuantity(1);
        order.setPrice(skill.getPrice());
        order.setTotalAmount(amount);
        order.setPayAmount(amount);
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setStatus(0); // 待支付
        order.setExpireTime(LocalDateTime.now().plusMinutes(PAYMENT_EXPIRE_MINUTES));
        orderMapper.insert(order);

        // 创建支付记录
        Payment payment = new Payment();
        payment.setOrderNo(orderNo);
        payment.setUserId(userId);
        payment.setOrderId(order.getId());
        payment.setAmount(amount);
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(0); // 待支付

        LocalDateTime now = LocalDateTime.now();
        payment.setCreateTime(now);
        payment.setExpireTime(now.plusMinutes(PAYMENT_EXPIRE_MINUTES));

        paymentMapper.insert(payment);

        // 生成支付链接/二维码
        String payUrl = generatePayUrl(payment, dto.getPaymentMethod());
        payment.setPayUrl(payUrl);
        paymentMapper.updateById(payment);

        // 返回结果
        PaymentDTO result = new PaymentDTO();
        BeanUtils.copyProperties(payment, result);
        result.setOrderId(order.getId());
        result.setOrderNo(orderNo);
        result.setSkillName(skill.getName());

        return result;
    }

    /**
     * 创建免费订单
     */
    private PaymentDTO createFreeOrder(Long userId, PaymentCreateDTO dto, Skill skill) {
        String orderNo = generateOrderNo();
        BigDecimal amount = BigDecimal.ZERO;

        // 创建订单记录
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setEmail(userId == null ? dto.getEmail() : null);
        order.setSkillId(skill.getId());
        order.setSkillName(skill.getName());
        order.setSkillIcon(skill.getIcon());
        order.setQuantity(1);
        order.setPrice(amount);
        order.setTotalAmount(amount);
        order.setPayAmount(amount);
        order.setPaymentMethod("free");
        order.setStatus(2); // 已支付
        order.setPayTime(LocalDateTime.now());
        orderMapper.insert(order);

        // TODO: 给用户发放商品

        PaymentDTO result = new PaymentDTO();
        result.setOrderId(order.getId());
        result.setOrderNo(orderNo);
        result.setAmount(amount);
        result.setStatus(2); // 已支付
        result.setSkillName(skill.getName());

        return result;
    }

    @Override
    public PaymentDTO getPaymentStatus(Long orderId) {
        Payment payment = paymentMapper.selectById(orderId);
        if (payment == null) {
            LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Payment::getOrderId, orderId);
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
        String orderNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String status = params.get("trade_status");

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
                order.setTradeNo(tradeNo);
                orderMapper.updateById(order);
            }
        } else if ("TRADE_CLOSED".equals(status)) {
            payment.setStatus(3); // 支付关闭
            Order order = orderMapper.selectById(payment.getOrderId());
            if (order != null) {
                order.setStatus(4); // 已取消
                orderMapper.updateById(order);
            }
        }

        paymentMapper.updateById(payment);
    }

    @Override
    @Transactional
    public boolean cancelPayment(Long orderId) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderId, orderId);
        Payment payment = paymentMapper.selectOne(wrapper);

        if (payment == null || payment.getStatus() != 0) {
            return false;
        }

        payment.setStatus(3); // 支付取消
        paymentMapper.updateById(payment);

        // 更新订单状态
        Order order = orderMapper.selectById(orderId);
        if (order != null) {
            order.setStatus(4); // 已取消
            orderMapper.updateById(order);
        }

        return true;
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
        if (order == null || order.getStatus() != 2) {
            return false;
        }

        order.setStatus(5); // 退款中
        order.setRefundReason(reason);
        order.setRefundApplyTime(LocalDateTime.now());
        orderMapper.updateById(order);

        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderId, orderId);
        Payment payment = paymentMapper.selectOne(wrapper);
        if (payment != null) {
            payment.setStatus(4); // 退款中
            paymentMapper.updateById(payment);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean processRefund(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 5) {
            return false;
        }

        order.setStatus(6); // 已退款
        order.setRefundTime(LocalDateTime.now());
        orderMapper.updateById(order);

        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderId, orderId);
        Payment payment = paymentMapper.selectOne(wrapper);
        if (payment != null) {
            payment.setStatus(5); // 已退款
            paymentMapper.updateById(payment);
        }

        return true;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "COCO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
               + String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * 生成支付链接/二维码（模拟）
     */
    private String generatePayUrl(Payment payment, String method) {
        if ("alipay".equals(method)) {
            return "https://openapi.alipay.com/gateway.do?out_trade_no=" + payment.getOrderNo();
        } else if ("wechat".equals(method)) {
            return "weixin://wxpay/bizpayurl?pr=" + payment.getOrderNo();
        }
        return "mock://pay/" + payment.getOrderNo();
    }
}
