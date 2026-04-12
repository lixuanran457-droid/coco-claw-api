package com.cococlown.cococlawservice.dto;

import lombok.Data;

@Data
public class BindRuleRequest {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 规则ID
     */
    private String ruleId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 过期时间
     */
    private String expireAt;
}
