package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户订阅表
 */
@Data
@TableName("user_subscription")
public class UserSubscription {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 套餐ID */
    private Long packageId;

    /** 套餐名称(冗余) */
    private String packageName;

    /** Token配额 */
    private Integer tokenQuota;

    /** 已使用 */
    private Integer tokenUsed;

    /** 剩余 */
    private Integer tokenRemaining;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 过期时间 */
    private LocalDateTime expireTime;

    /** 状态: 0-已过期, 1-生效中 */
    private Integer status;

    /** 自动续费: 0-否, 1-是 */
    private Integer autoRenew;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除: 0-未删除, 1-已删除 */
    @TableLogic
    private Integer deleted;
}
