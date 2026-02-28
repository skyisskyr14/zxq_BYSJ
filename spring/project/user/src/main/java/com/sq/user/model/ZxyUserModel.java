package com.sq.user.model;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sq.system.captcha.support.CaptchaDispatcher;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.entity.UserToProjectEntity;
import com.sq.system.usercore.entity.UserToRoleEntity;
import com.sq.system.usercore.repository.UserRepository;
import com.sq.system.usercore.repository.UserToProjectRepository;
import com.sq.system.usercore.repository.UserToRoleRepository;
import com.sq.user.dto.ZxyUserRegisterDto;
import com.sq.user.entity.ZxyUserEntity;
import com.sq.user.repository.ZxyUserRepository;
import com.sq.user.vo.ZxyUserBaseInfoVo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ZxyUserModel {

    @Resource
    private ZxyUserRepository zxyUserRepository;
    @Resource
    private UserRepository userRepository;
    @Autowired
    private UserToRoleRepository userToRoleRepository;
    @Autowired
    private UserToProjectRepository userToProjectRepository;
    @Resource
    private CaptchaDispatcher captchaDispatcher;

    public Map<String,Object> create(ZxyUserRegisterDto entity,Long projectId,String ip) {

        Map<String,Object> map = new HashMap<>();

//        System.out.println("UserRepository loaded from: " +
//                UserRepository.class.getProtectionDomain().getCodeSource().getLocation());

        boolean valid = captchaDispatcher.get(entity.getCaptchaType()).verify(entity.getUuid(), entity.getCaptcha());
        if (!valid) {
            map.put("code",false);
            map.put("mes","验证码错误");
            return map;
        }

        if(userRepository.findByUsername(entity.getPhone()) != null){
            map.put("code",false);
            map.put("mes","手机号已经注册,请重试");
            return map;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(entity.getPhone());
        userEntity.setPassword(entity.getPhone());
        userEntity.setPassword(entity.getPassword());
        userEntity.setCreateTime(LocalDateTime.now());
        userEntity.setUpdateTime(LocalDateTime.now());
        userEntity.setCreateIp(ip);
        if(userRepository.insert(userEntity) == 0){
            map.put("code",false);
            map.put("mes","系统插入错误,请重试");
            return map;
        }

        UserToRoleEntity userToRole = new UserToRoleEntity();
        userToRole.setUserId(userEntity.getId());
        userToRole.setRoleId(2L);
        if(userToRoleRepository.insert(userToRole)== 0){
            map.put("code",false);
            map.put("mes","校色权限插入错误,请重试");
            return null;
        }

        UserToProjectEntity userToProject = new UserToProjectEntity();
        userToProject.setUserId(userEntity.getId());
        userToProject.setProjectId(projectId);
        if(userToProjectRepository.insert(userToProject) == 0){
            map.put("code",false);
            map.put("mes","项目权限插入错误,请重试");
            return null;
        }

        ZxyUserEntity zxyUserEntity = new ZxyUserEntity();
        zxyUserEntity.setSysId(userEntity.getId());
        zxyUserEntity.setNickname(entity.getNickname());
        zxyUserEntity.setCreateTime(LocalDateTime.now());
        zxyUserEntity.setUpdateTime(LocalDateTime.now());
        if(zxyUserRepository.insert(zxyUserEntity) == 0){
            map.put("code",false);
            map.put("mes","用户插入错误,请重试");
            return null;
        }

        map.put("code",true);
        map.put("mes",userEntity.getId());
        return map;
    }

    public boolean updateBaseInfo(ZxyUserEntity zxyUser, String nickname, String avatar, Integer gender) {
        boolean changed = false;

        if (StringUtils.hasText(nickname) && !nickname.equals(zxyUser.getNickname())) {
            zxyUser.setNickname(nickname);
            changed = true;
        }

        if (StringUtils.hasText(avatar) && !avatar.equals(zxyUser.getAvatar())) {
            zxyUser.setAvatar(avatar);
            changed = true;
        }

        if (gender != null && gender >= 0 && !gender.equals(zxyUser.getGender())) {
            zxyUser.setGender(gender);
            changed = true;
        }

        if (!changed) {
            return false;
        }

        zxyUser.setUpdateTime(LocalDateTime.now());
        return zxyUserRepository.updateById(zxyUser) > 0;
    }

    public void delete(Long id) {
        zxyUserRepository.deleteById(id); // 你实体上 @TableLogic 则为逻辑删除
    }

    public ZxyUserBaseInfoVo getBaseInfo(String phone,ZxyUserEntity zxyUser) {
        ZxyUserBaseInfoVo baseInfoVo = new ZxyUserBaseInfoVo();
        baseInfoVo.setPhone(phone);
        baseInfoVo.setNickname(zxyUser.getNickname());
        baseInfoVo.setAvatar(zxyUser.getAvatar());
        baseInfoVo.setGender(zxyUser.getGender());
        baseInfoVo.setCreateTime(zxyUser.getCreateTime());
        return baseInfoVo;
    }

    public Page<ZxyUserEntity> page(Integer pageNum, Integer pageSize, String phone, String username, Integer status) {
        LambdaQueryWrapper<ZxyUserEntity> qw = Wrappers.lambdaQuery(ZxyUserEntity.class)
//                .eq(StringUtils.hasText(phone), UserEntity::getPhone, phone)
//                .like(StringUtils.hasText(username), UserEntity::getUsername, username)
//                .eq(status != null, UserEntity::getStatus, status)
                .orderByDesc(ZxyUserEntity::getId);

        return zxyUserRepository.selectPage(new Page<>(pageNum, pageSize), qw);
    }
}