package com.sq.system.authapp.controller.security;

import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.admincore.repository.RolePermissionRepository;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.framework.redis.AdminIpAccessService;
import com.sq.system.framework.redis.AdminTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import java.util.List;


@Tag(name = "角色权限接口", description = "角色绑定权限码接口")
@RestController
@RequestMapping("/role")
public class RolePermissionController {

    @Resource
    private RolePermissionRepository rolePermissionRepository;

    @Resource
    private AdminTokenService adminTokenService;

    @Resource
    private AdminIpAccessService adminIpAccessService;

    @PostConstruct
    public void init() {
        System.out.println("✅ 角色对应权限接口注入成功！");
    }

    @Operation(summary = "查询角色权限")
    @GetMapping("/{roleId}/permissions")
    @AdminLog(module = "安全核心", action = "查询角色权限")
    public ResponseResult<List<Long>> getPermissions(@PathVariable Long roleId,@CookieValue("token") String token, HttpServletRequest request) {
        AdminUserEntity admin = adminTokenService.getUserByToken(token);

        if (admin == null) {
            return ResponseResult.fail("未登录或 token 失效");
        }

        List<Long> permIds = rolePermissionRepository.selectPermIdsByRoleId(roleId);
        return ResponseResult.success(permIds);
    }

    @Operation(summary = "设置角色权限")
    @PostMapping("/{roleId}/permissions")
    @Transactional
    @AdminLog(module = "安全核心", action = "设置角色权限")
    public ResponseResult<String> setPermissions(@PathVariable Long roleId,
                                                 @RequestBody List<Long> permIds,
                                                 @CookieValue("token") String token,
                                                 HttpServletRequest request) {
        AdminUserEntity admin = adminTokenService.getUserByToken(token);

        if (admin == null) {
            return ResponseResult.fail("未登录或 token 失效");
        }

        rolePermissionRepository.deleteByRoleId(roleId);
        if (permIds != null && !permIds.isEmpty()) {
            rolePermissionRepository.insertBatch(roleId, permIds);
            return ResponseResult.success("操作成功");
        } else {
            return ResponseResult.success("清空成功");
        }
    }
}
