package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);

    /**
     * 根据用户名获取用户
     */
    User getUserByUsername(String username);
}
