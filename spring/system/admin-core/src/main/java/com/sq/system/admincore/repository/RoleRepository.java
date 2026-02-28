package com.sq.system.admincore.repository;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.admincore.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("system")
public interface RoleRepository extends BaseMapper<RoleEntity> {
    @Select({
            "<script>",
            "SELECT role_name FROM admin_role",
            "WHERE id IN",
            "<foreach collection='id' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<String> findRoleNameById(List<Long> id);

    @Select({
            "<script>",
            "SELECT role_code FROM admin_role",
            "WHERE id IN",
            "<foreach collection='id' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<String> findRoleCodesByids(List<Long> id);
}
