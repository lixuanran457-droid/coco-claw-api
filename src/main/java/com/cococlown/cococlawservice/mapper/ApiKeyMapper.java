package com.cococlown.cococlawservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cococlown.cococlawservice.entity.ApiKey;
import org.apache.ibatis.annotations.Mapper;

/**
 * API密钥Mapper
 */
@Mapper
public interface ApiKeyMapper extends BaseMapper<ApiKey> {
}
