package com.sq.system.admincore.model;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sq.system.admincore.entity.AdminProjectEntity;
import com.sq.system.admincore.entity.AdminRoleEntity;
import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.admincore.repository.AdminProjectRepository;
import com.sq.system.admincore.repository.AdminRoleRepository;
import com.sq.system.admincore.repository.AdminUserRepository;
import com.sq.system.admincore.vo.AdminLoginVO;
import com.sq.system.captcha.support.CaptchaDispatcher;
import com.sq.system.common.repository.SystemProjectRepository;
import com.sq.system.common.utils.JwtUtil;
import com.sq.system.common.utils.PasswordUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录业务封装类
 */
@Component
public class AdminLoginModel {

    private final AdminUserRepository repository; // 注入数据库操作类
    private final CaptchaDispatcher captchaDispatcher; // 注入验证码分发器
    private final AdminRoleRepository adminRoleRepository;
    private final AdminProjectRepository adminProjectRepository;
    private final SystemProjectRepository systemProjectRepository;
    // 构造器注入（必须两个都传）
    public AdminLoginModel(AdminUserRepository repository,
                           CaptchaDispatcher captchaDispatcher,
                           AdminRoleRepository adminRoleRepository,
                           AdminProjectRepository adminProjectRepository,
                           SystemProjectRepository systemProjectRepository) {
        this.repository = repository;
        this.captchaDispatcher = captchaDispatcher;
        this.adminRoleRepository = adminRoleRepository;
        this.adminProjectRepository = adminProjectRepository;
        this.systemProjectRepository = systemProjectRepository;
    }

    /**
     * 管理员登录逻辑
     */
    public AdminLoginVO login(String username, String password,
                              String projectCode,
                              String captchaType, String captchaUuid, String captchaInput) {

        AdminLoginVO vo = new AdminLoginVO();
        // 1. 验证验证码（必须先验证）
        boolean valid = captchaDispatcher.get(captchaType).verify(captchaUuid, captchaInput);
        if (!valid) {
            vo.setStatus("验证码错误");
            return vo;
        }

        // 2. 查询用户是否存在
        AdminUserEntity admin = repository.findByUsername(username);
        if (admin == null) {
            vo.setStatus("管理员不存在");
            return vo;
        }else{
            vo.setUser(admin);
        }

        Long projectId = systemProjectRepository.selectIdByProjectCode(projectCode);
        AdminProjectEntity isExsits = adminProjectRepository.selectOne(
                Wrappers.lambdaQuery(AdminProjectEntity.class)
                        .eq(AdminProjectEntity::getAdminId,admin.getId())
                        .eq(AdminProjectEntity::getProjectId,projectId)
        );
        if(isExsits == null){
            vo.setStatus("当前管理员不存在!");
            return vo;
        }

        if (!PasswordUtils.matches(password, admin.getPassword())) {
            vo.setStatus("密码错误");
            return vo;
        }

        // 3. 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", admin.getId());
        claims.put("username", admin.getUsername());
        claims.put("status", admin.getStatus());

        String token = JwtUtil.generateToken(claims);

        // 4. 构造响应对象
        vo.setStatus("登录成功");
        vo.setToken(token);
        return vo;
    }

    /**
     * 初始化创建管理员（不暴露注册）
     */
    public void create(String username, String password, String nickname,Long projectId) {
        AdminUserEntity user = new AdminUserEntity();
        user.setUsername(username);
        user.setPassword(PasswordUtils.encode(password)); // 密码加密
        user.setNickname(nickname);
        user.setIsSuperAdmin(0);
        user.setStatus(1); // 默认启用
        repository.insert(user);
        System.out.println(user);
        AdminRoleEntity adminRole = new AdminRoleEntity();
        adminRole.setAdminId(user.getId());
        adminRole.setRoleId(1L);
        adminRoleRepository.insert(adminRole);

        AdminProjectEntity aprj = new AdminProjectEntity();
        aprj.setAdminId(user.getId());
        aprj.setProjectId(projectId);
        adminProjectRepository.insert(aprj);

    }
}
