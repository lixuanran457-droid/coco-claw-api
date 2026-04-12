package com.cococlown.cococlawservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SyncUsageRequest {
    /**
     * API Key
     */
    private String apiKey;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 今日已用
     */
    private BigDecimal dailyUsed;

    /**
     * 今日剩余
     */
    private BigDecimal dailyRemain;

    /**
     * 本月已用
     */
    private BigDecimal monthlyUsed;

    /**
     * 本月剩余
     */
    private BigDecimal monthlyRemain;

    /**
     * 状态：ACTIVE-正常 DISABLED-禁用
     */
    private String status;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 签名
     */
    private String sign;
}
