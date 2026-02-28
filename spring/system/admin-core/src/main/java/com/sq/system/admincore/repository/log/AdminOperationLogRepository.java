package com.sq.system.admincore.repository.log;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.sq.system.admincore.entity.log.AdminOperationLogEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("system")
public interface AdminOperationLogRepository {

    @Insert("INSERT INTO admin_operation_log (" +
            "admin_id, admin_account, ip_address, request_uri, request_method, user_agent, " +
            "action_module, action_type, request_param, result_status, result_message, create_time" +
            ") VALUES (" +
            "#{adminId}, #{adminAccount}, #{ipAddress}, #{requestUri}, #{requestMethod}, #{userAgent}, " +
            "#{actionModule}, #{actionType}, #{requestParam}, #{resultStatus}, #{resultMessage}, #{createTime}" +
            ")")
    void insert(AdminOperationLogEntity log);

}
