package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cococlown.cococlawservice.dto.SkillDTO;
import com.cococlown.cococlawservice.entity.RecommendSkill;
import com.cococlown.cococlawservice.mapper.RecommendSkillMapper;
import com.cococlown.cococlawservice.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 推荐商品服务实现类
 */
@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private RecommendSkillMapper recommendSkillMapper;

    @Override
    public List<RecommendSkill> getAllRecommendSkills() {
        return recommendSkillMapper.selectList(
            new LambdaQueryWrapper<RecommendSkill>()
                .orderByAsc(RecommendSkill::getSortOrder)
        );
    }

    @Override
    public List<SkillDTO> getActiveRecommendSkills() {
        return recommendSkillMapper.selectActiveRecommendSkills();
    }

    @Override
    public List<SkillDTO> getActiveRecommendSkillDetails() {
        return recommendSkillMapper.selectActiveRecommendSkills();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRecommendSkill(RecommendSkill recommendSkill) {
        if (recommendSkill.getSortOrder() == null) {
            // 获取最大排序值 + 1
            List<RecommendSkill> list = getAllRecommendSkills();
            int maxOrder = 0;
            for (RecommendSkill r : list) {
                if (r.getSortOrder() != null && r.getSortOrder() > maxOrder) {
                    maxOrder = r.getSortOrder();
                }
            }
            recommendSkill.setSortOrder(maxOrder + 1);
        }
        if (recommendSkill.getIsActive() == null) {
            recommendSkill.setIsActive(1); // 默认启用
        }
        return recommendSkillMapper.insert(recommendSkill) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRecommendSkill(RecommendSkill recommendSkill) {
        return recommendSkillMapper.updateById(recommendSkill) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRecommendSkill(Long id) {
        return recommendSkillMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSortOrder(Long id, Integer sortOrder) {
        RecommendSkill recommendSkill = recommendSkillMapper.selectById(id);
        if (recommendSkill == null) {
            return false;
        }
        recommendSkill.setSortOrder(sortOrder);
        return recommendSkillMapper.updateById(recommendSkill) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleActive(Long id) {
        RecommendSkill recommendSkill = recommendSkillMapper.selectById(id);
        if (recommendSkill == null) {
            return false;
        }
        Integer currentActive = recommendSkill.getIsActive();
        Integer newActive = (currentActive == 1) ? 0 : 1;
        recommendSkill.setIsActive(newActive);
        return recommendSkillMapper.updateById(recommendSkill) > 0;
    }
}
