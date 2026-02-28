package com.sq.shop.model;

import com.sq.shop.dto.ZxyShopRegisterDto;
import com.sq.shop.dto.ZxyShopSaveDto;
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
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        zxyShopEntity.setIsDelete(0);
        if(zxyShopRepository.insert(zxyShopEntity) == 0){
            map.put("code",false);
            map.put("mes","用户插入错误,请重试");
            return null;
        }

        map.put("code",true);
        map.put("mes",userEntity.getId());
        return map;
    }

    public boolean createShopInfo(Long sysId, ZxyShopSaveDto dto) {
        if (zxyShopRepository.getShopBySysId(sysId) != null) {
            return false;
        }
        ZxyShopEntity shop = new ZxyShopEntity();
        shop.setSysId(sysId);
        shop.setRealname(dto.getRealname());
        shop.setAvatar(dto.getAvatar());
        shop.setGender(dto.getGender() == null ? 0 : dto.getGender());
        shop.setAge(dto.getAge() == null ? 0 : dto.getAge());
        shop.setIsDelete(0);
        shop.setCreateTime(LocalDateTime.now());
        shop.setUpdateTime(LocalDateTime.now());
        return zxyShopRepository.insert(shop) > 0;
    }

    public boolean updateShopInfo(ZxyShopEntity zxyShop, ZxyShopSaveDto dto) {
        boolean changed = false;
        if (StringUtils.hasText(dto.getRealname()) && !Objects.equals(dto.getRealname(), zxyShop.getRealname())) {
            zxyShop.setRealname(dto.getRealname());
            changed = true;
        }
        if (StringUtils.hasText(dto.getAvatar()) && !Objects.equals(dto.getAvatar(), zxyShop.getAvatar())) {
            zxyShop.setAvatar(dto.getAvatar());
            changed = true;
        }
        if (dto.getGender() != null && dto.getGender() >= 0 && !Objects.equals(dto.getGender(), zxyShop.getGender())) {
            zxyShop.setGender(dto.getGender());
            changed = true;
        }
        if (dto.getAge() != null && dto.getAge() >= 0 && !Objects.equals(dto.getAge(), zxyShop.getAge())) {
            zxyShop.setAge(dto.getAge());
            changed = true;
        }
        if (!changed) {
            return false;
        }
        zxyShop.setUpdateTime(LocalDateTime.now());
        return zxyShopRepository.updateById(zxyShop) > 0;
    }

    public boolean deleteShopInfo(ZxyShopEntity zxyShop) {
        zxyShop.setIsDelete(1);
        zxyShop.setUpdateTime(LocalDateTime.now());
        return zxyShopRepository.updateById(zxyShop) > 0;
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

    public List<ZxyBaseInfoShopVo> listShops() {
        return zxyShopRepository.listValidShops().stream().map(shop -> {
            ZxyBaseInfoShopVo vo = new ZxyBaseInfoShopVo();
            UserEntity user = userRepository.selectById(shop.getSysId());
            vo.setPhone(user == null ? "" : user.getUsername());
            vo.setRealname(shop.getRealname());
            vo.setAvatar(shop.getAvatar());
            vo.setGender(shop.getGender());
            vo.setAge(shop.getAge());
            vo.setCreateTime(shop.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }
}
