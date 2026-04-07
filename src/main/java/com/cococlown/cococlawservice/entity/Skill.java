package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SKILL实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("skill")
public class Skill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SKILL ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * SKILL名称
     */
    private String name;

    /**
     * 图标URL
     */
    private String icon;

    /**
     * 图标背景色
     */
    private String iconBg;

    /**
     * 简短描述
     */
    private String shortDesc;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 安全等级
     */
    private Integer securityLevel;

    /**
     * 功能特点 (JSON格式存储)
     */
    private String features;

    /**
     * 使用说明
     */
    private String usage;

    /**
     * 状态: 0-下架, 1-上架, 2-精选
     */
    private Integer status;

    /**
     * 是否精选: 0-否, 1-是
     */
    private Integer featured;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
