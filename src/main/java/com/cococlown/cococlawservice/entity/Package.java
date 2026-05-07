package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐表
 */
@Data
@TableName("package")
public class Package {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 套餐名称 */
    private String name;

    /** 图标 */
    private String icon;

    /** 描述 */
    private String description;

    /** 价格 */
    private BigDecimal price;

    /** 原价 */
    private BigDecimal originalPrice;

    /** 包含Token数量 */
    private Integer tokenAmount;

    /** 有效期(天) */
    private Integer durationDays;

    /** 每日限制(0=不限) */
    private Integer dailyLimit;

    /** 可用模型(JSON数组) */
    private String models;

    /** 功能特点(JSON数组) */
    private String features;

    /** 排序 */
    private Integer sortOrder;

    /** 状态: 0-下架, 1-上架 */
    private Integer status;

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
