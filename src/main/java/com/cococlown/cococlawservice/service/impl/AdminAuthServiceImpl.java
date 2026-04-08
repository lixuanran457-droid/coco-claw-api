package com.cococlown.cococlawservice.service.impl;

import com.cococlown.cococlawservice.entity.SysAdmin;
import com.cococlown.cococlawservice.mapper.SysAdminMapper;
import com.cococlown.cococlawservice.service.AdminAuthService;
import com.cococlown.cococlawservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 管理员认证服务实现
 */
@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    @Autowired
    private SysAdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${jwt.admin-expiration:86400000}")
    private Long adminExpiration;

    @Override
    public SysAdmin login(String username, String password) {
        SysAdmin admin = adminMapper.selectByUsername(username);
        if (admin == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (admin.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        return admin;
    }

    @Override
    public SysAdmin verifyToken(String token) {
        if (token == null || !token.startsWith("Admin ")) {
            return null;
        }

        String actualToken = token.substring(6);

        // 检查是否在黑名单
        String blacklistKey = "admin:token:blacklist:" + actualToken;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
            return null;
        }

        try {
            Long adminId = jwtUtil.getAdminIdFromToken(actualToken);
            if (adminId == null) {
                return null;
            }

            SysAdmin admin = adminMapper.selectById(adminId);
            if (admin == null || admin.getStatus() == 0) {
                return null;
            }

            return admin;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String generateToken(SysAdmin admin) {
        return jwtUtil.generateAdminToken(admin.getId(), admin.getUsername());
    }

    @Override
    public void logout(String token) {
        if (token != null && token.startsWith("Admin ")) {
            token = token.substring(6);

            // 将token加入黑名单
            String blacklistKey = "admin:token:blacklist:" + token;
            redisTemplate.opsForValue().set(blacklistKey, "1", adminExpiration / 1000, TimeUnit.SECONDS);
        }
    }
}
