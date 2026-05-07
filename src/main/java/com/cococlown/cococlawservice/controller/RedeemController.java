package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.entity.RedeemCode;
import com.cococlown.cococlawservice.service.RedeemCodeService;
import com.cococlown.cococlawservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 兑换码Controller
 */
@RestController
@RequestMapping("/api/redeem")
@RequiredArgsConstructor
@Tag(name = "兑换码")
public class RedeemController {

    private final RedeemCodeService redeemCodeService;

    @PostMapping("/use")
    @Operation(summary = "使用兑换码")
    public Result<Void> redeemCode(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        String code = body.get("code");
        if (code == null || code.isEmpty()) {
            return Result.error("请输入兑换码");
        }

        String result = redeemCodeService.redeemCode(userId, code);
        if (result.equals("兑换成功")) {
            return Result.success(null, result);
        } else {
            return Result.error(result);
        }
    }

    @GetMapping("/history")
    @Operation(summary = "获取兑换记录")
    public Result<List<RedeemCode>> getRedeemHistory(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        List<RedeemCode> history = redeemCodeService.getUserRedeemHistory(userId);
        return Result.success(history);
    }

    @GetMapping("/validate")
    @Operation(summary = "验证兑换码")
    public Result<Boolean> validateCode(@RequestParam String code) {
        boolean valid = redeemCodeService.validateCode(code);
        return Result.success(valid);
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
