package com.sq.user.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.user.entity.ZxyUserProfileEntity;
import org.apache.ibatis.annotations.Mapper;


@Mapper
@DS("zxq")
public interface ZxyUserProfileRepository extends BaseMapper<ZxyUserProfileEntity> {
}