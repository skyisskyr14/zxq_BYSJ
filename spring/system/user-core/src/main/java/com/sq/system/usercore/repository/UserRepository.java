package com.sq.system.usercore.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.usercore.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
@DS("system")
public interface UserRepository extends BaseMapper<UserEntity> {

    @Select("SELECT * FROM user WHERE username = #{username} LIMIT 1")
    UserEntity findByUsername(String username);

    @Select("SELECT * FROM user WHERE id = #{userId} LIMIT 1")
    UserEntity findIdById(Long userId);

    @Select("SELECT * FROM user WHERE phone = #{phone} LIMIT 1")
    UserEntity findByPhone(String phone);

    @Select("SELECT * FROM user WHERE email = #{email} LIMIT 1")
    UserEntity findByEmail(String email);

}
