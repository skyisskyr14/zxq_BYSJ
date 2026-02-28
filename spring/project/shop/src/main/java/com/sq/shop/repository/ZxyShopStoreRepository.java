package com.sq.shop.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.shop.entity.ZxyShopStoreEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
@DS("zxq")
public interface ZxyShopStoreRepository extends BaseMapper<ZxyShopStoreEntity> {

    @Select("SELECT * FROM shop_store WHERE shop_id = #{shopId} AND (is_delete = 0 OR is_delete IS NULL) LIMIT 1")
    ZxyShopStoreEntity getByShopId(Long shopId);
}
