package com.sq.user.model;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sq.user.entity.ZxyUserProfileEntity;
import com.sq.user.repository.ZxyUserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ZxyUserProfileModel {

    private final ZxyUserProfileRepository userProfileRepository;

    public ZxyUserProfileEntity getByUserId(Long userId) {
        return userProfileRepository.selectOne(
                Wrappers.lambdaQuery(ZxyUserProfileEntity.class)
                        .eq(ZxyUserProfileEntity::getUserId, userId)
                        .last("limit 1")
        );
    }

    /**
     * upsert：有就更新，没有就插入
     */
    public void saveOrUpdateByUserId(ZxyUserProfileEntity entity) {
        if (entity.getUserId() == null) {
            throw new IllegalArgumentException("userId不能为空");
        }
        ZxyUserProfileEntity db = getByUserId(entity.getUserId());
        if (db == null) {
            userProfileRepository.insert(entity);
        } else {
            entity.setId(db.getId());
            userProfileRepository.updateById(entity);
        }
    }

    public void delete(Long id) {
        userProfileRepository.deleteById(id);
    }
}