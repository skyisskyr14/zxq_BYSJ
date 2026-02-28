package com.sq.system.authapp.controller.admincore;

import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.admincore.model.AdminPermissionModel;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.annotation.PermissionLimit;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.framework.redis.AdminIpAccessService;
import com.sq.system.framework.redis.AdminTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "管理员权限接口", description = "获取当前管理员权限字段")
@RestController
@RequestMapping("/auth/admin")
public class AdminPermissionController {

    @Resource
    private AdminTokenService adminTokenService;

    @Resource
    private AdminPermissionModel permissionModel;

    @Resource
    private AdminIpAccessService adminIpAccessService;

    @PostConstruct
    public void init() {
        System.out.println("Cont======[2/5 system/authapp/admin] AdminPermissionController 初始化完成 ======");
    }


    /**
     * 获取当前管理员权限码集合
     */
    @Operation(summary = "获取管理员权限字段", description = "校验验证码后，返回当前管理员权限字段")
    @PermissionLimit(value = "admin:permission")
    @GetMapping("/permissions")
    @AdminLog(module = "管理员核心", action = "获取当前管理员权限字段")
    public ResponseResult<Set<String>> getPermissions(@CookieValue("token") String token, HttpServletRequest request) {
        AdminUserEntity admin = adminTokenService.getUserByToken(token);

        if (admin == null) {
            return ResponseResult.fail("未登录或 token 失效");
        }
        Set<String> permissions = permissionModel.getPermissionCodes(admin.getId());
        return ResponseResult.success(permissions);
    }
}
