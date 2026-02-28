package com.sq.shop.model;

import com.sq.shop.dto.ZxyShopRegisterDto;
import com.sq.shop.entity.ZxyShopEntity;
import com.sq.shop.repository.ZxyShopRepository;
import com.sq.shop.vo.ZxyBaseInfoShopVo;
import com.sq.system.captcha.support.CaptchaDispatcher;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.entity.UserToProjectEntity;
import com.sq.system.usercore.entity.UserToRoleEntity;
import com.sq.system.usercore.repository.UserRepository;
import com.sq.system.usercore.repository.UserToProjectRepository;
import com.sq.system.usercore.repository.UserToRoleRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ZxyShopModel {

    @Resource
    private CaptchaDispatcher captchaDispatcher;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserToRoleRepository userToRoleRepository;
    @Autowired
    private UserToProjectRepository userToProjectRepository;
    @Autowired
    private ZxyShopRepository zxyShopRepository;

    public Map<String,Object> create(ZxyShopRegisterDto entity, Long projectId, String ip) {

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
        userToRole.setRoleId(3L);
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

        ZxyShopEntity zxyShopEntity = new ZxyShopEntity();
        zxyShopEntity.setSysId(userEntity.getId());
        zxyShopEntity.setRealname(entity.getNickname());
        zxyShopEntity.setCreateTime(LocalDateTime.now());
        zxyShopEntity.setUpdateTime(LocalDateTime.now());
        if(zxyShopRepository.insert(zxyShopEntity) == 0){
            map.put("code",false);
            map.put("mes","用户插入错误,请重试");
            return null;
        }

        map.put("code",true);
        map.put("mes",userEntity.getId());
        return map;
    }

    public boolean update(ZxyShopEntity zxyShop,String avatar,int gender) {
        zxyShop.setAvatar(avatar);
        zxyShop.setGender(gender);
        zxyShop.setUpdateTime(LocalDateTime.now());
        if(zxyShopRepository.updateById(zxyShop) == 0){
            return false;
        }else{
            return true;
        }
    }

    public ZxyBaseInfoShopVo getBaseInfo(String phone, ZxyShopEntity zxyShop) {
        ZxyBaseInfoShopVo baseInfoVo = new ZxyBaseInfoShopVo();
        baseInfoVo.setPhone(phone);
        baseInfoVo.setRealname(zxyShop.getRealname());
        baseInfoVo.setAvatar(zxyShop.getAvatar());
        baseInfoVo.setGender(zxyShop.getGender());
        baseInfoVo.setAge(zxyShop.getAge());
        baseInfoVo.setCreateTime(zxyShop.getCreateTime());
        return baseInfoVo;
    }
}
