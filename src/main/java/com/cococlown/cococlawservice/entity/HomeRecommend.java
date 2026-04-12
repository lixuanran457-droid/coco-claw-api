package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 首页精选推荐实体
 */
@Data
@TableName("home_recommend")
public class HomeRecommend {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String itemType;
    
    private Long itemId;
    
    private String subtitle;
    
    private String icon;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
