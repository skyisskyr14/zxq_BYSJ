package com.sq.system.security.resolver;

import com.sq.system.admincore.entity.log.AdminOperationLogEntity;
import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.framework.redis.AdminIpAccessService;
import com.sq.system.framework.redis.AdminTokenService;
import com.sq.system.framework.redis.UserIpAccessService;
import com.sq.system.framework.redis.UserTokenService;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.entity.UserOperationLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class IpAccessResolver {

    @Resource
    private AdminTokenService adminTokenService;

    @Resource
    private AdminIpAccessService adminIpAccessService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private UserIpAccessService userIpAccessService;

    public boolean existByToken(String token) {
        return  adminTokenService.exists(token);
    }

    public AdminUserEntity findAdminBytoken(String token) {
        AdminUserEntity user = adminTokenService.getUserByToken(token);
        return user;
    }

    public UserEntity findUserBytoken(String token) {
        UserEntity user = userTokenService.getUserByToken(token);
        return user;
    }

    /**
     * 记录 IP 行为日志
     */
    public void recordAdminIpAccess(AdminOperationLogEntity log ,String token) {
        adminIpAccessService.recordAdminIpAccess(log,token);
    }

    public void recordUserIpAccess(UserOperationLogEntity log , String token) {
        userIpAccessService.recordUserIpAccess(log,token);
    }
}
