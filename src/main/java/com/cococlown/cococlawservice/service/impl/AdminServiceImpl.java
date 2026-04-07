package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cococlown.cococlawservice.entity.SysAdmin;
import com.cococlown.cococlawservice.mapper.SysAdminMapper;
import com.cococlown.cococlawservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private SysAdminMapper adminMapper;

    // 默认管理员密码
    private static final String DEFAULT_PASSWORD = "admin123";

    @Override
    public List<SysAdmin> getAllAdmins() {
        return adminMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public SysAdmin getAdminById(Long id) {
        return adminMapper.selectById(id);
    }

    @Override
    public SysAdmin getAdminByUsername(String username) {
        return adminMapper.selectOne(
            new LambdaQueryWrapper<SysAdmin>().eq(SysAdmin::getUsername, username)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createAdmin(SysAdmin admin) {
        // 检查用户名是否已存在
        SysAdmin existing = getAdminByUsername(admin.getUsername());
        if (existing != null) {
            return false;
        }
        
        // 设置默认密码
        if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
            admin.setPassword(encryptPassword(DEFAULT_PASSWORD));
        } else {
            admin.setPassword(encryptPassword(admin.getPassword()));
        }
        
        // 设置默认状态
        if (admin.getStatus() == null) {
            admin.setStatus(1); // 启用
        }
        
        // 设置默认角色
        if (admin.getRole() == null) {
            admin.setRole("admin");
        }
        
        return adminMapper.insert(admin) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAdmin(SysAdmin admin) {
        return adminMapper.updateById(admin) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAdmin(Long id) {
        return adminMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id, String newPassword) {
        SysAdmin admin = adminMapper.selectById(id);
        if (admin == null) {
            return false;
        }
        admin.setPassword(encryptPassword(newPassword));
        return adminMapper.updateById(admin) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        SysAdmin admin = adminMapper.selectById(id);
        if (admin == null) {
            return false;
        }
        admin.setStatus(status);
        return adminMapper.updateById(admin) > 0;
    }

    @Override
    public SysAdmin validateLogin(String username, String password) {
        SysAdmin admin = getAdminByUsername(username);
        if (admin == null) {
            return null;
        }
        if (admin.getStatus() != 1) {
            return null; // 账号被禁用
        }
        String encryptedPwd = encryptPassword(password);
        if (encryptedPwd.equals(admin.getPassword())) {
            return admin;
        }
        return null;
    }

    @Override
    public boolean verifyPassword(Long id, String password) {
        SysAdmin admin = adminMapper.selectById(id);
        if (admin == null) {
            return false;
        }
        String encryptedPwd = encryptPassword(password);
        return encryptedPwd.equals(admin.getPassword());
    }

    /**
     * 密码加密
     */
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(
            (password + "_coco_claw_salt").getBytes(StandardCharsets.UTF_8)
        );
    }
}
