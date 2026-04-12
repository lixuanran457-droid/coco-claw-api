package com.cococlown.cococlawservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResourcesDTO {
    /**
     * 用户API Key信息
     */
    private UserApiKeyDTO apiKey;

    /**
     * 用户订阅列表
     */
    private List<UserSubscriptionDTO> subscriptions;

    /**
     * 用户余额
     */
    private UserBalanceDTO balance;
}

@Data
class UserApiKeyDTO {
    private Long id;
    private String apiKey;
    private String baseUrl;
    private String status;
    private String currentType;
    private Long currentSubscriptionId;
}

@Data
class UserSubscriptionDTO {
    private Long id;
    private String name;
    private Long packageId;
    private BigDecimal dailyQuota;
    private BigDecimal dailyUsed;
    private BigDecimal dailyRemain;
    private BigDecimal totalQuota;
    private BigDecimal totalUsed;
    private BigDecimal totalRemain;
    private String status;
    private Integer isCurrent;
    private Integer daysRemaining;
    private String unit;
    private LocalDateTime expireAt;
}

@Data
class UserBalanceDTO {
    private Long id;
    private BigDecimal balance;
    private String ruleId;
    private Integer status;
}
