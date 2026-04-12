package com.cococlown.cococlawservice.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class SubscribeRequest {
    @NotNull(message = "请选择套餐")
    private Long packageId;
}
