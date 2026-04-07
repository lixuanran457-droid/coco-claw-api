package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.entity.SysAdmin;

import java.util.List;

/**
 * 管理员服务接口
 */
public interface AdminService {

    /**
     * 获取所有管理员
     */
    List<SysAdmin> getAllAdmins();

    /**
     * 根据ID获取管理员
     */
    SysAdmin getAdminById(Long id);

    /**
     * 根据用户名获取管理员
     */
    SysAdmin getAdminByUsername(String username);

    /**
     * 创建管理员
     */
    boolean createAdmin(SysAdmin admin);

    /**
     * 更新管理员
     */
    boolean updateAdmin(SysAdmin admin);

    /**
     * 删除管理员
     */
    boolean deleteAdmin(Long id);

    /**
     * 重置密码
     */
    boolean resetPassword(Long id, String newPassword);

    /**
     * 更新状态
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 验证管理员登录
     */
    SysAdmin validateLogin(String username, String password);

    /**
     * 验证密码
     */
    boolean verifyPassword(Long id, String password);
}
