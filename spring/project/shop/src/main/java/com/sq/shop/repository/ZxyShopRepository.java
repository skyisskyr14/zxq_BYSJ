package com.sq.shop.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.shop.entity.ZxyShopEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("zxq")
public interface ZxyShopRepository extends BaseMapper<ZxyShopEntity> {

    @Select("SELECT * FROM shop WHERE sys_id = #{sysId} AND (is_delete = 0 OR is_delete IS NULL) LIMIT 1")
    ZxyShopEntity getShopBySysId(Long sysId);

    @Select("SELECT * FROM shop WHERE (is_delete = 0 OR is_delete IS NULL) ORDER BY create_time DESC")
    List<ZxyShopEntity> listValidShops();
}
