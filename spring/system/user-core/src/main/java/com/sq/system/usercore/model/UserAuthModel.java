package com.sq.system.usercore.model;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sq.system.captcha.support.CaptchaDispatcher;
import com.sq.system.common.utils.JwtUtil;
import com.sq.system.usercore.dto.UserLoginDTO;
import com.sq.system.usercore.dto.UserRegisterDTO;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.entity.UserToProjectEntity;
import com.sq.system.usercore.entity.UserToRoleEntity;
import com.sq.system.usercore.repository.UserRepository;
import com.sq.system.usercore.repository.UserToProjectRepository;
import com.sq.system.usercore.repository.UserToRoleRepository;
import com.sq.system.usercore.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserAuthModel {

    private final UserRepository userCoreRepository;
    private final CaptchaDispatcher captchaDispatcher;
    private final UserToProjectRepository userToProjectRepository;
    private final UserToRoleRepository userToRoleRepository;

    public String register(UserRegisterDTO dto, long projectId) {
        // 1) 验证码（你暂时注释掉了，这里保持你的逻辑）
        // boolean valid = captchaDispatcher.get("default").verify(dto.getCaptchaUuid(), dto.getCaptchaInput());
        // if (!valid) return "验证码错误";

        // 2) 用户名是否存在
        if (userCoreRepository.findByUsername(dto.getUsername()) != null) {
            return "用户名已存在";
        }

        // 3) 组装并写入用户
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setSecurePassword(dto.getSecurePassword());
        user.setStatus(1);
        user.setEmail(dto.getEmail());
        user.setCreateTime(LocalDateTime.now());

        userCoreRepository.insert(user);
        return "注册成功";
    }

    public UserLoginVO login(UserLoginDTO dto, String ip) {
        UserLoginVO vo = new UserLoginVO();

        // 1) 验证码校验
        boolean valid = captchaDispatcher.get(dto.getCaptchaType())
                .verify(dto.getCaptchaUuid(), dto.getCaptchaInput());

        if (!valid) {
            vo.setStatus("验证码错误");
            return vo;
        }

        // 2) 查询用户
        UserEntity user = userCoreRepository.findByUsername(dto.getUsername());
        if (user == null) {
            vo.setStatus("用户名不存在");
            return vo;
        }else{
            UserToProjectEntity userProject = userToProjectRepository.selectOne(
                    Wrappers.lambdaQuery(UserToProjectEntity.class)
                            .eq(UserToProjectEntity::getUserId, user.getId())
            );
            UserToRoleEntity userRole = userToRoleRepository.selectOne(
                    Wrappers.lambdaQuery(UserToRoleEntity.class)
                            .eq(UserToRoleEntity::getUserId, user.getId())
            );

            if (userProject == null || userRole == null) {
                vo.setStatus("用户不存在");
                return vo;
            }

            if (Objects.equals(userProject.getProjectId(), dto.getType())
                    && Objects.equals(userRole.getRoleId(), dto.getRole())) {
                vo.setUser(user);
            }else{
                vo.setStatus("用户不存在");
                return vo;
            }
        }

        // 3) 校验项目 / 角色（修复：selectOne 可能为 null，避免 .getXxx() 直接 NPE）
        UserToProjectEntity userToProject = userToProjectRepository.selectOne(
                Wrappers.lambdaQuery(UserToProjectEntity.class)
                        .eq(UserToProjectEntity::getUserId, user.getId())
                        .last("limit 1")
        );

        UserToRoleEntity userToRole = userToRoleRepository.selectOne(
                Wrappers.lambdaQuery(UserToRoleEntity.class)
                        .eq(UserToRoleEntity::getUserId, user.getId())
                        .last("limit 1")
        );

        // 任何一条绑定关系不存在，都视为“用户不存在/不匹配”
        if (userToProject == null || userToRole == null) {
            vo.setStatus("用户不存在");
            return vo;
        }

        // dto.getType() 你这里是 projectId / projectType 的含义，我保持原逻辑不动
        if (!Objects.equals(userToProject.getProjectId(), dto.getType())
                || !Objects.equals(userToRole.getRoleId(), dto.getRole())) {
            vo.setStatus("用户不存在");
            return vo;
        }

        // 4) 密码校验
        if (!Objects.equals(dto.getPassword(), user.getPassword())) {
            vo.setStatus("密码错误");
            return vo;
        }

        // 5) 状态校验
        if (user.getStatus() == 0) {
            vo.setStatus("用户因违规操作已被封禁");
            return vo;
        }

        // 6) 更新 IP
        user.setNowIp(ip);
        userCoreRepository.updateById(user);

        // 7) 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("status", user.getStatus());

        String token = JwtUtil.generateToken(claims);

        // 8) 返回 VO
        vo.setUser(user);
        vo.setStatus("登录成功");
        vo.setToken(token);
        return vo;
    }
}
