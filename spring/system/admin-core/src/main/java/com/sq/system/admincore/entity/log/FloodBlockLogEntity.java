package com.sq.system.admincore.entity.log;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 洪水攻击拦截日志表
 * 用于记录请求频繁、命中限流策略的用户/管理员/IP行为
 */
@Data
@TableName("flood_block_log")
public class FloodBlockLogEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 请求标识符（如 ip:127.0.0.1 / admin:zhangsan） */
    private String identifier;

    /** 请求来源 IP 地址 */
    private String ipAddress;

    /** 请求 URI 路径 */
    private String requestUri;

    /** 请求方法（GET / POST） */
    private String requestMethod;

    /** 请求参数 JSON 快照 */
    private String requestParam;

    /** 命中的限流规则（如 每秒10次） */
    private String hitRule;

    /** 系统对该请求采取的动作（拦截 / 封禁 / 拒绝） */
    private String actionTaken;

    /** 备注信息（来源模块 / 拦截器名等） */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;
}
