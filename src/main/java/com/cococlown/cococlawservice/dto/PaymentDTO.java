package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 支付DTO
 */
@Data
public class PaymentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long orderId;
    private String orderNo;
    private String paymentMethod;
    private String tradeNo;      // 第三方支付流水号
    private BigDecimal amount;
    private Integer status;      // 0-待支付, 1-支付中, 2-已支付, 3-支付失败/取消, 4-退款中, 5-已退款
    private String payUrl;       // 支付链接/二维码
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;  // 订单过期时间
    private String skillName;    // 商品名称
    
    /**
     * 支付参数（根据支付方式返回不同格式）
     * - alipay: {type: "html", payForm: "..."}
     * - wechat: {type: "qrcode", codeUrl: "..."}
     * - sandbox: {type: "sandbox", orderNo: "..."}
     */
    private Map<String, Object> payParams;
}
