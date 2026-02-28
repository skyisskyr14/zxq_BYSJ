package com.sq.system.usercore.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.usercore.entity.UserRolePermissionEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserRolePermissionRepository extends BaseMapper<UserRolePermissionEntity> {

    @Select("SELECT permission_id FROM user_role_permission WHERE role_id = #{roleId}")
    List<Long> selectPermIdByRoleId(int roleId);

    @Delete("DELETE FROM user_role_permission WHERE role_id = #{roleId}")
    void deleteByRoleId(int roleId);

    @Insert({
            "<script>",
            "INSERT INTO user_role_permission (role_id, permission_id) VALUES",
            "<foreach collection='permIds' item='permId' separator=','>",
            "(#{roleId}, #{permId})",
            "</foreach>",
            "</script>"
    })
    void insertBatch(int roleId, List<Long> permIds);
}
