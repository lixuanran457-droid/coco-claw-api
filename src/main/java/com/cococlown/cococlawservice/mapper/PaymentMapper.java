package com.cococlown.cococlawservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cococlown.cococlawservice.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付记录Mapper
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
