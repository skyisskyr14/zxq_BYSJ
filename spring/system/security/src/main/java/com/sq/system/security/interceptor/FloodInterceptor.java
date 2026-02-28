package com.sq.system.security.interceptor;

import com.sq.system.admincore.entity.log.FloodBlockLogEntity;
import com.sq.system.common.utils.IpUtil;
import com.sq.system.security.config.FloodConfig;
import com.sq.system.security.resolver.FloodResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class FloodInterceptor implements HandlerInterceptor {

    @Resource
    private FloodResolver floodResolver;

    @Autowired
    private FloodConfig floodConfig;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        int MAX_REQUESTS = floodConfig.getMaxRequests();  // 最大请求次数
        int EXPIRE_TIME = floodConfig.getExpireTime();    // 统计窗口：1 秒

        // 优先从 Header 获取 token（App、小程序、前端自带）
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 若 Header 无 token，则从 Cookie 取（H5 浏览器自动带）
        if (token == null || token.isEmpty()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("token".equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }
        }
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        String key = floodResolver.backKey(token,uri,ip);

        Long count = floodResolver.increment(key,EXPIRE_TIME);
        if (count > MAX_REQUESTS) {
            String key_log;
            String[] parts = key.split(":", 4);
            FloodBlockLogEntity log = new FloodBlockLogEntity();
            if(Objects.equals(parts[1], "ip")){
                log.setIdentifier("IP");
                key_log = floodResolver.backKey_log(ip,null,request.getRequestURI(),request.getMethod());
            }else{
                log.setIdentifier(parts[1] + ":" + parts[2]);
                key_log = floodResolver.backKey_log(ip,parts[2], request.getRequestURI(),request.getMethod());
            }
            log.setIpAddress(IpUtil.getIp(request));
            log.setRequestUri(request.getRequestURI());
            log.setRequestMethod(request.getMethod());
            log.setRequestParam(null);
            log.setHitRule(MAX_REQUESTS + "次/" + EXPIRE_TIME + "秒");
            log.setActionTaken("拒绝");
            log.setRemark("触发洪水防御基础政策");
            log.setCreateTime(LocalDateTime.now());
            if (floodResolver.insertLog(key_log,log,10)){
                System.out.println("触发洪水防御，已记录");
            }else{
                System.out.println("持续触发洪水防御，10分钟内不在记录");
            }

            response.setStatus(429); // HTTP 429 Too Many Requests
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("访问频率过高，请稍后再试！");
            return false;
        }

        return true;
    }

    /**
     * 从 HttpOnly Cookie 中提取名为 token 的 token
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}