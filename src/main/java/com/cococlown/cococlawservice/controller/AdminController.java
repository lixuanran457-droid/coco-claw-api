package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.entity.SysAdmin;
import com.cococlown.cococlawservice.service.AdminAuthService;
import com.cococlown.cococlawservice.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private AdminAuthService adminAuthService;

    /**
     * 管理员登录
     */
    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @RequestBody Map<String, String> loginData,
            HttpServletResponse response) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            SysAdmin admin = adminAuthService.login(username, password);
            String token = adminAuthService.generateToken(admin);

            // 设置httpOnly Cookie
            Cookie cookie = new Cookie("admin_token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(86400); // 24小时
            response.addCookie(cookie);

            // 返回用户信息（不包含密码）
            admin.setPassword(null);

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", admin);

            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员登出
     */
    @ApiOperation("管理员登出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("admin_token".equals(cookie.getName())) {
                    adminAuthService.logout(cookie.getValue());

                    // 清除Cookie
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        return Result.success(null);
    }

    /**
     * 获取当前管理员信息
     */
    @ApiOperation("获取当前管理员信息")
    @GetMapping("/me")
    public Result<SysAdmin> getCurrentAdmin(HttpServletRequest request) {
        String token = getTokenFromCookie(request);
        if (token == null) {
            return Result.error(401, "请先登录");
        }

        SysAdmin admin = adminAuthService.verifyToken("Admin " + token);
        if (admin == null) {
            return Result.error(401, "登录已过期");
        }

        admin.setPassword(null);
        return Result.success(admin);
    }

    /**
     * 获取所有管理员
     */
    @ApiOperation("获取所有管理员")
    @GetMapping("/list")
    public Result<List<SysAdmin>> getAllAdmins() {
        List<SysAdmin> admins = adminService.getAllAdmins();
        for (SysAdmin admin : admins) {
            admin.setPassword(null);
        }
        return Result.success(admins);
    }

    /**
     * 获取管理员详情
     */
    @ApiOperation("获取管理员详情")
    @GetMapping("/{id}")
    public Result<SysAdmin> getAdminById(@PathVariable Long id) {
        SysAdmin admin = adminService.getAdminById(id);
        if (admin != null) {
            admin.setPassword(null);
        }
        return Result.success(admin);
    }

    /**
     * 创建管理员
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
     * 更新管理员
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
     * 删除管理员
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
     * 重置密码
     */
    @ApiOperation("重置密码")
    @PostMapping("/{id}/reset-password")
    public Result<Boolean> resetPassword(@PathVariable Long id, @RequestParam(required = false) String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            newPassword = "admin123";
        }
        boolean success = adminService.resetPassword(id, newPassword);
        if (success) {
            return Result.success("密码已重置为: " + newPassword, true);
        }
        return Result.error("重置失败");
    }

    /**
     * 更新管理员状态
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

    /**
     * 从Cookie获取Token
     */
    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("admin_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
