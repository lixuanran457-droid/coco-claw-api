package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 使用记录表
 */
@Data
@TableName("usage_record")
public class UsageRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** API密钥ID */
    private Long apiKeyId;

    /** 使用的模型 */
    private String model;

    /** 输入Token数 */
    private Integer inputTokens;

    /** 输出Token数 */
    private Integer outputTokens;

    /** 总Token数 */
    private Integer totalTokens;

    /** 消费金额 */
    private BigDecimal cost;

    /** 响应延迟(毫秒) */
    private Integer latencyMs;

    /** 请求IP */
    private String ipAddress;

    /** User-Agent */
    private String userAgent;

    /** 请求ID */
    private String requestId;

    /** 状态: 0-失败, 1-成功 */
    private Integer status;

    /** 错误信息 */
    private String errorMessage;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 逻辑删除: 0-未删除, 1-已删除 */
    @TableLogic
    private Integer deleted;
}
