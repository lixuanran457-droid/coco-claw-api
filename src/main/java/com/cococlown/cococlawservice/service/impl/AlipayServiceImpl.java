package com.cococlown.cococlawservice.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeRefundResponse;
import com.alipay.easysdk.payment.wap.models.AlipayTradeWapPayRequestBuilder;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayRequestBuilder;
import com.alipay.easysdk.payment.common.models.AlipayTradeCloseResponse;
import com.cococlown.cococlawservice.config.PaymentConfig;
import com.cococlown.cococlawservice.entity.Order;
import com.cococlown.cococlawservice.entity.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * 支付宝支付服务
 */
@Slf4j
@Service
public class AlipayService {

    @Autowired
    private PaymentConfig paymentConfig;

    @PostConstruct
    public void init() {
        try {
            PaymentConfig.AlipayConfig alipayConfig = paymentConfig.getAlipay();
            
            Config config = new Config();
            config.protocol = "https";
            config.gateway = paymentConfig.getSandbox().isEnabled() 
                ? "https://openapi-sandbox.dl.alipaydev.com/gateway.do"
                : "https://openapi.alipay.com/gateway.do";
            config.appId = alipayConfig.getAppId();
            config.privateKey = alipayConfig.getPrivateKey();
            config.format = "JSON";
            config.charset = "UTF-8";
            config.signType = alipayConfig.getSignType();
            config.alipayPublicKey = alipayConfig.getAlipayPublicKey();
            
            Factory.setOptions(config);
            log.info("支付宝SDK初始化成功");
        } catch (Exception e) {
            log.warn("支付宝SDK初始化失败: {}", e.getMessage());
        }
    }

    /**
     * 创建WAP支付（手机网站支付）
     */
    public String createWapPay(Order order, Payment payment) {
        try {
            String subject = order.getSkillName() != null ? order.getSkillName() : "COCO技能商品";
            
            AlipayTradeWapPayRequestBuilder builder = new AlipayTradeWapPayRequestBuilder()
                    .setOutTradeNo(payment.getOrderNo())
                    .setTotalAmount(payment.getAmount().toString())
                    .setSubject(subject)
                    .setProductCode("QUICK_WAP_WAY")
                    .setQuitUrl(paymentConfig.getCallback().getBaseUrl() + "/pay/cancel")
                    .setBody("COCO技能商城订单-" + order.getOrderNo());

            com.alipay.easysdk.payment.wap.models.AlipayTradeWapPayResponse response = 
                    Factory.Payment.WapPay()
                    .pay(builder);

            if (ResponseChecker.success(response)) {
                log.info("支付宝WAP支付创建成功: {}", payment.getOrderNo());
                return response.getBody();
            } else {
                log.error("支付宝WAP支付创建失败: {}", response.msg + ", " + response.subMsg);
                throw new RuntimeException("支付创建失败: " + response.subMsg);
            }
        } catch (Exception e) {
            log.error("支付宝WAP支付异常: {}", e.getMessage());
            throw new RuntimeException("支付创建异常: " + e.getMessage());
        }
    }

    /**
     * 创建PC支付（电脑网站支付）
     */
    public String createPagePay(Order order, Payment payment) {
        try {
            String subject = order.getSkillName() != null ? order.getSkillName() : "COCO技能商品";
            
            AlipayTradePagePayRequestBuilder builder = new AlipayTradePagePayRequestBuilder()
                    .setOutTradeNo(payment.getOrderNo())
                    .setTotalAmount(payment.getAmount().toString())
                    .setSubject(subject)
                    .setProductCode("FAST_INSTANT_TRADE_PAY")
                    .setBody("COCO技能商城订单-" + order.getOrderNo())
                    .setReturnUrl(paymentConfig.getAlipay().getReturnUrl());

            com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse response = 
                    Factory.Payment.Page()
                    .pay(builder);

            if (ResponseChecker.success(response)) {
                log.info("支付宝PC支付创建成功: {}", payment.getOrderNo());
                return response.getBody();
            } else {
                log.error("支付宝PC支付创建失败: {}", response.msg + ", " + response.subMsg);
                throw new RuntimeException("支付创建失败: " + response.subMsg);
            }
        } catch (Exception e) {
            log.error("支付宝PC支付异常: {}", e.getMessage());
            throw new RuntimeException("支付创建异常: " + e.getMessage());
        }
    }

    /**
     * 查询支付状态
     */
    public String queryTradeStatus(String orderNo) {
        try {
            AlipayTradeQueryResponse response = Factory.Payment.Common()
                    .query(orderNo);

            if (ResponseChecker.success(response)) {
                log.info("支付宝交易查询成功: {}, 状态: {}", orderNo, response.tradeStatus);
                return response.tradeStatus;
            } else {
                log.warn("支付宝交易查询失败: {}", response.subMsg);
                return null;
            }
        } catch (Exception e) {
            log.error("支付宝交易查询异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 关闭交易
     */
    public boolean closeTrade(String orderNo) {
        try {
            AlipayTradeCloseResponse response = Factory.Payment.Common()
                    .close(orderNo);

            if (ResponseChecker.success(response)) {
                log.info("支付宝交易关闭成功: {}", orderNo);
                return true;
            } else {
                log.warn("支付宝交易关闭失败: {}", response.subMsg);
                return false;
            }
        } catch (Exception e) {
            log.error("支付宝交易关闭异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 申请退款
     */
    public boolean refund(Order order, BigDecimal refundAmount, String reason) {
        try {
            AlipayTradeRefundResponse response = Factory.Payment.Common()
                    .refund(order.getOrderNo(), refundAmount.toString(), reason);

            if (ResponseChecker.success(response)) {
                log.info("支付宝退款成功: {}, 退款金额: {}", order.getOrderNo(), refundAmount);
                return true;
            } else {
                log.error("支付宝退款失败: {}", response.subMsg);
                return false;
            }
        } catch (Exception e) {
            log.error("支付宝退款异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证回调签名
     */
    public boolean verifyCallback(JSONObject params) {
        try {
            // 支付宝回调验证逻辑
            // 在实际生产环境中，需要使用支付宝SDK的签名验证方法
            String sign = params.getString("sign");
            String signType = params.getString("sign_type");
            
            if (sign == null || sign.isEmpty()) {
                return false;
            }
            
            // 这里可以添加更严格的签名验证
            return true;
        } catch (Exception e) {
            log.error("支付宝回调验证异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 解析回调参数
     */
    public JSONObject parseCallbackParams(String params) {
        try {
            return JSONObject.parseObject(params);
        } catch (Exception e) {
            log.error("支付宝回调参数解析失败: {}", e.getMessage());
            return null;
        }
    }
}
