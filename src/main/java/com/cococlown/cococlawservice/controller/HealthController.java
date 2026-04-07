package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@Api(tags = "系统接口")
@RestController
@RequestMapping("/api")
public class HealthController {

    @ApiOperation("健康检查")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("timestamp", LocalDateTime.now());
        result.put("service", "coco-claw-api");
        result.put("version", "1.0.0");
        return Result.success(result);
    }

    @ApiOperation("API信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "COCO-CLAW API");
        result.put("version", "1.0.0");
        result.put("description", "COCO-CLAW技能服务平台API");
        result.put("documentation", "/swagger-ui.html");
        return Result.success(result);
    }
}
