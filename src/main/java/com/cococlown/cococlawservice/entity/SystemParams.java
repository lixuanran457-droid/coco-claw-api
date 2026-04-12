package com.cococlown.cococlawservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统参数实体
 */
@Data
@TableName("system_params")
public class SystemParams {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String paramKey;
    
    private String paramValue;
    
    private String paramType;
    
    private String description;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
