package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.entity.SysAdmin;
import com.cococlown.cococlawservice.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@Api(tags = "管理员管理")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 获取所有管理员（后台管理）
     */
    @ApiOperation("获取所有管理员")
    @GetMapping("/list")
    public Result<List<SysAdmin>> getAllAdmins() {
        List<SysAdmin> admins = adminService.getAllAdmins();
        // 不返回密码
        for (SysAdmin admin : admins) {
            admin.setPassword(null);
        }
        return Result.success(admins);
    }

    /**
     * 获取管理员详情（后台管理）
     */
    @ApiOperation("获取管理员详情")
    @GetMapping("/{id}")
    public Result<SysAdmin> getAdminById(@PathVariable Long id) {
        SysAdmin admin = adminService.getAdminById(id);
        if (admin != null) {
            admin.setPassword(null); // 不返回密码
        }
        return Result.success(admin);
    }

    /**
     * 创建管理员（后台管理）
     */
    @ApiOperation("创建管理员")
    @PostMapping
    public Result<Boolean> createAdmin(@RequestBody SysAdmin admin) {
        boolean success = adminService.createAdmin(admin);
        if (success) {
            return Result.success("创建成功", true);
        }
        return Result.error("用户名已存在");
    }

    /**
     * 更新管理员（后台管理）
     */
    @ApiOperation("更新管理员")
    @PutMapping("/{id}")
    public Result<Boolean> updateAdmin(@PathVariable Long id, @RequestBody SysAdmin admin) {
        admin.setId(id);
        boolean success = adminService.updateAdmin(admin);
        if (success) {
            return Result.success("更新成功", true);
        }
        return Result.error("更新失败");
    }

    /**
     * 删除管理员（后台管理）
     */
    @ApiOperation("删除管理员")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAdmin(@PathVariable Long id) {
        boolean success = adminService.deleteAdmin(id);
        if (success) {
            return Result.success("删除成功", true);
        }
        return Result.error("删除失败");
    }

    /**
     * 重置密码（后台管理）
     */
    @ApiOperation("重置密码")
    @PostMapping("/{id}/reset-password")
    public Result<Boolean> resetPassword(@PathVariable Long id, @RequestParam(required = false) String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            newPassword = "admin123"; // 默认密码
        }
        boolean success = adminService.resetPassword(id, newPassword);
        if (success) {
            return Result.success("密码已重置为: " + newPassword, true);
        }
        return Result.error("重置失败");
    }

    /**
     * 更新管理员状态（后台管理）
     */
    @ApiOperation("更新管理员状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = adminService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功", true);
        }
        return Result.error("状态更新失败");
    }

    /**
     * 管理员登录
     */
    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        SysAdmin admin = adminService.validateLogin(username, password);
        if (admin != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", admin.getId());
            result.put("username", admin.getUsername());
            result.put("role", admin.getRole());
            result.put("status", admin.getStatus());
            return Result.success("登录成功", result);
        }
        return Result.error("用户名或密码错误");
    }

    /**
     * 验证密码
     */
    @ApiOperation("验证密码")
    @PostMapping("/verify-password")
    public Result<Boolean> verifyPassword(@RequestBody Map<String, Object> data) {
        Long id = Long.valueOf(data.get("id").toString());
        String password = data.get("password").toString();
        boolean valid = adminService.verifyPassword(id, password);
        if (valid) {
            return Result.success("验证通过", true);
        }
        return Result.error("密码错误");
    }
}
