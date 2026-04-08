package com.cococlown.cococlawservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单查询DTO
 */
@Data
public class OrderQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 订单状态筛选
     */
    private Integer status;

    /**
     * 搜索关键词（订单号/商品名称）
     */
    private String keyword;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
}
