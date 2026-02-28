package com.sq.shop.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.shop.entity.ZxyShopStoreEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("zxq")
public interface ZxyShopStoreRepository extends BaseMapper<ZxyShopStoreEntity> {

    @Select("SELECT * FROM shop_store WHERE shop_id = #{shopId} AND (is_delete = 0 OR is_delete IS NULL) LIMIT 1")
    ZxyShopStoreEntity getByShopId(Long shopId);

    @Select("SELECT * FROM shop_store WHERE id = #{id} AND (is_delete = 0 OR is_delete IS NULL) LIMIT 1")
    ZxyShopStoreEntity getByIdValid(Long id);

    @Select("SELECT * FROM shop_store WHERE (is_delete = 0 OR is_delete IS NULL) ORDER BY update_time DESC, id DESC")
    List<ZxyShopStoreEntity> listAll();
}
