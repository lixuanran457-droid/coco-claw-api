package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_api_key")
public class UserApiKey {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * API Key
     */
    private String apiKey;

    /**
     * API 地址
     */
    private String baseUrl;

    /**
     * 技术团队租户ID
     */
    private String tenantId;

    /**
     * SUBSCRIPTION-订阅 BALANCE-余额
     */
    private String currentType;

    /**
     * 当前使用的订阅ID（null表示使用余额）
     */
    private Long currentSubscriptionId;

    /**
     * ACTIVE-激活 DISABLED-禁用
     */
    private String status;

    /**
     * 最后同步时间
     */
    private LocalDateTime lastSyncAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
