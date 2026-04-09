package com.cococlown.cococlawservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.entity.Skill;
import com.cococlown.cococlawservice.entity.UserSkill;
import com.cococlown.cococlawservice.mapper.SkillMapper;
import com.cococlown.cococlawservice.mapper.UserSkillMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户技能控制器 - 用户查看和管理已购买的技能
 */
@Api(tags = "用户技能管理")
@RestController
@RequestMapping("/api/user/skill")
public class UserSkillController {

    @Autowired
    private UserSkillMapper userSkillMapper;

    @Autowired
    private SkillMapper skillMapper;

    /**
     * 获取用户的所有技能列表
     */
    @ApiOperation("获取用户技能列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getUserSkills(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(required = false) String email) {
        
        if (userId == null && (email == null || email.isEmpty())) {
            return Result.error("请登录或提供邮箱地址");
        }

        LambdaQueryWrapper<UserSkill> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(UserSkill::getUserId, userId);
        } else {
            wrapper.eq(UserSkill::getEmail, email);
        }
        wrapper.eq(UserSkill::getStatus, 1); // 只查询正常状态的
        wrapper.orderByDesc(UserSkill::getCreateTime);
        
        List<UserSkill> userSkills = userSkillMapper.selectList(wrapper);
        
        // 转换结果，补充技能详情
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserSkill us : userSkills) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", us.getId());
            item.put("skillId", us.getSkillId());
            item.put("skillName", us.getSkillName());
            item.put("usageCount", us.getUsageCount());
            item.put("maxUsageCount", us.getMaxUsageCount());
            item.put("unlimited", us.getMaxUsageCount() == null || us.getMaxUsageCount() == 0);
            item.put("remainUsage", us.getMaxUsageCount() == null || us.getMaxUsageCount() == 0 
                    ? -1 : us.getMaxUsageCount() - us.getUsageCount());
            item.put("expireTime", us.getExpireTime());
            item.put("isExpired", us.getExpireTime() != null && us.getExpireTime().isBefore(LocalDateTime.now()));
            item.put("createTime", us.getCreateTime());
            
            // 获取技能详情
            Skill skill = skillMapper.selectById(us.getSkillId());
            if (skill != null) {
                item.put("skillIcon", skill.getIcon());
                item.put("skillDescription", skill.getDescription());
                item.put("skillCategory", skill.getCategoryName());
                item.put("apiKey", us.getSkillApiKey()); // 返回API Key
            }
            
            result.add(item);
        }
        
        return Result.success(result);
    }

    /**
     * 获取单个技能详情
     */
    @ApiOperation("获取技能详情")
    @GetMapping("/{skillId}")
    public Result<Map<String, Object>> getUserSkillDetail(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long skillId,
            @RequestParam(required = false) String email) {
        
        LambdaQueryWrapper<UserSkill> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(UserSkill::getUserId, userId);
        } else if (email != null && !email.isEmpty()) {
            wrapper.eq(UserSkill::getEmail, email);
        } else {
            return Result.error("请登录或提供邮箱地址");
        }
        wrapper.eq(UserSkill::getSkillId, skillId);
        
        UserSkill userSkill = userSkillMapper.selectOne(wrapper);
        
        if (userSkill == null) {
            return Result.error("您未购买该技能");
        }
        
        // 检查过期
        if (userSkill.getExpireTime() != null && userSkill.getExpireTime().isBefore(LocalDateTime.now())) {
            return Result.error("该技能已过期");
        }
        
        // 检查使用次数
        if (userSkill.getMaxUsageCount() != null && userSkill.getMaxUsageCount() > 0 
                && userSkill.getUsageCount() >= userSkill.getMaxUsageCount()) {
            return Result.error("该技能使用次数已用完");
        }
        
        // 获取技能详情
        Skill skill = skillMapper.selectById(skillId);
        if (skill == null) {
            return Result.error("技能不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", userSkill.getId());
        result.put("skillId", skillId);
        result.put("skillName", userSkill.getSkillName());
        result.put("skillDescription", skill.getDescription());
        result.put("skillIcon", skill.getIcon());
        result.put("usageCount", userSkill.getUsageCount());
        result.put("maxUsageCount", userSkill.getMaxUsageCount());
        result.put("remainUsage", userSkill.getMaxUsageCount() == null || userSkill.getMaxUsageCount() == 0 
                ? -1 : userSkill.getMaxUsageCount() - userSkill.getUsageCount());
        result.put("expireTime", userSkill.getExpireTime());
        result.put("apiKey", userSkill.getSkillApiKey());
        result.put("apiEndpoint", skill.getApiEndpoint());
        result.put("apiDocumentation", skill.getDocumentationUrl());
        
        return Result.success(result);
    }

    /**
     * 使用技能（增加使用次数）
     */
    @ApiOperation("使用技能")
    @PostMapping("/use/{skillId}")
    public Result<Map<String, Object>> useSkill(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long skillId,
            @RequestParam(required = false) String email) {
        
        LambdaQueryWrapper<UserSkill> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(UserSkill::getUserId, userId);
        } else if (email != null && !email.isEmpty()) {
            wrapper.eq(UserSkill::getEmail, email);
        } else {
            return Result.error("请登录或提供邮箱地址");
        }
        wrapper.eq(UserSkill::getSkillId, skillId);
        
        UserSkill userSkill = userSkillMapper.selectOne(wrapper);
        
        if (userSkill == null) {
            return Result.error("您未购买该技能");
        }
        
        // 检查过期
        if (userSkill.getExpireTime() != null && userSkill.getExpireTime().isBefore(LocalDateTime.now())) {
            return Result.error("该技能已过期");
        }
        
        // 检查使用次数
        if (userSkill.getMaxUsageCount() != null && userSkill.getMaxUsageCount() > 0 
                && userSkill.getUsageCount() >= userSkill.getMaxUsageCount()) {
            return Result.error("该技能使用次数已用完");
        }
        
        // 增加使用次数
        userSkill.setUsageCount(userSkill.getUsageCount() + 1);
        userSkill.setUpdatedAt(LocalDateTime.now());
        userSkillMapper.updateById(userSkill);
        
        // 获取技能信息
        Skill skill = skillMapper.selectById(skillId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("skillName", userSkill.getSkillName());
        result.put("usageCount", userSkill.getUsageCount());
        result.put("remainUsage", userSkill.getMaxUsageCount() == null || userSkill.getMaxUsageCount() == 0 
                ? -1 : userSkill.getMaxUsageCount() - userSkill.getUsageCount();
        result.put("apiKey", userSkill.getSkillApiKey());
        result.put("apiEndpoint", skill != null ? skill.getApiEndpoint() : null);
        
        return Result.success("技能使用成功", result);
    }

    /**
     * 验证用户是否有权使用某技能
     */
    @ApiOperation("验证技能使用权")
    @GetMapping("/verify/{skillId}")
    public Result<Boolean> verifySkillAccess(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long skillId,
            @RequestParam(required = false) String email) {
        
        LambdaQueryWrapper<UserSkill> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(UserSkill::getUserId, userId);
        } else if (email != null && !email.isEmpty()) {
            wrapper.eq(UserSkill::getEmail, email);
        } else {
            return Result.success(false);
        }
        wrapper.eq(UserSkill::getSkillId, skillId);
        
        UserSkill userSkill = userSkillMapper.selectOne(wrapper);
        
        if (userSkill == null) {
            return Result.success(false);
        }
        
        // 检查是否过期
        if (userSkill.getExpireTime() != null && userSkill.getExpireTime().isBefore(LocalDateTime.now())) {
            return Result.success(false);
        }
        
        // 检查使用次数
        if (userSkill.getMaxUsageCount() != null && userSkill.getMaxUsageCount() > 0 
                && userSkill.getUsageCount() >= userSkill.getMaxUsageCount()) {
            return Result.success(false);
        }
        
        return Result.success(true);
    }
}
