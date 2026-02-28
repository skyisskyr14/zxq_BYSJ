package com.sq.system.admincore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("system_project")
public class ProjectEntity {
    private Long id;
    private String projectCode;
    private String projectName;
    private String description;
    private LocalDateTime createTime;
}
