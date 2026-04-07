package com.cococlown.cococlawservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cococlown.cococlawservice.dto.SkillDTO;
import com.cococlown.cococlawservice.dto.SkillQueryDTO;
import com.cococlown.cococlawservice.entity.Skill;

import java.util.List;

/**
 * SKILL服务接口
 */
public interface SkillService {

    /**
     * 分页查询SKILL列表
     */
    IPage<SkillDTO> getSkillPage(SkillQueryDTO query);

    /**
     * 获取SKILL详情
     */
    SkillDTO getSkillDetail(Long id);

    /**
     * 创建SKILL
     */
    boolean createSkill(Skill skill);

    /**
     * 更新SKILL
     */
    boolean updateSkill(Skill skill);

    /**
     * 删除SKILL
     */
    boolean deleteSkill(Long id);

    /**
     * 切换SKILL状态
     */
    boolean toggleStatus(Long id);

    /**
     * 获取精选SKILL
     */
    List<SkillDTO> getFeaturedSkills(Integer limit);
}
