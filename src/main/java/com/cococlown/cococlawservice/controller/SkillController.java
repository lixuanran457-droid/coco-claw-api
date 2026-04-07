package com.cococlown.cococlawservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.SkillDTO;
import com.cococlown.cococlawservice.dto.SkillQueryDTO;
import com.cococlown.cococlawservice.entity.Skill;
import com.cococlown.cococlawservice.service.SkillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SKILL控制器
 */
@Api(tags = "SKILL管理")
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    /**
     * 获取SKILL列表（支持分页、分类筛选、搜索）
     */
    @ApiOperation("获取SKILL列表")
    @GetMapping
    public Result<IPage<SkillDTO>> getSkillList(SkillQueryDTO query) {
        IPage<SkillDTO> page = skillService.getSkillPage(query);
        return Result.success(page);
    }

    /**
     * 获取SKILL详情
     */
    @ApiOperation("获取SKILL详情")
    @GetMapping("/{id}")
    public Result<SkillDTO> getSkillDetail(@PathVariable Long id) {
        SkillDTO skill = skillService.getSkillDetail(id);
        if (skill == null) {
            return Result.error("SKILL不存在");
        }
        return Result.success(skill);
    }

    /**
     * 创建SKILL（后台）
     */
    @ApiOperation("创建SKILL")
    @PostMapping
    public Result<Boolean> createSkill(@RequestBody Skill skill) {
        boolean success = skillService.createSkill(skill);
        if (success) {
            return Result.success("创建成功", true);
        }
        return Result.error("创建失败");
    }

    /**
     * 更新SKILL（后台）
     */
    @ApiOperation("更新SKILL")
    @PutMapping("/{id}")
    public Result<Boolean> updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        skill.setId(id);
        boolean success = skillService.updateSkill(skill);
        if (success) {
            return Result.success("更新成功", true);
        }
        return Result.error("更新失败");
    }

    /**
     * 删除SKILL（后台）
     */
    @ApiOperation("删除SKILL")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteSkill(@PathVariable Long id) {
        boolean success = skillService.deleteSkill(id);
        if (success) {
            return Result.success("删除成功", true);
        }
        return Result.error("删除失败");
    }

    /**
     * 切换上下架状态
     */
    @ApiOperation("切换上下架状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> toggleStatus(@PathVariable Long id) {
        boolean success = skillService.toggleStatus(id);
        if (success) {
            return Result.success("状态切换成功", true);
        }
        return Result.error("状态切换失败");
    }

    /**
     * 获取精选SKILL
     */
    @ApiOperation("获取精选SKILL")
    @GetMapping("/featured")
    public Result<List<SkillDTO>> getFeaturedSkills(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<SkillDTO> featuredSkills = skillService.getFeaturedSkills(limit);
        return Result.success(featuredSkills);
    }
}
