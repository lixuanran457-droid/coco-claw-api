package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户技能表 - 记录用户购买的技能
 */
@Data
@TableName("user_skill")
public class UserSkill {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（注册用户）
     */
    private Long userId;

    /**
     * 邮箱（游客）
     */
    private String email;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 技能ID
     */
    private Long skillId;

    /**
     * 技能名称
     */
    private String skillName;

    /**
     * 技能API Key
     */
    private String skillApiKey;

    /**
     * 使用次数
     */
    private Integer usageCount;

    /**
     * 最大使用次数（0表示无限制）
     */
    private Integer maxUsageCount;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
