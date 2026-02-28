package com.sq.system.usercore.repository;

import com.sq.system.usercore.entity.UserOperationLogEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserOperationLogRepository {

    @Insert("INSERT INTO user_operation_log (" +
            "user_id, user_account, ip_address, request_uri, request_method, user_agent, " +
            "action_module, action_type, request_param, result_status, result_message, create_time" +
            ") VALUES (" +
            "#{userId}, #{userAccount}, #{ipAddress}, #{requestUri}, #{requestMethod}, #{userAgent}, " +
            "#{actionModule}, #{actionType}, #{requestParam}, #{resultStatus}, #{resultMessage}, #{createTime}" +
            ")")
    void insert(UserOperationLogEntity log);

}
