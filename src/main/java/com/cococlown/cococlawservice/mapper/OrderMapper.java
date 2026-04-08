package com.cococlown.cococlawservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cococlown.cococlawservice.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单 Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 查询指定时间段的营收
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM orders WHERE status = 2 AND create_time >= #{startTime}")
    BigDecimal selectTodayRevenue(@Param("startTime") LocalDateTime startTime);
}
