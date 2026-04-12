package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("quota_sync_log")
public class QuotaSyncLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long subscriptionId;

    private String apiKey;

    /**
     * PUSH-技术团队推送 PULL-我们拉取
     */
    private String syncType;

    /**
     * 同步前今日已用
     */
    private BigDecimal beforeDailyUsed;

    /**
     * 同步前已用总额
     */
    private BigDecimal beforeTotalUsed;

    /**
     * 同步后今日已用
     */
    private BigDecimal afterDailyUsed;

    /**
     * 同步后已用总额
     */
    private BigDecimal afterTotalUsed;

    /**
     * 原始同步数据
     */
    private String rawData;

    /**
     * 1-成功 0-失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
