package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.AuthResponseDTO;
import com.cococlown.cococlawservice.dto.LoginDTO;
import com.cococlown.cococlawservice.dto.RegisterDTO;
import com.cococlown.cococlawservice.entity.User;
import com.cococlown.cococlawservice.mapper.UserMapper;
import com.cococlown.cococlawservice.service.AuthService;
import com.cococlown.cococlawservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    @Override
    public AuthResponseDTO login(LoginDTO loginDTO) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername())
               .or()
               .eq(User::getPhone, loginDTO.getUsername())
               .or()
               .eq(User::getEmail, loginDTO.getUsername());
        
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码（实际项目中应使用BCrypt加密）
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 返回认证响应
        return buildAuthResponse(token, user);
    }

    @Override
    public AuthResponseDTO register(RegisterDTO registerDTO) {
        // 验证密码确认
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次密码输入不一致");
        }

        // 验证验证码
        if (registerDTO.getPhone() != null && !verifyCaptcha(registerDTO.getPhone(), registerDTO.getCaptchaCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, registerDTO.getUsername());
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (registerDTO.getPhone() != null) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, registerDTO.getPhone());
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new RuntimeException("手机号已被注册");
            }
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword()); // 实际项目应加密
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setStatus(1); // 启用状态

        userMapper.insert(user);

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return buildAuthResponse(token, user);
    }

    @Override
    public boolean sendCaptcha(String phone) {
        // 生成6位验证码
        String captcha = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        
        // 存入Redis，5分钟有效期
        String key = "captcha:" + phone;
        redisTemplate.opsForValue().set(key, captcha, 5, TimeUnit.MINUTES);

        // TODO: 实际项目中应调用短信服务发送验证码
        // 这里仅打印到日志，实际部署时请替换为真实短信发送
        System.out.println("【COCO CLAW】验证码: " + captcha + "，5分钟内有效");

        return true;
    }

    @Override
    public String refreshToken(String oldToken) {
        if (!jwtUtil.validateToken(oldToken)) {
            throw new RuntimeException("Token无效");
        }
        return jwtUtil.refreshToken(oldToken);
    }

    @Override
    public void logout(String token) {
        // 将Token加入黑名单
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 计算剩余过期时间
        Long ttl = (jwtUtil.getExpiration() - System.currentTimeMillis()) / 1000;
        if (ttl > 0) {
            String key = "token:blacklist:" + token;
            redisTemplate.opsForValue().set(key, "1", ttl, TimeUnit.SECONDS);
        }
    }

    /**
     * 验证验证码
     */
    private boolean verifyCaptcha(String phone, String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        String key = "captcha:" + phone;
        String cachedCaptcha = redisTemplate.opsForValue().get(key);
        return code.equals(cachedCaptcha);
    }

    /**
     * 构建认证响应
     */
    private AuthResponseDTO buildAuthResponse(String token, User user) {
        AuthResponseDTO response = new AuthResponseDTO();
        response.setAccessToken(token);
        response.setExpiresIn(expiration / 1000);
        
        AuthResponseDTO.UserInfoDTO userInfo = new AuthResponseDTO.UserInfoDTO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setStatus(user.getStatus());
        
        response.setUser(userInfo);
        
        return response;
    }
}
