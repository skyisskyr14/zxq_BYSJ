package com.sq.system.framework.swagger;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("SQ 平台接口文档")
                .version("1.0.0")
                .description("分系统和对应项目"));
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("系统api")
                .packagesToScan("com.skyissky.authapp.controller") // 指定你接口包
                .build();
    }
//
//    @Bean
//    public GroupedOpenApi fansApi() {
//        return GroupedOpenApi.builder()
//                .group("fans-api")
//                .packagesToScan("com.skyissky.projects.fans_project.controller","com.skyissky.extend.fans.controller") // 指定你接口包
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi zxApi() {
//        return GroupedOpenApi.builder()
//                .group("zx-api")
//                .packagesToScan("com.skyissky.projects.zx_project.controller","com.skyissky.extend.zx.controller") // 指定你接口包
//                .build();
//    }
}
