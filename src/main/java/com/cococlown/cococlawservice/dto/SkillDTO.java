package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * SKILL数据传输对象
 */
@Data
public class SkillDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SKILL ID
     */
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
     * 分类名称
     */
    private String categoryName;

    /**
     * 安全等级
     */
    private Integer securityLevel;

    /**
     * 功能特点
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
     * 是否精选
     */
    private Integer featured;
}
