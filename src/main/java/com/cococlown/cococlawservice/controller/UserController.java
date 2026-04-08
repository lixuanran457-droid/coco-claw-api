package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.UserDTO;
import com.cococlown.cococlawservice.dto.UserUpdateDTO;
import com.cococlown.cococlawservice.entity.User;
import com.cococlown.cococlawservice.mapper.UserMapper;
import com.cococlown.cococlawservice.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 用户控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取当前用户信息
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<UserDTO> getCurrentUserInfo(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(convertToDTO(user));
    }

    /**
     * 根据ID获取用户
     */
    @ApiOperation("根据ID获取用户")
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(convertToDTO(user));
    }

    /**
     * 根据用户名获取用户
     */
    @ApiOperation("根据用户名获取用户")
    @GetMapping("/username/{username}")
    public Result<UserDTO> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(convertToDTO(user));
    }

    /**
     * 更新用户信息
     */
    @ApiOperation("更新用户信息")
    @PutMapping("/info")
    public Result<Boolean> updateUserInfo(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody UserUpdateDTO updateDTO) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 更新字段
        if (updateDTO.getNickname() != null) {
            user.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getGender() != null) {
            user.setGender(updateDTO.getGender());
        }
        if (updateDTO.getBirthday() != null) {
            user.setBirthday(updateDTO.getBirthday());
        }
        if (updateDTO.getBio() != null) {
            user.setBio(updateDTO.getBio());
        }

        int result = userMapper.updateById(user);
        return Result.success(result > 0);
    }

    /**
     * 修改密码
     */
    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<Boolean> changePassword(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!user.getPassword().equals(oldPassword)) {
            return Result.error(400, "原密码错误");
        }

        user.setPassword(newPassword);
        int result = userMapper.updateById(user);
        return Result.success(result > 0);
    }

    /**
     * 绑定手机号
     */
    @ApiOperation("绑定手机号")
    @PutMapping("/phone")
    public Result<Boolean> bindPhone(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam String phone,
            @RequestParam(required = false) String captcha) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        // TODO: 验证码校验
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        user.setPhone(phone);
        int result = userMapper.updateById(user);
        return Result.success(result > 0);
    }

    /**
     * 绑定邮箱
     */
    @ApiOperation("绑定邮箱")
    @PutMapping("/email")
    public Result<Boolean> bindEmail(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam String email,
            @RequestParam(required = false) String captcha) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        // TODO: 验证码校验
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        user.setEmail(email);
        int result = userMapper.updateById(user);
        return Result.success(result > 0);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        // 确保余额不为null
        if (dto.getBalance() == null) {
            dto.setBalance(BigDecimal.ZERO);
        }
        return dto;
    }
}
