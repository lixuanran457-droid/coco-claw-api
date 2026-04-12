package com.cococlown.cococlawservice.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;

@Data
public class RechargeBalanceRequest {
    @NotNull(message = "请输入充值金额")
    @DecimalMin(value = "10", message = "最小充值金额为10元")
    private Double amount;
}
