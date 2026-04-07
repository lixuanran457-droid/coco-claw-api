package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * SKILL查询DTO
 */
@Data
public class SkillQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否精选
     */
    private Integer featured;
}
