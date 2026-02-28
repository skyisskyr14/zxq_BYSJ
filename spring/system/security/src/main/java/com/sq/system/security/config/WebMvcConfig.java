package com.sq.system.security.config;

import com.sq.system.security.interceptor.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

//token限制配置
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private TokenInterceptor tokenInterceptor;

    @Bean
    public PermissionInterceptor permissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Resource
    private FloodInterceptor floodInterceptor;

    @Resource
    private IpAccessInterceptor ipaccessInterceptor;

    @Resource
    private ProjectContextInterceptor projectContextInterceptor;

    @Resource
    private MaintenanceTimeInterceptor maintenanceTimeInterceptor;

    @Value("${app.kf.base-dir}")
    private String baseDir;
    @Value("${app.recharge.base-dir}")
    private String baseDir1;
    @Value("${app.pay.base-dir}")
    private String baseDir2;
    @Value("${app.user.avatar.base-dir}")
    private String baseDir3;

    @PostConstruct
    public void debugStaticDirs() {
        System.out.println("==== [DEBUG] Static Resource Roots ====");
        System.out.println("kf.baseDir      = " + baseDir);
        System.out.println("recharge.baseDir= " + baseDir1);
        System.out.println("pay.baseDir2    = " + baseDir2);
        System.out.println("pay.location2   = " + Paths.get(baseDir2).toUri());
        System.out.println("user.avatarDir  = " + baseDir3);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = toDirLocation(baseDir);
        registry.addResourceHandler("/uploads/kf/**").addResourceLocations(location);
        String location1 = toDirLocation(baseDir1);
        registry.addResourceHandler("/uploads/recharge/**").addResourceLocations(location1);
        String location2 = toDirLocation(baseDir2);
        registry.addResourceHandler("/uploads/pay/**").addResourceLocations(location2);
        String location3 = toDirLocation(baseDir3);
        registry.addResourceHandler("/uploads/user/avatar/**").addResourceLocations(location3);
    }

    private String toDirLocation(String dir) {
        String location = Paths.get(dir).toUri().toString();
        return location.endsWith("/") ? location : location + "/";
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 先加“时间维护”拦截器（放前面一点）
        registry.addInterceptor(maintenanceTimeInterceptor)
                .addPathPatterns("/**");  // 这里先对所有路径生效，内部再按 BLOCK_PREFIXES 过滤

        //token校验政策
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**") // 全部拦截
                .excludePathPatterns(
                        "/auth/captcha",
                        "/auth/admin/login",
                        "/auth/user/login",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",

                        "/uploads/kf/**",
                        "/uploads/**",

                        "/fd/user/register"
                ); // 白名单接口

        //洪水攻击防御策略
        registry.addInterceptor(floodInterceptor)
                .addPathPatterns("/**") // 限流的接口路径
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ); // 放行接口
        //权限防御政策
        registry.addInterceptor(permissionInterceptor())
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns(
                        "/auth/captcha",
                        "/auth/admin/login",
                        "/auth/user/login",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",

                        "/uploads/kf/**",
                        "/uploads/**",

                        "/fd/user/register"
                );
        //ip获取统计政策
        registry.addInterceptor(ipaccessInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",

                        "/uploads/kf/**",
                        "/uploads/**"
                );
        //项目隔离统计政策
        registry.addInterceptor(projectContextInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/captcha",
                        "/auth/admin/login",
                        "/auth/user/login",
                        "/auth/user/register",
                        "/auth/user/auth-token",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",

                        "/uploads/kf/**",
                        "/uploads/**",

                        "/fd/user/register"
                );
    }


}
