package com.sq.system.usercore.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.usercore.entity.UserRoleEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserRoleRepository extends BaseMapper<UserRoleEntity> {

    @Select("SELECT role_id FROM admin_role WHERE admin_id = #{adminId}")
    List<Long> selectRoleIdsByAdminId(Long adminId);

    @Delete("DELETE FROM admin_role WHERE admin_id = #{adminId}")
    void deleteByAdminId(@Param("adminId") Long adminId);

    @Insert({
            "<script>",
            "INSERT INTO admin_role (admin_id, role_id) VALUES",
            "<foreach collection='roleIds' item='roleId' separator=','>",
            "(#{adminId}, #{roleId})",
            "</foreach>",
            "</script>"
    })
    void insertBatch(@Param("adminId") Long adminId, @Param("roleIds") List<Long> roleIds);
}
