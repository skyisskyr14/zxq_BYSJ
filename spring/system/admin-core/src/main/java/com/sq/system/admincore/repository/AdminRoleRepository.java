package com.sq.system.admincore.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.admincore.entity.AdminRoleEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
@DS("system")
public interface AdminRoleRepository extends BaseMapper<AdminRoleEntity> {

    @Select("SELECT role_id FROM admin_to_role WHERE admin_id = #{adminId}")
    List<Long> selectRoleIdsByAdminId(Long adminId);

    @Delete("DELETE FROM admin_to_role WHERE admin_id = #{adminId}")
    void deleteByAdminId(@Param("adminId") Long adminId);

    @Insert({
            "<script>",
            "INSERT INTO admin_to_role (admin_id, role_id) VALUES",
            "<foreach collection='roleIds' item='roleId' separator=','>",
            "(#{adminId}, #{roleId})",
            "</foreach>",
            "</script>"
    })
    void insertBatch(@Param("adminId") Long adminId, @Param("roleIds") List<Long> roleIds);
}
