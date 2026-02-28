package com.sq.shop.model;

import com.sq.shop.dto.ZxyShopStoreSaveDto;
import com.sq.shop.entity.ZxyShopStoreEntity;
import com.sq.shop.entity.ZxyShopStoreImageEntity;
import com.sq.shop.repository.ZxyShopStoreImageRepository;
import com.sq.shop.repository.ZxyShopStoreRepository;
import com.sq.shop.vo.ZxyShopStoreVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ZxyShopStoreModel {

    @Resource
    private ZxyShopStoreRepository shopStoreRepository;

    @Resource
    private ZxyShopStoreImageRepository shopStoreImageRepository;

    public ZxyShopStoreVo detail(Long shopId) {
        ZxyShopStoreEntity store = shopStoreRepository.getByShopId(shopId);
        if (store == null) {
            return null;
        }
        return toVo(store);
    }

    public ZxyShopStoreVo detailById(Long id) {
        ZxyShopStoreEntity store = shopStoreRepository.getByIdValid(id);
        if (store == null) {
            return null;
        }
        return toVo(store);
    }

    public List<ZxyShopStoreVo> listAll() {
        return shopStoreRepository.listAll().stream().map(this::toVo).collect(Collectors.toList());
    }

    public boolean create(Long shopId, ZxyShopStoreSaveDto dto) {
        if (shopStoreRepository.getByShopId(shopId) != null) {
            return false;
        }
        ZxyShopStoreEntity entity = new ZxyShopStoreEntity();
        entity.setShopId(shopId);
        entity.setName(dto.getName());
        entity.setCity(dto.getCity());
        entity.setAddress(dto.getAddress());
        entity.setPhone(dto.getPhone());
        entity.setScore(normalizeScore(dto.getScore()));
        entity.setIntro(dto.getIntro());
        entity.setIsDelete(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        if (shopStoreRepository.insert(entity) == 0) {
            return false;
        }
        replaceImages(entity.getId(), dto.getImages());
        return true;
    }

    public boolean update(Long shopId, ZxyShopStoreSaveDto dto) {
        ZxyShopStoreEntity entity = shopStoreRepository.getByShopId(shopId);
        if (entity == null) {
            return false;
        }

        boolean changed = false;
        if (StringUtils.hasText(dto.getName()) && !Objects.equals(dto.getName(), entity.getName())) {
            entity.setName(dto.getName());
            changed = true;
        }
        if (StringUtils.hasText(dto.getCity()) && !Objects.equals(dto.getCity(), entity.getCity())) {
            entity.setCity(dto.getCity());
            changed = true;
        }
        if (StringUtils.hasText(dto.getAddress()) && !Objects.equals(dto.getAddress(), entity.getAddress())) {
            entity.setAddress(dto.getAddress());
            changed = true;
        }
        if (StringUtils.hasText(dto.getPhone()) && !Objects.equals(dto.getPhone(), entity.getPhone())) {
            entity.setPhone(dto.getPhone());
            changed = true;
        }
        Integer score = normalizeScore(dto.getScore());
        if (score != null && !Objects.equals(score, entity.getScore())) {
            entity.setScore(score);
            changed = true;
        }
        if (!Objects.equals(dto.getIntro(), entity.getIntro())) {
            entity.setIntro(dto.getIntro());
            changed = true;
        }

        List<String> oldImages = listImageUrls(entity.getId());
        List<String> newImages = normalizeImages(dto.getImages());
        if (!Objects.equals(oldImages, newImages)) {
            replaceImages(entity.getId(), newImages);
            changed = true;
        }

        if (!changed) {
            return false;
        }

        entity.setUpdateTime(LocalDateTime.now());
        return shopStoreRepository.updateById(entity) > 0;
    }

    public boolean delete(Long shopId) {
        ZxyShopStoreEntity entity = shopStoreRepository.getByShopId(shopId);
        if (entity == null) {
            return false;
        }
        entity.setIsDelete(1);
        entity.setUpdateTime(LocalDateTime.now());
        shopStoreImageRepository.deleteByStoreId(entity.getId());
        return shopStoreRepository.updateById(entity) > 0;
    }

    private ZxyShopStoreVo toVo(ZxyShopStoreEntity store) {
        ZxyShopStoreVo vo = new ZxyShopStoreVo();
        vo.setId(store.getId());
        vo.setName(store.getName());
        vo.setCity(store.getCity());
        vo.setAddress(store.getAddress());
        vo.setPhone(store.getPhone());
        vo.setScore(store.getScore());
        vo.setIntro(store.getIntro());
        vo.setImages(listImageUrls(store.getId()));
        vo.setCreateTime(store.getCreateTime());
        vo.setUpdateTime(store.getUpdateTime());
        return vo;
    }

    private List<String> listImageUrls(Long storeId) {
        return shopStoreImageRepository.listByStoreId(storeId)
                .stream()
                .map(ZxyShopStoreImageEntity::getImageUrl)
                .collect(Collectors.toList());
    }

    private void replaceImages(Long storeId, List<String> images) {
        shopStoreImageRepository.deleteByStoreId(storeId);
        List<String> validImages = normalizeImages(images);
        for (int i = 0; i < validImages.size(); i++) {
            ZxyShopStoreImageEntity image = new ZxyShopStoreImageEntity();
            image.setStoreId(storeId);
            image.setImageUrl(validImages.get(i));
            image.setSort(i + 1);
            image.setCreateTime(LocalDateTime.now());
            shopStoreImageRepository.insert(image);
        }
    }

    private List<String> normalizeImages(List<String> images) {
        if (images == null) {
            return Collections.emptyList();
        }
        return images.stream().filter(StringUtils::hasText).collect(Collectors.toList());
    }

    private Integer normalizeScore(Integer score) {
        if (score == null) {
            return null;
        }
        if (score < 1) {
            return 1;
        }
        return Math.min(score, 5);
    }
}
