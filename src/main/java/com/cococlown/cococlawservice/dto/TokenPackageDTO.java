package com.cococlown.cococlawservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TokenPackageDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String ruleId;
    private String ruleType;
    private BigDecimal dailyQuota;
    private BigDecimal totalQuota;
    private String unit;
    private Integer durationDays;
    private String durationType;
    private Integer status;
    private Integer isRecommended;
    private Integer sortOrder;
}
