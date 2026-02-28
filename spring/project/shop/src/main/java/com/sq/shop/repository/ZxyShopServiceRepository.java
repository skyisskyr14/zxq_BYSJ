package com.sq.shop.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.shop.entity.ZxyShopServiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("zxq")
public interface ZxyShopServiceRepository extends BaseMapper<ZxyShopServiceEntity> {

    @Select("SELECT * FROM shop_service WHERE shop_id = #{shopId} AND (is_delete = 0 OR is_delete IS NULL) ORDER BY id DESC")
    List<ZxyShopServiceEntity> listByShopId(Long shopId);

    @Select("SELECT * FROM shop_service WHERE id = #{id} AND shop_id = #{shopId} AND (is_delete = 0 OR is_delete IS NULL) LIMIT 1")
    ZxyShopServiceEntity getByIdAndShopId(Long id, Long shopId);
}
