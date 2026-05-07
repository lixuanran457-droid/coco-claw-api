package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.entity.UsageRecord;
import com.cococlown.cococlawservice.service.UsageRecordService;
import com.cococlown.cococlawservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 使用记录Controller
 */
@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
@Tag(name = "使用记录")
public class UsageController {

    private final UsageRecordService usageRecordService;

    @GetMapping("/records")
    @Operation(summary = "获取使用记录")
    public Result<List<UsageRecord>> getUsageRecords(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {

        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        List<UsageRecord> records = usageRecordService.getUserUsageRecords(
            userId, startDate, endDate, page, pageSize);

        return Result.success(records);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取使用统计")
    public Result<Map<String, Object>> getUsageStats(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        Map<String, Object> stats = usageRecordService.getUserUsageStats(userId);
        return Result.success(stats);
    }

    @GetMapping("/today")
    @Operation(summary = "获取今日使用量")
    public Result<Integer> getTodayUsage(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.unauthorized();
        }

        Integer todayUsage = usageRecordService.getTodayUsage(userId);
        return Result.success(todayUsage);
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
