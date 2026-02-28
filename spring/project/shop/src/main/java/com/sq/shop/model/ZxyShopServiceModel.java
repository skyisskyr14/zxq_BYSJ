package com.sq.shop.model;

import com.sq.shop.dto.ZxyShopServiceSaveDto;
import com.sq.shop.entity.ZxyShopServiceEntity;
import com.sq.shop.repository.ZxyShopServiceRepository;
import com.sq.shop.vo.ZxyShopServiceVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ZxyShopServiceModel {

    @Resource
    private ZxyShopServiceRepository shopServiceRepository;

    public List<ZxyShopServiceVo> list(Long shopId) {
        return shopServiceRepository.listByShopId(shopId).stream().map(this::toVo).collect(Collectors.toList());
    }

    public boolean create(Long shopId, ZxyShopServiceSaveDto dto) {
        ZxyShopServiceEntity entity = new ZxyShopServiceEntity();
        entity.setShopId(shopId);
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        entity.setIsDelete(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        return shopServiceRepository.insert(entity) > 0;
    }

    public boolean update(Long shopId, ZxyShopServiceSaveDto dto) {
        ZxyShopServiceEntity entity = shopServiceRepository.getByIdAndShopId(dto.getId(), shopId);
        if (entity == null) {
            return false;
        }
        boolean changed = false;
        if (StringUtils.hasText(dto.getName()) && !Objects.equals(dto.getName(), entity.getName())) {
            entity.setName(dto.getName());
            changed = true;
        }
        if (dto.getPrice() != null && !Objects.equals(dto.getPrice(), entity.getPrice())) {
            entity.setPrice(dto.getPrice());
            changed = true;
        }
        if (!Objects.equals(dto.getDescription(), entity.getDescription())) {
            entity.setDescription(dto.getDescription());
            changed = true;
        }
        if (!changed) {
            return false;
        }
        entity.setUpdateTime(LocalDateTime.now());
        return shopServiceRepository.updateById(entity) > 0;
    }

    public boolean delete(Long id, Long shopId) {
        ZxyShopServiceEntity entity = shopServiceRepository.getByIdAndShopId(id, shopId);
        if (entity == null) {
            return false;
        }
        entity.setIsDelete(1);
        entity.setUpdateTime(LocalDateTime.now());
        return shopServiceRepository.updateById(entity) > 0;
    }

    private ZxyShopServiceVo toVo(ZxyShopServiceEntity entity) {
        ZxyShopServiceVo vo = new ZxyShopServiceVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setPrice(entity.getPrice());
        vo.setDescription(entity.getDescription());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
