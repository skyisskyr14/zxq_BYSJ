package com.sq.system.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminLog {

    /**
     * 操作模块名（如 用户管理 / 权限管理）
     */
    String module() default "未知模块";

    /**
     * 操作行为说明（如 添加用户 / 删除权限）
     */
    String action() default "未定义行为";
}
