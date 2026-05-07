package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 第三方账号绑定表
 */
@Data
@TableName("user_binding")
public class UserBinding {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 第三方平台: github, dingtalk, asktoken */
    private String provider;

    /** 第三方用户ID */
    private String providerUserId;

    /** 访问令牌 */
    private String accessToken;

    /** 刷新令牌 */
    private String refreshToken;

    /** 令牌过期时间 */
    private LocalDateTime tokenExpiresAt;

    /** 第三方昵称 */
    private String nickname;

    /** 第三方头像 */
    private String avatar;

    /** 状态: 0-失效, 1-有效 */
    private Integer status;

    /** 绑定时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime bindTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除: 0-未删除, 1-已删除 */
    @TableLogic
    private Integer deleted;
}
