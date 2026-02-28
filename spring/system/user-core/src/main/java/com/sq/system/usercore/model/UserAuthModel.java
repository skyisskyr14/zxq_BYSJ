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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class UserAuthModel {

    private final UserRepository userCoreRepository;
    private final CaptchaDispatcher captchaDispatcher;
    private final UserToProjectRepository userToProjectRepository;
    private final UserToRoleRepository userToRoleRepository;

//    public UserAuthModel(UserRepository userRepository,
//                           CaptchaDispatcher captchaDispatcher,
//                           UserToProjectRepository userToProjectRepository) {
//        this.userRepository = userRepository;
//        this.captchaDispatcher = captchaDispatcher;
//        this.userToProjectRepository = userToProjectRepository;
//    }


    public String register(UserRegisterDTO dto,long projectId) {
//        boolean valid = captchaDispatcher.get("default").verify(dto.getCaptchaUuid(), dto.getCaptchaInput());
//
//        if (!valid) {
//            return "验证码错误";
//        }

        if (userCoreRepository.findByUsername(dto.getUsername()) != null) {
            return "用户名已存在";
        }

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setSecurePassword(dto.getSecurePassword());
//        user.setProjectId(1L);
//        user.setRoleId(3);
        user.setStatus(1);
        user.setEmail(dto.getEmail());
//        user.setProjectId(projectId); // 自动注入项目
        user.setCreateTime(LocalDateTime.now());

        userCoreRepository.insert(user);
        return "注册成功";
    }

    public UserLoginVO login(UserLoginDTO dto,String ip) {
        UserLoginVO vo = new UserLoginVO();
        // 1. 验证验证码（必须先验证）
//       System.out.println(dto);
        boolean valid = captchaDispatcher.get(dto.getCaptchaType()).verify(dto.getCaptchaUuid(), dto.getCaptchaInput());
        if (!valid) {
            vo.setStatus("验证码错误");
            return vo;
        }

        // 2. 查询用户是否存在
        UserEntity user = userCoreRepository.findByUsername(dto.getUsername());
        if (user == null) {
            vo.setStatus("用户名不存在");
            return vo;
        }else{
//            System.out.println(user.getId());
            if(Objects.equals(userToProjectRepository.selectOne(
                    Wrappers.lambdaQuery(UserToProjectEntity.class)
                            .eq(UserToProjectEntity::getUserId,user.getId())
            ).getProjectId(), dto.getType())
            && Objects.equals(userToRoleRepository.selectOne(
                    Wrappers.lambdaQuery(UserToRoleEntity.class)
                            .eq(UserToRoleEntity::getUserId,user.getId())
            ).getRoleId(), dto.getRole())){
                vo.setUser(user);
            }else{
                vo.setStatus("用户不存在");
                return vo;
            }
        }


        if (!Objects.equals(dto.getPassword(), user.getPassword())) {
            vo.setStatus("密码错误");
            return vo;
        }

        if(user.getStatus() == 0){
            vo.setStatus("用户因违规操作已被封禁");
            return vo;
        }

        user.setNowIp(ip);
        userCoreRepository.updateById(user);

        // 3. 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("status", user.getStatus());

        String token = JwtUtil.generateToken(claims);

        // 4. 构造响应对象
        vo.setStatus("登录成功");
        vo.setToken(token);
        return vo;
    }
}

