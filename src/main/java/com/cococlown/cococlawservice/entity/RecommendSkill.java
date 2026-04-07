package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 推荐商品实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("recommend_skill")
public class RecommendSkill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 推荐ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 技能ID
     */
    private Long skillId;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 是否启用: 0-禁用, 1-启用
     */
    private Integer isActive;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
