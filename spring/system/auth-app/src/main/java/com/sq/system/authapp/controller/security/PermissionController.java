package com.sq.system.authapp.controller.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.admincore.entity.PermissionEntity;
import com.sq.system.admincore.repository.PermissionRepository;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.PageResult;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.common.result.dto.PageRequest;
import com.sq.system.framework.redis.AdminIpAccessService;
import com.sq.system.framework.redis.AdminTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

@Tag(name = "权限码接口", description = "维护系统权限项")
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Resource
    private PermissionRepository permissionRepository;

    @Resource
    private AdminTokenService adminTokenService;

    @Resource
    private AdminIpAccessService adminIpAccessService;
    @PostConstruct
    public void init() {
        System.out.println("✅ 权限接口注入成功！");
    }

    @Operation(summary = "权限列表")
    @GetMapping("/list")
    @AdminLog(module = "安全核心", action = "获取权限列表")
    public ResponseResult<PageResult<PermissionEntity>> list(PageRequest pageRequest) {
        Page<PermissionEntity> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        QueryWrapper<PermissionEntity> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort");

        IPage<PermissionEntity> result = permissionRepository.selectPage(page, wrapper);
        return ResponseResult.success(PageResult.of(result));
    }

    @Operation(summary = "新增权限")
    @PostMapping("/create")
    @AdminLog(module = "安全核心", action = "新增权限")
    public ResponseResult<String> create(@RequestBody PermissionEntity permission,@CookieValue("token") String token, HttpServletRequest request) {
        AdminUserEntity admin = adminTokenService.getUserByToken(token);

        if (admin == null) {
            return ResponseResult.fail("未登录或 token 失效");
        }

        permissionRepository.insert(permission);
        return ResponseResult.success("添加成功");
    }

    @Operation(summary = "更新权限")
    @PutMapping("/update")
    @AdminLog(module = "安全核心", action = "更新权限")
    public ResponseResult<String> update(@RequestBody PermissionEntity permission,@CookieValue("token") String token, HttpServletRequest request) {
        AdminUserEntity admin = adminTokenService.getUserByToken(token);

        if (admin == null) {
            return ResponseResult.fail("未登录或 token 失效");
        }

        permissionRepository.update(permission);
        return ResponseResult.success("修改成功");
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/delete/{id}")
    @AdminLog(module = "安全核心", action = "删除权限")
    public ResponseResult<String> delete(@PathVariable Long id ,@CookieValue("token") String token, HttpServletRequest request) {

        AdminUserEntity admin = adminTokenService.getUserByToken(token);

        if (admin == null) {
            return ResponseResult.fail("未登录或 token 失效");
        }

        permissionRepository.deleteById(id);
        return ResponseResult.success("删除成功");
    }
}
