package com.sq.shop.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.shop.entity.ZxyShopEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
@DS("zxq")
public interface ZxyShopRepository extends BaseMapper<ZxyShopEntity> {

    @Select("SELECT * FROM shop WHERE sys_id = #{sysId}")
    ZxyShopEntity getShopBySysId(Long sysId);
}
