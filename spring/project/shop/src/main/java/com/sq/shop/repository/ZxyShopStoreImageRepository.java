package com.sq.shop.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.shop.entity.ZxyShopStoreImageEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("zxq")
public interface ZxyShopStoreImageRepository extends BaseMapper<ZxyShopStoreImageEntity> {

    @Select("SELECT * FROM shop_store_image WHERE store_id = #{storeId} ORDER BY sort ASC, id ASC")
    List<ZxyShopStoreImageEntity> listByStoreId(Long storeId);

    @Delete("DELETE FROM shop_store_image WHERE store_id = #{storeId}")
    int deleteByStoreId(Long storeId);
}
