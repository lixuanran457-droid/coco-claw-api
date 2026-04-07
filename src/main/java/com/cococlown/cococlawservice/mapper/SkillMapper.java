package com.cococlown.cococlawservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cococlown.cococlawservice.dto.SkillDTO;
import com.cococlown.cococlawservice.dto.SkillQueryDTO;
import com.cococlown.cococlawservice.entity.Skill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SKILL Mapper接口
 */
@Mapper
public interface SkillMapper extends BaseMapper<Skill> {

    /**
     * 分页查询SKILL
     */
    IPage<SkillDTO> selectSkillPage(Page<?> page, @Param("query") SkillQueryDTO query);

    /**
     * 查询精选SKILL
     */
    List<SkillDTO> selectFeaturedSkills(@Param("limit") Integer limit);

    /**
     * 根据ID查询SKILL详情
     */
    SkillDTO selectSkillDetailById(@Param("id") Long id);
}
