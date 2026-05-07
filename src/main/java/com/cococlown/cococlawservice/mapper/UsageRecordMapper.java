package com.cococlown.cococlawservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cococlown.cococlawservice.entity.UsageRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 使用记录Mapper
 */
@Mapper
public interface UsageRecordMapper extends BaseMapper<UsageRecord> {
}
