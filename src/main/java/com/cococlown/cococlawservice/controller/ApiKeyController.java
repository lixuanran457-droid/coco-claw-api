package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.entity.ApiKey;
import com.cococlown.cococlawservice.service.ApiKeyService;
import com.cococlown.cococlawservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API密钥管理Controller
 */
@RestController
@RequestMapping("/api/apikey")
@RequiredArgsConstructor
@Tag(name = "API密钥管理")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping("/create")
    @Operation(summary = "创建API密钥")
    public Result<Map<String, String>> createApiKey(
            @RequestParam String name,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String note,
            HttpServletRequest request) {

        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        String fullKey = apiKeyService.createApiKey(userId, name, groupName, note);

        Map<String, String> result = new HashMap<>();
        result.put("apiKey", fullKey);
        result.put("name", name);

        return Result.success(result, "API密钥创建成功，请妥善保管！");
    }

    @GetMapping("/list")
    @Operation(summary = "获取API密钥列表")
    public Result<List<ApiKey>> getApiKeyList(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        List<ApiKey> list = apiKeyService.getUserApiKeys(userId);
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除API密钥")
    public Result<Void> deleteApiKey(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        boolean success = apiKeyService.deleteApiKey(userId, id);
        if (success) {
            return Result.success(null, "删除成功");
        } else {
            return Result.error("删除失败，密钥不存在");
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "切换API密钥状态")
    public Result<Void> toggleStatus(
            @PathVariable Long id,
            @RequestParam Integer status,
            HttpServletRequest request) {

        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        boolean success = apiKeyService.toggleApiKeyStatus(userId, id, status);
        if (success) {
            return Result.success(null, "状态更新成功");
        } else {
            return Result.error("更新失败");
        }
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
