package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.SkillDTO;
import com.cococlown.cococlawservice.entity.RecommendSkill;
import com.cococlown.cococlawservice.service.RecommendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐商品控制器
 */
@Api(tags = "推荐商品管理")
@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取推荐商品列表（C端）
     */
    @ApiOperation("获取推荐商品列表")
    @GetMapping("/list")
    public Result<List<SkillDTO>> getRecommendList() {
        List<SkillDTO> recommendSkills = recommendService.getActiveRecommendSkillDetails();
        return Result.success(recommendSkills);
    }

    /**
     * 获取所有推荐商品（后台管理）
     */
    @ApiOperation("获取所有推荐商品")
    @GetMapping("/admin/list")
    public Result<List<RecommendSkill>> getAllRecommendSkills() {
        List<RecommendSkill> recommendSkills = recommendService.getAllRecommendSkills();
        return Result.success(recommendSkills);
    }

    /**
     * 添加推荐商品（后台管理）
     */
    @ApiOperation("添加推荐商品")
    @PostMapping
    public Result<Boolean> addRecommendSkill(@RequestBody RecommendSkill recommendSkill) {
        boolean success = recommendService.addRecommendSkill(recommendSkill);
        if (success) {
            return Result.success("添加成功", true);
        }
        return Result.error("添加失败");
    }

    /**
     * 更新推荐商品（后台管理）
     */
    @ApiOperation("更新推荐商品")
    @PutMapping("/{id}")
    public Result<Boolean> updateRecommendSkill(@PathVariable Long id, @RequestBody RecommendSkill recommendSkill) {
        recommendSkill.setId(id);
        boolean success = recommendService.updateRecommendSkill(recommendSkill);
        if (success) {
            return Result.success("更新成功", true);
        }
        return Result.error("更新失败");
    }

    /**
     * 删除推荐商品（后台管理）
     */
    @ApiOperation("删除推荐商品")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteRecommendSkill(@PathVariable Long id) {
        boolean success = recommendService.deleteRecommendSkill(id);
        if (success) {
            return Result.success("删除成功", true);
        }
        return Result.error("删除失败");
    }

    /**
     * 更新排序（后台管理）
     */
    @ApiOperation("更新排序")
    @PutMapping("/{id}/sort")
    public Result<Boolean> updateSortOrder(@PathVariable Long id, @RequestParam Integer sortOrder) {
        boolean success = recommendService.updateSortOrder(id, sortOrder);
        if (success) {
            return Result.success("排序更新成功", true);
        }
        return Result.error("排序更新失败");
    }

    /**
     * 切换启用状态（后台管理）
     */
    @ApiOperation("切换启用状态")
    @PutMapping("/{id}/toggle")
    public Result<Boolean> toggleActive(@PathVariable Long id) {
        boolean success = recommendService.toggleActive(id);
        if (success) {
            return Result.success("状态切换成功", true);
        }
        return Result.error("状态切换失败");
    }
}
