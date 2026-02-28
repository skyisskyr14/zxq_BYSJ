package com.sq.system.authapp.controller.admincore;

import com.sq.system.admincore.dto.UpdatePasswordDTO;
import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.common.utils.PasswordUtils;
import com.sq.system.framework.redis.AdminTokenService;
import com.sq.system.security.context.TokenContextHolder;
import com.sq.system.admincore.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "当前管理员接口")
@RestController
@RequestMapping("/admin")
public class AdminMeController {

    @Resource
    private AdminTokenService adminTokenService;

    @Resource
    private AdminRoleRepository adminRoleRepository;

    @Resource
    private AdminProjectRepository adminProjectRepository;

    @Resource
    private AdminUserRepository adminUserRepository;

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private ProjectRepository projectRepository;

    @PostConstruct
    public void init() {
        System.out.println("Cont======[1/5 system/authapp/admin] AdminMeController 初始化完成 ======");
    }


    @Operation(summary = "获取当前管理员信息")
    @GetMapping("/me")
    @AdminLog(module = "管理员核心", action = "获取当前管理员基本信息")
    public ResponseResult<Object> getAdminInfo() {
        AdminUserEntity user = TokenContextHolder.get();

        if(user == null){
            return ResponseResult.fail("未找到管理员");
        }

        List<Long> roleIds = adminRoleRepository.selectRoleIdsByAdminId(user.getId());
        List<String> roleNames = roleRepository.findRoleNameById(roleIds);
        List<String> roleCodes = roleRepository.findRoleCodesByids(roleIds);
//        List<Long> projectIds = adminProjectRepository.selectProjectIdsByAdminId(user.getId());
//        List<String> projectCodes = projectRepository.findProjectCodeById(projectIds);

        return ResponseResult.success(new AdminProfile(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getIsSuperAdmin(),
                roleNames,
                roleCodes
//                projectCodes
        ));
    }

    // 封装信息返回结构
    private record AdminProfile(Long id,
                                String username,
                                String nickname,
                                int isSuperAdmin,
                                List<String> roleNames,
                                List<String> roleCodes
//                                List<String> projectCodes
    ) {
    }

    @AdminLog(module = "管理员管理", action = "修改密码")
    @Operation(summary = "修改当前管理员密码")
    @PutMapping("/updatePassword")
    public ResponseResult<String> updatePassword(@RequestBody UpdatePasswordDTO dto, HttpServletRequest request) {

        AdminUserEntity user = getToken(request);
        if (user == null) return ResponseResult.fail("未登录");

        if (!PasswordUtils.matches(dto.getOldPassword(), user.getPassword())) {
            return ResponseResult.fail("原密码错误");
        }

        String newEncoded = PasswordUtils.encode(dto.getNewPassword());
        user.setPassword(newEncoded);
        user.setUpdate_time(String.valueOf(LocalDateTime.now()));
        adminUserRepository.updateById(user);
        return ResponseResult.success("密码修改成功");
    }

    private AdminUserEntity getToken(HttpServletRequest request) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("token".equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }


        return adminTokenService.getUserByToken(token);
    }
}
