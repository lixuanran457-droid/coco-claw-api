package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.dto.SkillDTO;
import com.cococlown.cococlawservice.entity.RecommendSkill;

import java.util.List;

/**
 * 推荐商品服务接口
 */
public interface RecommendService {

    /**
     * 获取所有推荐商品
     */
    List<RecommendSkill> getAllRecommendSkills();

    /**
     * 获取启用的推荐商品
     */
    List<SkillDTO> getActiveRecommendSkills();

    /**
     * 获取推荐商品（技能详情）
     */
    List<SkillDTO> getActiveRecommendSkillDetails();

    /**
     * 添加推荐商品
     */
    boolean addRecommendSkill(RecommendSkill recommendSkill);

    /**
     * 更新推荐商品
     */
    boolean updateRecommendSkill(RecommendSkill recommendSkill);

    /**
     * 删除推荐商品
     */
    boolean deleteRecommendSkill(Long id);

    /**
     * 更新排序
     */
    boolean updateSortOrder(Long id, Integer sortOrder);

    /**
     * 切换启用状态
     */
    boolean toggleActive(Long id);
}
