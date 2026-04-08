package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cococlown.cococlawservice.dto.*;
import com.cococlown.cococlawservice.entity.User;
import com.cococlown.cococlawservice.mapper.UserMapper;
import com.cococlown.cococlawservice.service.AuthService;
import com.cococlown.cococlawservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;
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

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    @Override
    public AuthResponseDTO login(LoginDTO loginDTO) {
        // 校验邮箱
        if (loginDTO.getEmail() == null || loginDTO.getEmail().isEmpty()) {
            throw new RuntimeException("邮箱不能为空");
        }

        // 查询用户
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getEmail, loginDTO.getEmail())
        );

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证方式二选一：密码 或 验证码
        if (loginDTO.getCaptchaCode() != null && !loginDTO.getCaptchaCode().isEmpty()) {
            // 验证码登录
            if (!verifyCaptcha(loginDTO.getEmail(), loginDTO.getCaptchaCode())) {
                throw new RuntimeException("验证码错误或已过期");
            }
        } else if (loginDTO.getPassword() != null && !loginDTO.getPassword().isEmpty()) {
            // 密码登录
            if (!user.getPassword().equals(loginDTO.getPassword())) {
                throw new RuntimeException("密码错误");
            }
        } else {
            throw new RuntimeException("请提供密码或验证码");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        return buildAuthResponse(token, user);
    }

    @Override
    public AuthResponseDTO register(RegisterDTO registerDTO) {
        // 校验必填项
        if (registerDTO.getEmail() == null || registerDTO.getEmail().isEmpty()) {
            throw new RuntimeException("邮箱不能为空");
        }
        if (registerDTO.getPassword() == null || registerDTO.getPassword().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        if (registerDTO.getConfirmPassword() == null || registerDTO.getConfirmPassword().isEmpty()) {
            throw new RuntimeException("确认密码不能为空");
        }

        // 验证密码确认
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次密码输入不一致");
        }

        // 密码强度校验（8-20位）
        String password = registerDTO.getPassword();
        if (password.length() < 8 || password.length() > 20) {
            throw new RuntimeException("密码长度必须在8-20位之间");
        }

        // 验证邮箱验证码
        if (registerDTO.getCaptchaCode() == null || registerDTO.getCaptchaCode().isEmpty()) {
            throw new RuntimeException("验证码不能为空");
        }
        if (!verifyCaptcha(registerDTO.getEmail(), registerDTO.getCaptchaCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 检查邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, registerDTO.getEmail());
        if (userMapper.selectCount(emailWrapper) > 0) {
            throw new RuntimeException("该邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : "用户" + System.currentTimeMillis() % 10000);
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        user.setStatus(1); // 启用状态

        userMapper.insert(user);

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        return buildAuthResponse(token, user);
    }

    @Override
    public boolean sendCaptcha(String email) {
        // 生成6位验证码
        String captcha = String.format("%06d", (int) ((Math.random() * 9 + 1) * 100000));

        // 存入Redis，5分钟有效期
        String key = "captcha:" + email;
        redisTemplate.opsForValue().set(key, captcha, 5, TimeUnit.MINUTES);

        // TODO: 实际项目中应调用邮件服务发送验证码
        // 这里仅打印到日志，实际部署时请替换为真实邮件发送
        System.out.println("【COCO CLAW】邮箱验证码: " + captcha + "，5分钟内有效，发送至: " + email);

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
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long ttl = (jwtUtil.getExpiration() - System.currentTimeMillis()) / 1000;
        if (ttl > 0) {
            String key = "token:blacklist:" + token;
            redisTemplate.opsForValue().set(key, "1", ttl, TimeUnit.SECONDS);
        }
    }

    @Override
    public void sendResetPasswordEmail(String email) {
        // 检查邮箱是否存在
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getEmail, email)
        );

        if (user == null) {
            // 为防止枚举攻击，不提示用户邮箱不存在
            System.out.println("【COCO CLAW】密码重置请求: 邮箱不存在 - " + email);
            return;
        }

        // 生成重置Token（32位UUID）
        String resetToken = UUID.randomUUID().toString().replace("-", "");

        // 存入Redis，30分钟有效期
        String key = "reset_password:" + resetToken;
        Map<String, String> data = Map.of(
            "userId", String.valueOf(user.getId()),
            "email", email
        );
        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);

        // TODO: 实际项目中应调用邮件服务发送重置链接
        String resetUrl = baseUrl + "/reset-password?token=" + resetToken;
        System.out.println("【COCO CLAW】密码重置链接: " + resetUrl + "，30分钟内有效，发送至: " + email);
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {
        // 校验Token
        if (dto.getToken() == null || dto.getToken().isEmpty()) {
            throw new RuntimeException("重置Token不能为空");
        }

        // 校验密码
        if (dto.getNewPassword() == null || dto.getNewPassword().isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }
        if (dto.getNewPassword().length() < 8 || dto.getNewPassword().length() > 20) {
            throw new RuntimeException("密码长度必须在8-20位之间");
        }

        // 确认密码
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("两次密码输入不一致");
        }

        // 验证Token
        String key = "reset_password:" + dto.getToken();
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

        if (data.isEmpty()) {
            throw new RuntimeException("重置链接已过期，请重新申请");
        }

        String userIdStr = (String) data.get("userId");
        String email = (String) data.get("email");

        if (userIdStr == null) {
            throw new RuntimeException("重置链接无效");
        }

        Long userId = Long.parseLong(userIdStr);

        // 更新密码
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(dto.getNewPassword());
        userMapper.updateById(user);

        // 使Token失效
        redisTemplate.delete(key);

        System.out.println("【COCO CLAW】密码重置成功，用户ID: " + userId);
    }

    @Override
    public boolean verifyResetToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        String key = "reset_password:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 验证验证码
     */
    private boolean verifyCaptcha(String email, String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        String key = "captcha:" + email;
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
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setStatus(user.getStatus());

        response.setUser(userInfo);

        return response;
    }
}
