package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cococlown.cococlawservice.common.Constants;
import com.cococlown.cococlawservice.dto.SkillDTO;
import com.cococlown.cococlawservice.dto.SkillQueryDTO;
import com.cococlown.cococlawservice.entity.Skill;
import com.cococlown.cococlawservice.mapper.SkillMapper;
import com.cococlown.cococlawservice.service.SkillService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SKILL服务实现类
 */
@Service
public class SkillServiceImpl implements SkillService {

    @Autowired
    private SkillMapper skillMapper;

    @Override
    public IPage<SkillDTO> getSkillPage(SkillQueryDTO query) {
        Page<SkillDTO> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SkillDTO> result = skillMapper.selectSkillPage(page, query);
        return result;
    }

    @Override
    public SkillDTO getSkillDetail(Long id) {
        SkillDTO dto = skillMapper.selectSkillDetailById(id);
        if (dto != null) {
            // 处理features字段，转换为列表
            if (StringUtils.isNotBlank(dto.getFeatures())) {
                // features可能是JSON格式或逗号分隔的字符串
                List<String> featureList = new ArrayList<>();
                String features = dto.getFeatures();
                if (features.startsWith("[")) {
                    // JSON格式
                    featureList = com.alibaba.fastjson2.JSON.parseArray(features, String.class);
                } else {
                    // 逗号分隔
                    for (String f : features.split(",")) {
                        if (StringUtils.isNotBlank(f.trim())) {
                            featureList.add(f.trim());
                        }
                    }
                }
                dto.setFeatures(String.join(",", featureList));
            }
        }
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createSkill(Skill skill) {
        // 设置默认值
        if (skill.getStatus() == null) {
            skill.setStatus(Constants.SkillStatus.ONLINE);
        }
        if (skill.getFeatured() == null) {
            skill.setFeatured(0);
        }
        if (skill.getRating() == null) {
            skill.setRating(java.math.BigDecimal.valueOf(5.0));
        }
        if (skill.getSales() == null) {
            skill.setSales(0);
        }
        if (skill.getLikes() == null) {
            skill.setLikes(0);
        }
        return skillMapper.insert(skill) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSkill(Skill skill) {
        return skillMapper.updateById(skill) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSkill(Long id) {
        return skillMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleStatus(Long id) {
        Skill skill = skillMapper.selectById(id);
        if (skill == null) {
            return false;
        }
        Integer currentStatus = skill.getStatus();
        Integer newStatus = (currentStatus == Constants.SkillStatus.ONLINE)
                ? Constants.SkillStatus.OFFLINE
                : Constants.SkillStatus.ONLINE;
        skill.setStatus(newStatus);
        return skillMapper.updateById(skill) > 0;
    }

    @Override
    public List<SkillDTO> getFeaturedSkills(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return skillMapper.selectFeaturedSkills(limit);
    }
}
