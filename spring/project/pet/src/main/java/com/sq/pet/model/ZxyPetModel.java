package com.sq.pet.model;

import com.sq.pet.dto.ZxyPetSaveDto;
import com.sq.pet.entity.ZxyPetEntity;
import com.sq.pet.repository.ZxyPetRepository;
import com.sq.pet.vo.ZxyPetVo;
import com.sq.user.entity.ZxyUserEntity;
import com.sq.user.repository.ZxyUserRepository;
import com.sq.system.usercore.entity.UserEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ZxyPetModel {

    @Resource
    private ZxyPetRepository zxyPetRepository;
    @Resource
    private ZxyUserRepository zxyUserRepository;

    public Long getBizUserIdBySysUser(UserEntity user) {
        ZxyUserEntity zxyUser = zxyUserRepository.getUserBySysId(user.getId());
        return zxyUser == null ? null : zxyUser.getId();
    }

    public List<ZxyPetVo> list(Long userId) {
        return zxyPetRepository.listByUserId(userId).stream().map(this::toVo).collect(Collectors.toList());
    }

    public boolean create(Long userId, ZxyPetSaveDto dto) {
        ZxyPetEntity entity = new ZxyPetEntity();
        entity.setUserId(userId);
        entity.setName(dto.getName().trim());
        entity.setType(dto.getType());
        entity.setAge(dto.getAge());
        entity.setWeight(dto.getWeight());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        return zxyPetRepository.insert(entity) > 0;
    }

    public boolean update(Long userId, ZxyPetSaveDto dto) {
        ZxyPetEntity entity = zxyPetRepository.getByIdAndUserId(dto.getId(), userId);
        if (entity == null) {
            return false;
        }
        boolean changed = false;
        if (StringUtils.hasText(dto.getName()) && !dto.getName().trim().equals(entity.getName())) {
            entity.setName(dto.getName().trim());
            changed = true;
        }
        if (dto.getType() != null && !Objects.equals(dto.getType(), entity.getType())) {
            entity.setType(dto.getType());
            changed = true;
        }
        if (dto.getAge() != null && !Objects.equals(dto.getAge(), entity.getAge())) {
            entity.setAge(dto.getAge());
            changed = true;
        }
        if (dto.getWeight() != null && !Objects.equals(dto.getWeight(), entity.getWeight())) {
            entity.setWeight(dto.getWeight());
            changed = true;
        }
        if (!changed) {
            return false;
        }
        entity.setUpdateTime(LocalDateTime.now());
        return zxyPetRepository.updateById(entity) > 0;
    }

    public boolean delete(Long userId, Long petId) {
        ZxyPetEntity entity = zxyPetRepository.getByIdAndUserId(petId, userId);
        if (entity == null) {
            return false;
        }
        return zxyPetRepository.deleteById(entity.getId()) > 0;
    }

    public ZxyPetVo detail(Long userId, Long petId) {
        ZxyPetEntity entity = zxyPetRepository.getByIdAndUserId(petId, userId);
        return entity == null ? null : toVo(entity);
    }

    private ZxyPetVo toVo(ZxyPetEntity entity) {
        ZxyPetVo vo = new ZxyPetVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setType(entity.getType());
        vo.setAge(entity.getAge());
        vo.setWeight(entity.getWeight());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
