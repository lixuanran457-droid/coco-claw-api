package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * API密钥表
 */
@Data
@TableName("api_key")
public class ApiKey {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 密钥名称 */
    private String name;

    /** API密钥 */
    private String apiKey;

    /** 密钥前缀（用于显示） */
    private String prefix;

    /** 分组名称 */
    private String groupName;

    /** 状态: 0-禁用, 1-启用 */
    private Integer status;

    /** 最后使用时间 */
    private LocalDateTime lastUsedAt;

    /** 最后使用IP */
    private String lastUsedIp;

    /** 过期时间 */
    private LocalDateTime expiresAt;

    /** 备注 */
    private String note;

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
