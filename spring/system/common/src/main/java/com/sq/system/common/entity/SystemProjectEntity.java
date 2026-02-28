package com.sq.system.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("system_project")
public class SystemProjectEntity {
    @Schema(description = "键值ID")
    private String id;

    @Schema(description = "键值ID")
    private String projectCode;

    @Schema(description = "键值ID")
    private String projectName;

    @Schema(description = "键值ID")
    private String description;

    @Schema(description = "键值ID")
    private LocalDateTime createTime;

    @Schema(description = "键值ID")
    private LocalDateTime updateTime;
}
