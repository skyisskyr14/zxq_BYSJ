package com.sq.system.authapp.controller.admincore;

import com.sq.system.admincore.dto.AdminLoginDTO;
import com.sq.system.admincore.entity.log.AdminOperationLogEntity;
import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.admincore.model.AdminLoginModel;
import com.sq.system.admincore.repository.AdminProjectRepository;
import com.sq.system.admincore.vo.AdminLoginVO;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.common.utils.CookieUtil;
import com.sq.system.common.utils.IpUtil;
import com.sq.system.common.utils.PasswordUtils;
import com.sq.system.framework.redis.AdminIpAccessService;
import com.sq.system.framework.redis.AdminTokenService;
import com.sq.system.security.config.IpAccessConfig;
import com.sq.system.security.context.TokenContextHolder;
import com.sq.system.security.resolver.IpAccessResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "管理员登录注册接口", description = "管理员登录、退出、创建")
@RestController
@RequestMapping("/auth/admin")
public class AuthAdminController {

    @Resource
    private AdminLoginModel loginModel;

    @Resource
    private AdminTokenService adminTokenService;
    @Resource
    private AdminIpAccessService adminIpAccessService;
    @Resource
    private IpAccessResolver ipAccessResolver;
    @Resource
    private AdminProjectRepository adminProjectRepository;

    @Autowired
    private IpAccessConfig ipAccessConfig;

    @PostConstruct
    public void init() {
        System.out.println("Cont======[5/5 system/authapp/admin] AuthAdminController 初始化完成 ======");
        System.out.println(PasswordUtils.encode("qwe123123"));
    }

    @Operation(summary = "管理员登录", description = "校验验证码后，校验用户名与密码，登录成功，不返回token，由服务器设置token")
    @PostMapping("/login")
    public ResponseResult<String> login(@RequestBody AdminLoginDTO dto, HttpServletResponse response, HttpServletRequest request) {
        String ip = IpUtil.getIp(request);
        String uri = request.getRequestURI();

        AdminLoginVO vo = loginModel.login(
                dto.getUsername(),
                dto.getPassword(),
                dto.getProjectCode(),
                dto.getCaptchaType(),
                dto.getCaptchaUuid(),
                dto.getCaptchaInput()
        );

        AdminOperationLogEntity log = new AdminOperationLogEntity();
        log.setIpAddress(ip);
        log.setRequestUri(uri);
        log.setRequestMethod(request.getMethod());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setActionModule("system/admin-core,system/auth-app");
        log.setActionType("登录");
        log.setCreateTime(LocalDateTime.now());

        if (vo.getToken() == null) {
            log.setResultStatus("失败");
            log.setResultMessage(vo.getStatus());
            adminIpAccessService.recordAdminIpAccess(log, vo.getToken());
            return ResponseResult.fail(vo.getStatus());
        } else {
            AdminUserEntity admin = vo.getUser();
            log.setAdminId(admin.getId());
            log.setAdminAccount(admin.getUsername());
            log.setResultStatus("成功");
            log.setResultMessage(vo.getToken());

            adminTokenService.existsDelete(vo.getUser().getId());

            adminIpAccessService.recordAdminIpAccess(log, vo.getToken());
            adminTokenService.saveToken(vo.getToken(), vo.getUser());
            Long projectId = adminProjectRepository.selectProjectIdsByAdminId(admin.getId()).stream().findFirst().orElse(null);
            CookieUtil.setLoginCookies(response, vo.getToken(), projectId);
            if(vo.getUser().getIsSuperAdmin() == 1){
                return ResponseResult.success("超级管理员");
            }else{
                return ResponseResult.success("登录成功");
            }
        }
    }

    @Operation(summary = "管理员登出", description = "校验token后，删除redis的token缓存")
    @AdminLog( module = "system/autn-app",action = "管理员退出登录")
    @PostMapping("/logout")
    public ResponseResult<String> logout(HttpServletResponse response, HttpServletRequest request) {
        String ip = IpUtil.getIp(request);
        String uri = request.getRequestURI();

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isEmpty()) {
            token = extractTokenFromCookie(request);
        }

        if (token != null) {
            adminTokenService.removeToken(token);
        } else {
            return ResponseResult.fail("token不存在");
        }

        CookieUtil.clearLoginCookies(response);
        return ResponseResult.success("退出登录成功");
    }

    @Operation(summary = "管理员创建", description = "创建管理员")
    @PostMapping("/create")
    @AdminLog(module = "管理员核心", action = "创建管理员")
    public ResponseResult<?> create(@RequestBody Map<String, String> body) {
        AdminUserEntity admin = TokenContextHolder.get();
        if(admin == null || admin.getIsSuperAdmin() != 1){
            return ResponseResult.fail("权限不足");
        }

        String username = body.get("username");
        String password = body.get("password");
        String nickname = body.get("nickname");
        String project = body.get("projectId");

        loginModel.create(username, password, nickname, Long.valueOf(project));
        return ResponseResult.success("创建成功");
    }

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
