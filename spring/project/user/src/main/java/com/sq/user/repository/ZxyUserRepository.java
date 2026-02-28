package com.sq.user.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.user.entity.ZxyUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
@DS("zxq")
public interface ZxyUserRepository extends BaseMapper<ZxyUserEntity> {
    @Select("SELECT * FROM user WHERE sys_id = #{sysId}")
    ZxyUserEntity getUserBySysId(Long sysId);
}