package com.sq.system.usercore.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.usercore.entity.UserToRoleEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("system")
public interface UserToRoleRepository extends BaseMapper<UserToRoleEntity> {

}
