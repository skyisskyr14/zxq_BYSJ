package com.sq.system.authapp.controller.admincore;

import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.admincore.repository.AdminProjectRepository;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.framework.redis.AdminIpAccessService;
import com.sq.system.framework.redis.AdminTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "管理员项目绑定接口")
@RestController
@RequestMapping("/admin")
public class AdminProjectController {

    @Resource
    private AdminProjectRepository adminProjectRepository;

    @Resource
    private AdminTokenService adminTokenService;

    @Resource
    private AdminIpAccessService adminIpAccessService;

    @PostConstruct
    public void init() {
        System.out.println("Cont======[3/5 system/authapp/admin] AdminProjectController 初始化完成 ======");
    }

    @GetMapping("/{adminId}/projects")
    @AdminLog(module = "管理员核心", action = "查询管理员绑定项目")
    public ResponseResult<List<Long>> getAdminProjects(@CookieValue("token") String token, @PathVariable Long adminId, HttpServletRequest request) {
        AdminUserEntity admin = adminTokenService.getUserByToken(token);

        if (admin == null) {
            return ResponseResult.fail("未登录或 token 失效");
        }
        List<Long> longList = adminProjectRepository.selectProjectIdsByAdminId(adminId);

        return ResponseResult.success(longList);
    }

    @PostMapping("/{adminId}/projects")
    @Transactional
    @AdminLog(module = "管理员核心", action = "修改管理员绑定项目")
    public ResponseResult<String> setAdminProjects(@PathVariable Long adminId,
                                                   @RequestBody List<Long> projectIds,
                                                   @CookieValue("token") String token,
                                                   HttpServletRequest request) {

        AdminUserEntity admin = adminTokenService.getUserByToken(token);

        if (admin == null) {
            return ResponseResult.fail("未登录或 token 失效");
        }

        adminProjectRepository.deleteByAdminId(adminId);
        if (projectIds != null && !projectIds.isEmpty()) {
            return ResponseResult.success("绑定成功");
        }else{
            return ResponseResult.success("清空成功");
        }
    }
}
