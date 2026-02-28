package com.sq.system.admincore.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.admincore.entity.RolePermissionEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
@DS("system")
public interface RolePermissionRepository extends BaseMapper<RolePermissionEntity> {

    @Select("<script>" +
            "SELECT permission_id FROM admin_role_to_permission " +
            "WHERE role_id IN " +
            "<foreach collection='roleIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Long> selectPermIdsByRoleIds(@Param("roleIds") List<Long> roleIds);

    @Select("SELECT permission_id FROM admin_role_to_permission WHERE role_id = #{roleId}")
    List<Long> selectPermIdsByRoleId(@Param("roleId") Long roleId);

    @Delete("DELETE FROM admin_role_to_permission WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Insert({
            "<script>",
            "INSERT INTO admin_role_to_permission (role_id, permission_id) VALUES",
            "<foreach collection='permIds' item='permId' separator=','>",
            "(#{roleId}, #{permId})",
            "</foreach>",
            "</script>"
    })
    void insertBatch(@Param("roleId") Long roleId, @Param("permIds") List<Long> permIds);
}
