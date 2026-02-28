package com.sq.system.security.helper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sq.system.security.context.ProjectContextHolder;

/**
 * SQL 辅助工具，用于自动追加项目隔离条件
 */
public class SqlHelper {

    /**
     * 在查询条件中自动添加 project_id = 当前上下文 projectId
     */
    public static <T> void appendProjectCondition(QueryWrapper<T> wrapper) {
        Long projectId = ProjectContextHolder.get();
        if (projectId != null) {
            wrapper.eq("project_id", projectId);
        }
    }
}
