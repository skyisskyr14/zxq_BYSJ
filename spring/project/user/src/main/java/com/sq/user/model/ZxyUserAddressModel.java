package com.sq.user.model;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sq.user.entity.ZxyUserAddressEntity;
import com.sq.user.repository.ZxyUserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ZxyUserAddressModel {

    private final ZxyUserAddressRepository userAddressRepository;

    public Long create(ZxyUserAddressEntity entity) {
        if (entity.getUserId() == null) throw new IllegalArgumentException("userId不能为空");
        if (!StringUtils.hasText(entity.getContactName())) throw new IllegalArgumentException("contactName不能为空");
        if (!StringUtils.hasText(entity.getContactPhone())) throw new IllegalArgumentException("contactPhone不能为空");
        if (!StringUtils.hasText(entity.getDetail())) throw new IllegalArgumentException("detail不能为空");

        userAddressRepository.insert(entity);
        return entity.getId();
    }

    public void update(ZxyUserAddressEntity entity) {
        if (entity.getId() == null) throw new IllegalArgumentException("id不能为空");
        userAddressRepository.updateById(entity);
    }

    public void delete(Long id) {
        userAddressRepository.deleteById(id); // 若 @TableLogic 则逻辑删除
    }

    public ZxyUserAddressEntity getById(Long id) {
        return userAddressRepository.selectById(id);
    }

    public List<ZxyUserAddressEntity> listByUserId(Long userId) {
        return userAddressRepository.selectList(
                Wrappers.lambdaQuery(ZxyUserAddressEntity.class)
                        .eq(ZxyUserAddressEntity::getUserId, userId)
                        .orderByDesc(ZxyUserAddressEntity::getIsDefault)
                        .orderByDesc(ZxyUserAddressEntity::getId)
        );
    }

    public Page<ZxyUserAddressEntity> page(Integer pageNum, Integer pageSize, Long userId) {
        return userAddressRepository.selectPage(
                new Page<>(pageNum, pageSize),
                Wrappers.lambdaQuery(ZxyUserAddressEntity.class)
                        .eq(userId != null, ZxyUserAddressEntity::getUserId, userId)
                        .orderByDesc(ZxyUserAddressEntity::getIsDefault)
                        .orderByDesc(ZxyUserAddressEntity::getId)
        );
    }

    /**
     * 设置默认地址：同一用户只能有一个默认
     */
    public void setDefault(Long userId, Long addressId) {
        if (userId == null) throw new IllegalArgumentException("userId不能为空");
        if (addressId == null) throw new IllegalArgumentException("addressId不能为空");

        // 1) 清空该用户其它默认
        userAddressRepository.update(
                null,
                Wrappers.lambdaUpdate(ZxyUserAddressEntity.class)
                        .eq(ZxyUserAddressEntity::getUserId, userId)
                        .set(ZxyUserAddressEntity::getIsDefault, 0)
        );

        // 2) 设置指定地址为默认（确保属于该用户）
        int updated = userAddressRepository.update(
                null,
                Wrappers.lambdaUpdate(ZxyUserAddressEntity.class)
                        .eq(ZxyUserAddressEntity::getId, addressId)
                        .eq(ZxyUserAddressEntity::getUserId, userId)
                        .set(ZxyUserAddressEntity::getIsDefault, 1)
        );

        if (updated == 0) {
            throw new IllegalArgumentException("地址不存在或不属于该用户");
        }
    }
}