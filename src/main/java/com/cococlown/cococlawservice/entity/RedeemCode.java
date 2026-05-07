package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 兑换码表
 */
@Data
@TableName("redeem_code")
public class RedeemCode {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 兑换码 */
    private String code;

    /** 类型: 1-Token, 2-套餐 */
    private Integer type;

    /** Token数量 */
    private Integer tokenAmount;

    /** 套餐ID */
    private Long packageId;

    /** 套餐名称 */
    private String packageName;

    /** 套餐天数 */
    private Integer packageDays;

    /** 最大使用次数 */
    private Integer maxUseCount;

    /** 已使用次数 */
    private Integer usedCount;

    /** 绑定的用户ID */
    private Long userId;

    /** 绑定时间 */
    private LocalDateTime bindTime;

    /** 过期时间 */
    private LocalDateTime expireTime;

    /** 状态: 0-禁用, 1-可用, 2-已用完, 3-已过期 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 逻辑删除: 0-未删除, 1-已删除 */
    @TableLogic
    private Integer deleted;
}
