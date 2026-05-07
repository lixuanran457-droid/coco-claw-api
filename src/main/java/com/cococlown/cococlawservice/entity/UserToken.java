package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户Token余额表
 */
@Data
@TableName("user_token")
public class UserToken {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** Token余额 */
    private Integer balance;

    /** 累计消耗 */
    private Integer totalConsumed;

    /** 累计充值 */
    private Integer totalRecharged;

    /** 冻结数量 */
    private Integer freezeAmount;

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
