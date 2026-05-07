package com.cococlown.cococlawservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cococlown.cococlawservice.entity.UserBinding;
import org.apache.ibatis.annotations.Mapper;

/**
 * 第三方绑定Mapper
 */
@Mapper
public interface UserBindingMapper extends BaseMapper<UserBinding> {
}
