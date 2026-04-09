package com.cococlown.cococlawservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {

    private CallbackConfig callback = new CallbackConfig();
    private SandboxConfig sandbox = new SandboxConfig();
    private AlipayConfig alipay = new AlipayConfig();
    private WechatpayConfig wechatpay = new WechatpayConfig();

    @Data
    public static class CallbackConfig {
        private String baseUrl;
        private String alipayPath;
        private String wechatPath;
    }

    @Data
    public static class SandboxConfig {
        private boolean enabled = false;
    }

    @Data
    public static class AlipayConfig {
        private String appId;
        private String privateKey;
        private String alipayPublicKey;
        private String notifyUrl;
        private String returnUrl;
        private String signType = "RSA2";
        private String gateway = "https://openapi.alipay.com/gateway.do";
    }

    @Data
    public static class WechatpayConfig {
        private String mchid;
        private String serialNo;
        private String privateKeyPath;
        private String alipayCertPath;
        private String notifyUrl;
        private String apiKey;
        private String appid;
        private String domain = "https://api.mch.weixin.qq.com";
    }
}
