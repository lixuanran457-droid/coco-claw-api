package com.cococlown.cococlawservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cococlown.cococlawservice.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
