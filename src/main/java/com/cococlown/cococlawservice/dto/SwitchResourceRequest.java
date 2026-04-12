package com.cococlown.cococlawservice.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class SwitchResourceRequest {
    /**
     * 使用类型：SUBSCRIPTION-订阅 BALANCE-余额
     */
    @NotNull(message = "请选择使用类型")
    private String type;

    /**
     * 订阅ID（type=SUBSCRIPTION时必填）
     */
    private Long subscriptionId;
}
