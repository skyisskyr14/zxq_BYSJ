package com.sq.system.admincore.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.admincore.entity.AdminUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
@DS("system")
public interface AdminUserRepository extends BaseMapper<AdminUserEntity> {

    @Select("SELECT * FROM admin WHERE username = #{username} LIMIT 1")
    AdminUserEntity findByUsername(String username); // 查找管理员

    @Update("UPDATE admin SET password = #{password} , nickname = #{nickname} ," +
            "status = #{status} , description = #{description} , " +
            "update_time = #{update_time} WHERE id = #{id}")
    int updateById(AdminUserEntity entity);

}
