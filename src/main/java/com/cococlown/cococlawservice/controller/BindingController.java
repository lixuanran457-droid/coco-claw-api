package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.entity.UserBinding;
import com.cococlown.cococlawservice.service.UserBindingService;
import com.cococlown.cococlawservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 第三方绑定Controller
 */
@RestController
@RequestMapping("/api/binding")
@RequiredArgsConstructor
@Tag(name = "第三方绑定")
public class BindingController {

    private final UserBindingService userBindingService;

    @GetMapping("/list")
    @Operation(summary = "获取绑定列表")
    public Result<List<UserBinding>> getBindings(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        List<UserBinding> bindings = userBindingService.getUserBindings(userId);
        return Result.success(bindings);
    }

    @DeleteMapping("/{provider}")
    @Operation(summary = "解除绑定")
    public Result<Void> unbind(
            @PathVariable String provider,
            HttpServletRequest request) {

        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        boolean success = userBindingService.unbindAccount(userId, provider);
        if (success) {
            return Result.success(null, "解除绑定成功");
        } else {
            return Result.error("解除绑定失败");
        }
    }

    @GetMapping("/check/{provider}")
    @Operation(summary = "检查绑定状态")
    public Result<Boolean> checkBinding(
            @PathVariable String provider,
            HttpServletRequest request) {

        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        boolean bound = userBindingService.isBound(userId, provider);
        return Result.success(bound);
    }

    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return JwtUtil.getUserId(token);
        }
        return null;
    }
}
