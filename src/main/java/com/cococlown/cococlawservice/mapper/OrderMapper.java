package com.cococlown.cococlawservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cococlown.cococlawservice.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
