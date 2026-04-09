package com.cococlown.cococlawservice.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cococlown.cococlawservice.dto.PaymentCreateDTO;
import com.cococlown.cococlawservice.dto.PaymentDTO;
import com.cococlown.cococlawservice.entity.Order;
import com.cococlown.cococlawservice.entity.Payment;
import com.cococlown.cococlawservice.entity.Skill;
import com.cococlown.cococlawservice.entity.UserSkill;
import com.cococlown.cococlawservice.mapper.OrderMapper;
import com.cococlown.cococlawservice.mapper.PaymentMapper;
import com.cococlown.cococlawservice.mapper.SkillMapper;
import com.cococlown.cococlawservice.mapper.UserSkillMapper;
import com.cococlown.cococlawservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SkillMapper skillMapper;

    @Autowired
    private UserSkillMapper userSkillMapper;

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private WechatpayService wechatpayService;

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
        payment.setCreateTime(LocalDateTime.now());
        payment.setExpireTime(LocalDateTime.now().plusMinutes(PAYMENT_EXPIRE_MINUTES));
        paymentMapper.insert(payment);

        // 调用真正的支付服务生成支付参数
        Map<String, Object> payParams = generateRealPayParams(order, payment);

        // 返回结果
        PaymentDTO result = new PaymentDTO();
        BeanUtils.copyProperties(payment, result);
        result.setOrderId(order.getId());
        result.setOrderNo(orderNo);
        result.setSkillName(skill.getName());
        result.setPayParams(payParams);

        return result;
    }

    /**
     * 生成真正的支付参数
     */
    private Map<String, Object> generateRealPayParams(Order order, Payment payment) {
        Map<String, Object> payParams = new HashMap<>();

        try {
            String paymentMethod = payment.getPaymentMethod();
            
            if ("alipay".equals(paymentMethod)) {
                // 支付宝WAP支付
                String payForm = alipayService.createWapPay(order, payment);
                payParams.put("payForm", payForm);
                payParams.put("type", "html"); // 前端需要渲染HTML表单
            } else if ("wechat".equals(paymentMethod)) {
                // 微信Native支付，返回二维码URL
                String codeUrl = wechatpayService.createNativePay(order, payment);
                payParams.put("codeUrl", codeUrl);
                payParams.put("type", "qrcode"); // 前端需要展示二维码
            } else if ("bankcard".equals(paymentMethod)) {
                // 银行卡支付（预留）
                payParams.put("type", "redirect");
                payParams.put("payUrl", "/pay/bankcard?orderNo=" + payment.getOrderNo());
            } else {
                // 默认模拟支付（沙箱环境）
                payParams.put("type", "mock");
                payParams.put("orderNo", payment.getOrderNo());
                payParams.put("mockPayUrl", "/pay/mock?orderNo=" + payment.getOrderNo());
            }
        } catch (Exception e) {
            log.error("生成支付参数失败: {}", e.getMessage());
            // 如果支付服务调用失败，返回沙箱参数
            payParams.put("type", "sandbox");
            payParams.put("orderNo", payment.getOrderNo());
            payParams.put("message", "沙箱环境测试");
        }

        return payParams;
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

        // 给用户发放商品（技能交付）
        deliverSkillToUser(order, userId, dto.getEmail(), skill);

        PaymentDTO result = new PaymentDTO();
        result.setOrderId(order.getId());
        result.setOrderNo(orderNo);
        result.setAmount(amount);
        result.setStatus(2); // 已支付
        result.setSkillName(skill.getName());

        return result;
    }

    /**
     * 技能交付 - 给用户发放技能
     */
    private void deliverSkillToUser(Order order, Long userId, String email, Skill skill) {
        // 确定用户标识（userId或email）
        String userIdentifier = userId != null ? userId.toString() : email;
        
        // 检查是否已经拥有该技能
        LambdaQueryWrapper<UserSkill> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(UserSkill::getUserId, userId);
        } else {
            wrapper.eq(UserSkill::getEmail, email);
        }
        wrapper.eq(UserSkill::getSkillId, skill.getId());
        UserSkill existingSkill = userSkillMapper.selectOne(wrapper);

        if (existingSkill != null) {
            // 已有该技能，增加使用次数
            existingSkill.setUsageCount(existingSkill.getUsageCount() + 1);
            existingSkill.setExpireTime(LocalDateTime.now().plusDays(365)); // 默认有效期1年
            existingSkill.setUpdatedAt(LocalDateTime.now());
            userSkillMapper.updateById(existingSkill);
            log.info("用户技能使用次数增加: userId={}, skillId={}", userIdentifier, skill.getId());
        } else {
            // 新增用户技能记录
            UserSkill userSkill = new UserSkill();
            userSkill.setUserId(userId);
            userSkill.setEmail(email != null ? email : null);
            userSkill.setOrderId(order.getId());
            userSkill.setSkillId(skill.getId());
            userSkill.setSkillName(skill.getName());
            userSkill.setSkillApiKey(skill.getApiKey()); // 复制API Key
            userSkill.setUsageCount(0);
            userSkill.setMaxUsageCount(skill.getMaxUsageCount() != null ? skill.getMaxUsageCount() : 0);
            userSkill.setExpireTime(LocalDateTime.now().plusDays(365)); // 默认有效期1年
            userSkill.setStatus(1); // 正常
            userSkill.setCreateTime(LocalDateTime.now());
            userSkill.setUpdatedAt(LocalDateTime.now());
            userSkillMapper.insert(userSkill);
            log.info("技能交付成功: userId={}, skillId={}", userIdentifier, skill.getId());
        }
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
        boolean paymentSuccess = false;
        
        if ("alipay".equals(paymentMethod)) {
            // 支付宝回调
            if ("TRADE_SUCCESS".equals(status) || "TRADE_FINISHED".equals(status)) {
                paymentSuccess = true;
            }
        } else if ("wechat".equals(paymentMethod)) {
            // 微信回调
            if ("SUCCESS".equals(params.get("result_code"))) {
                paymentSuccess = true;
                tradeNo = params.get("transaction_id");
            }
        }

        if (paymentSuccess) {
            payment.setStatus(2); // 已支付
            payment.setPayTime(LocalDateTime.now());
            payment.setTradeNo(tradeNo);

            // 更新订单状态并交付技能
            Order order = orderMapper.selectById(payment.getOrderId());
            if (order != null) {
                order.setStatus(2); // 已支付
                order.setPayTime(LocalDateTime.now());
                order.setTradeNo(tradeNo);
                orderMapper.updateById(order);
                
                // 交付技能
                Skill skill = skillMapper.selectById(order.getSkillId());
                if (skill != null) {
                    deliverSkillToUser(order, order.getUserId(), order.getEmail(), skill);
                }
            }
        } else if ("TRADE_CLOSED".equals(status) || "PAYERROR".equals(params.get("err_code"))) {
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

        // 尝试关闭第三方支付
        try {
            if ("alipay".equals(payment.getPaymentMethod())) {
                alipayService.closeTrade(payment.getOrderNo());
            } else if ("wechat".equals(payment.getPaymentMethod())) {
                wechatpayService.closeTrade(payment.getOrderNo());
            }
        } catch (Exception e) {
            log.warn("关闭支付失败: {}", e.getMessage());
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

        // 检查技能使用情况
        LambdaQueryWrapper<UserSkill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSkill::getOrderId, orderId);
        UserSkill userSkill = userSkillMapper.selectOne(wrapper);
        
        // 如果已使用，拒绝退款
        if (userSkill != null && userSkill.getUsageCount() > 0) {
            throw new RuntimeException("技能已使用，无法申请退款");
        }

        order.setStatus(5); // 退款中
        order.setRefundReason(reason);
        order.setRefundApplyTime(LocalDateTime.now());
        orderMapper.updateById(order);

        LambdaQueryWrapper<Payment> paymentWrapper = new LambdaQueryWrapper<>();
        paymentWrapper.eq(Payment::getOrderId, orderId);
        Payment payment = paymentMapper.selectOne(paymentWrapper);
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

        // 调用第三方支付退款
        LambdaQueryWrapper<Payment> paymentWrapper = new LambdaQueryWrapper<>();
        paymentWrapper.eq(Payment::getOrderId, orderId);
        Payment payment = paymentMapper.selectOne(paymentWrapper);

        boolean refundSuccess = false;
        if (payment != null) {
            try {
                if ("alipay".equals(payment.getPaymentMethod())) {
                    refundSuccess = alipayService.refund(order, order.getPayAmount(), order.getRefundReason());
                } else if ("wechat".equals(payment.getPaymentMethod())) {
                    refundSuccess = wechatpayService.refund(order, order.getPayAmount(), order.getRefundReason());
                }
            } catch (Exception e) {
                log.error("第三方退款失败: {}", e.getMessage());
            }
        }

        if (refundSuccess) {
            order.setStatus(6); // 已退款
            order.setRefundTime(LocalDateTime.now());
            orderMapper.updateById(order);

            if (payment != null) {
                payment.setStatus(5); // 已退款
                paymentMapper.updateById(payment);
            }

            // 撤销用户技能
            LambdaQueryWrapper<UserSkill> userSkillWrapper = new LambdaQueryWrapper<>();
            userSkillWrapper.eq(UserSkill::getOrderId, orderId);
            UserSkill userSkill = userSkillMapper.selectOne(userSkillWrapper);
            if (userSkill != null) {
                userSkillMapper.deleteById(userSkill);
                log.info("用户技能已撤销: userSkillId={}", userSkill.getId());
            }

            return true;
        }

        return false;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "COCO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
               + String.format("%04d", (int) (Math.random() * 10000));
    }
}
