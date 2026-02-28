package com.sq.system.admincore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("admin_to_project")
public class AdminProjectEntity {
    private Long adminId;
    private Long projectId;
}
