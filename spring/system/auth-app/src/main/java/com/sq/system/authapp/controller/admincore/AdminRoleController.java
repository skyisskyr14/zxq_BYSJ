package com.sq.system.authapp.controller.admincore;

import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.admincore.entity.log.AdminOperationLogEntity;
import com.sq.system.admincore.repository.AdminRoleRepository;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.common.utils.IpUtil;
import com.sq.system.framework.redis.AdminIpAccessService;
import com.sq.system.framework.redis.AdminTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "管理管理员角色及权限接口", description = "查询某管理员角色、设置某管理员角色")
@RestController
@RequestMapping("/admin")
public class AdminRoleController {

    @Resource
    private AdminRoleRepository adminRoleRepository;

    @Resource
    private AdminTokenService adminTokenService;

    @Resource
    private AdminIpAccessService adminIpAccessService;

    @PostConstruct
    public void init() {
        System.out.println("Cont======[4/5 system/authapp/admin] AdminRoleController 初始化完成 ======");
    }

    /**
     * 查询某管理员已有角色 ID 集合
     */
    @Operation(summary = " 查询某管理员已有角色 ID 集合", description = "返回管理员校色Id集合")
    @GetMapping("/{adminId}/roles")
    @AdminLog(module = "管理员核心", action = "获取当前管理员角色")
    public ResponseResult<List<Long>> getAdminRoles(@PathVariable Long adminId,@CookieValue("token") String token, HttpServletRequest request) {
        AdminUserEntity admin = adminTokenService.getUserByToken(token);

        String ip = IpUtil.getIp(request); // 获取客户端 IP
        String location = IpUtil.getIp2region(ip); // IP 归属地
        String uri = request.getRequestURI(); // 当前访问 URI

        AdminOperationLogEntity log = new AdminOperationLogEntity();
        log.setIpAddress(ip);
        log.setRequestUri(uri);
        log.setRequestMethod(request.getMethod());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setActionModule("管理员核心");
        log.setActionType("查询某管理员已有角色");
        log.setRequestParam(null);
        log.setCreateTime(LocalDateTime.now());
        if (admin == null) {
            log.setAdminId(null);
            log.setAdminAccount(null);
            log.setResultStatus("失败");
            log.setResultMessage("未登录或 token 失效");
            adminIpAccessService.recordAdminIpAccess(log,token);
            return ResponseResult.fail("未登录或 token 失效");
        }

        log.setAdminId(admin.getId());
        log.setAdminAccount(admin.getUsername());
        log.setResultStatus("成功");
        List<Long> roleIds = adminRoleRepository.selectRoleIdsByAdminId(adminId);
        String joined = roleIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        log.setResultMessage(joined);
        adminIpAccessService.recordAdminIpAccess(log,token);
        return ResponseResult.success(roleIds);
    }

    /**
     * 设置管理员的角色（会清除原有角色）
     */
    @Operation(summary = " 设置管理员的角色", description = "会清除原有角色    ")
    @PostMapping("/{adminId}/roles")
    @Transactional
    @AdminLog(module = "管理员核心", action = "设置当前管理员角色")
    public ResponseResult<String> setAdminRoles(@PathVariable Long adminId,
                                                @RequestBody List<Long> roleIds,
                                                @CookieValue("token") String token,
                                                HttpServletRequest request) {
        AdminUserEntity admin = adminTokenService.getUserByToken(token);

        String ip = IpUtil.getIp(request); // 获取客户端 IP
        String location = IpUtil.getIp2region(ip); // IP 归属地
        String uri = request.getRequestURI(); // 当前访问 URI

        AdminOperationLogEntity log = new AdminOperationLogEntity();
        log.setIpAddress(ip);
        log.setRequestUri(uri);
        log.setRequestMethod(request.getMethod());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setActionModule("管理员核心");
        log.setActionType("设置管理员的角色");
        log.setRequestParam(roleIds.toString());
        log.setCreateTime(LocalDateTime.now());
        if (admin == null) {
            log.setAdminId(null);
            log.setAdminAccount(null);
            log.setResultStatus("失败");
            log.setResultMessage("未登录或 token 失效");
            adminIpAccessService.recordAdminIpAccess(log,token);
            return ResponseResult.fail("未登录或 token 失效");
        }

        log.setAdminId(admin.getId());
        log.setAdminAccount(admin.getUsername());
        log.setResultStatus("成功");

        adminRoleRepository.deleteByAdminId(adminId);
        if (roleIds != null && !roleIds.isEmpty()) {
            adminRoleRepository.insertBatch(adminId, roleIds);
            log.setResultMessage("操作成功");
        }else{
            log.setResultMessage("null:清空管理员角色绑定");
        }
        adminIpAccessService.recordAdminIpAccess(log,token);
        return ResponseResult.success("操作成功");
    }
}
