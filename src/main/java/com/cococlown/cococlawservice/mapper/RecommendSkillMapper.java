package com.cococlown.cococlawservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cococlown.cococlawservice.dto.SkillDTO;
import com.cococlown.cococlawservice.entity.RecommendSkill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 推荐商品 Mapper接口
 */
@Mapper
public interface RecommendSkillMapper extends BaseMapper<RecommendSkill> {

    /**
     * 获取启用的推荐商品列表
     */
    List<SkillDTO> selectActiveRecommendSkills();

    /**
     * 获取指定数量的推荐商品
     */
    List<SkillDTO> selectRecommendSkillsByLimit(@Param("limit") Integer limit);
}
