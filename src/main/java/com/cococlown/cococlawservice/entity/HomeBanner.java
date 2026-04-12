package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 首页Banner实体
 */
@Data
@TableName("home_banner")
public class HomeBanner {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String imageUrl;
    
    private String linkUrl;
    
    private String linkType;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
