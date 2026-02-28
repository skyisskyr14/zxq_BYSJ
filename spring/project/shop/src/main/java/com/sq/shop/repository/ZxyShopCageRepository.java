package com.sq.shop.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.shop.entity.ZxyShopCageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("zxq")
public interface ZxyShopCageRepository extends BaseMapper<ZxyShopCageEntity> {

    @Select("SELECT * FROM shop_cage WHERE shop_id = #{shopId} AND (is_delete = 0 OR is_delete IS NULL) ORDER BY id DESC")
    List<ZxyShopCageEntity> listByShopId(Long shopId);

    @Select("SELECT * FROM shop_cage WHERE id = #{id} AND shop_id = #{shopId} AND (is_delete = 0 OR is_delete IS NULL) LIMIT 1")
    ZxyShopCageEntity getByIdAndShopId(Long id, Long shopId);

    @Select("SELECT * FROM shop_cage WHERE code = #{code} AND shop_id = #{shopId} AND (is_delete = 0 OR is_delete IS NULL) LIMIT 1")
    ZxyShopCageEntity getByCode(String code, Long shopId);
}
