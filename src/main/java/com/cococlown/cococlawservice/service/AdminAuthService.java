package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.entity.SysAdmin;

/**
 * 管理员认证服务接口
 */
public interface AdminAuthService {

    /**
     * 管理员登录
     */
    SysAdmin login(String username, String password);

    /**
     * 验证管理员token
     */
    SysAdmin verifyToken(String token);

    /**
     * 生成管理员token
     */
    String generateToken(SysAdmin admin);

    /**
     * 登出
     */
    void logout(String token);
}
