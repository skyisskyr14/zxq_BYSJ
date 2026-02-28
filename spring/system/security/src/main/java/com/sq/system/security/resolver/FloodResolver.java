package com.sq.system.security.resolver;

import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.admincore.entity.log.FloodBlockLogEntity;
import com.sq.system.framework.redis.AdminTokenService;
import com.sq.system.framework.redis.FloodService;
import com.sq.system.framework.redis.UserTokenService;
import com.sq.system.usercore.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class FloodResolver {

    @Resource
    private AdminTokenService adminTokenService;

    @Resource
    private FloodService floodService;
    @Autowired
    private UserTokenService userTokenService;

    public String backKey(String token,String url,String ip) {
        String keyBase = "FLOOD";

        if(adminTokenService.exists(token)) {
            AdminUserEntity user = adminTokenService.getUserByToken(token);
            return keyBase + ":admin:" + user.getUsername() + ":" + url;
        } else if (userTokenService.exists(token)) {
            UserEntity user = userTokenService.getUserByToken(token);
            return keyBase + ":user:" + user.getUsername() + ":" + url;
        }else {
            return keyBase + ":ip:" + ip + ":" + url;
        }
    }

    public Long increment(String key,int EXPIRE_TIME) {
        return floodService.increment(key, EXPIRE_TIME);
    }

    public String backKey_log(String ip,String name,String url,String method) {
        String keyBase = "FLOOD:LOG:";
        return keyBase  + ip + ":" + name + ":" + url + ":" + method;
    }

    public boolean insertLog(String key, FloodBlockLogEntity log , int time){
        return floodService.insertLog(key, log, time);
    }

}
