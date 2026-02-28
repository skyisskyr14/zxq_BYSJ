package com.sq.system.admincore.repository.log;

import com.sq.system.admincore.entity.log.FloodBlockLogEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FloodBlockLogRepository {

    @Insert("INSERT INTO flood_block_log (" +
            "identifier, ip_address, request_uri, request_method, " +
            "request_param, hit_rule, action_taken, remark, create_time" +
            ") VALUES (" +
            "#{identifier}, #{ipAddress}, #{requestUri}, #{requestMethod}, " +
            "#{requestParam}, #{hitRule}, #{actionTaken}, #{remark}, #{createTime}" +
            ")")
    void insert(FloodBlockLogEntity log);
}
