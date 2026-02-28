package com.sq.system.admincore.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.admincore.entity.PermissionEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
@DS("system")
public interface PermissionRepository extends BaseMapper<PermissionEntity> {

    @Select("<script>" +
            "SELECT perm_code FROM admin_permission " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<String> selectPermCodesByIds(@Param("ids") List<Long> ids);

    @Insert("INSERT INTO admin_permission (perm_code, perm_name, module, type, sort, description) " +
            "VALUES (#{permCode}, #{permName}, #{module}, #{type}, #{sort}, #{description})")
    int insert(PermissionEntity permission);

    @Update("UPDATE admin_permission SET perm_code = #{permCode}, perm_name = #{permName}, " +
            "module = #{module}, type = #{type}, sort = #{sort}, description = #{description} " +
            "WHERE id = #{id}")
    void update(PermissionEntity permission);

    @Delete("DELETE FROM admin_permission WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
