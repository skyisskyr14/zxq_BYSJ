package com.sq.shop.model;

import com.sq.shop.dto.ZxyShopCageSaveDto;
import com.sq.shop.entity.ZxyShopCageEntity;
import com.sq.shop.repository.ZxyShopCageRepository;
import com.sq.shop.vo.ZxyShopCageVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ZxyShopCageModel {

    @Resource
    private ZxyShopCageRepository shopCageRepository;

    public List<ZxyShopCageVo> list(Long shopId) {
        return shopCageRepository.listByShopId(shopId).stream().map(this::toVo).collect(Collectors.toList());
    }

    public boolean create(Long shopId, ZxyShopCageSaveDto dto) {
        if (shopCageRepository.getByCode(dto.getCode(), shopId) != null) {
            return false;
        }
        ZxyShopCageEntity entity = new ZxyShopCageEntity();
        entity.setShopId(shopId);
        entity.setCode(dto.getCode());
        entity.setSize(dto.getSize());
        entity.setPrice(dto.getPrice());
        entity.setStatus(normalizeStatus(dto.getStatus()));
        entity.setIsDelete(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        return shopCageRepository.insert(entity) > 0;
    }

    public boolean update(Long shopId, ZxyShopCageSaveDto dto) {
        ZxyShopCageEntity entity = shopCageRepository.getByIdAndShopId(dto.getId(), shopId);
        if (entity == null) {
            return false;
        }
        boolean changed = false;
        if (StringUtils.hasText(dto.getCode()) && !Objects.equals(dto.getCode(), entity.getCode())) {
            ZxyShopCageEntity sameCode = shopCageRepository.getByCode(dto.getCode(), shopId);
            if (sameCode != null && !Objects.equals(sameCode.getId(), entity.getId())) {
                return false;
            }
            entity.setCode(dto.getCode());
            changed = true;
        }
        if (StringUtils.hasText(dto.getSize()) && !Objects.equals(dto.getSize(), entity.getSize())) {
            entity.setSize(dto.getSize());
            changed = true;
        }
        if (dto.getPrice() != null && !Objects.equals(dto.getPrice(), entity.getPrice())) {
            entity.setPrice(dto.getPrice());
            changed = true;
        }
        String status = normalizeStatus(dto.getStatus());
        if (StringUtils.hasText(status) && !Objects.equals(status, entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }
        if (!changed) {
            return false;
        }
        entity.setUpdateTime(LocalDateTime.now());
        return shopCageRepository.updateById(entity) > 0;
    }

    public boolean delete(Long id, Long shopId) {
        ZxyShopCageEntity entity = shopCageRepository.getByIdAndShopId(id, shopId);
        if (entity == null) {
            return false;
        }
        entity.setIsDelete(1);
        entity.setUpdateTime(LocalDateTime.now());
        return shopCageRepository.updateById(entity) > 0;
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "free";
        }
        if ("occupied".equals(status)) {
            return "occupied";
        }
        return "free";
    }

    private ZxyShopCageVo toVo(ZxyShopCageEntity entity) {
        ZxyShopCageVo vo = new ZxyShopCageVo();
        vo.setId(entity.getId());
        vo.setCode(entity.getCode());
        vo.setSize(entity.getSize());
        vo.setPrice(entity.getPrice());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
