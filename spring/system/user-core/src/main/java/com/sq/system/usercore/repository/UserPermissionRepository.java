package com.sq.system.usercore.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.usercore.entity.UserPermissionEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserPermissionRepository extends BaseMapper<UserPermissionEntity> {
    @Select("<script>" +
            "SELECT perm_code FROM user_permission " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<String> selectPermCodesByIds(@Param("ids") List<Long> ids);

    @Insert("INSERT INTO user_permission (perm_code, perm_name, module, type, sort, description) " +
            "VALUES (#{permCode}, #{permName}, #{module}, #{type}, #{sort}, #{description})")
    int insert(UserPermissionEntity permission);

    @Update("UPDATE user_permission SET perm_code = #{permCode}, perm_name = #{permName}, " +
            "module = #{module}, type = #{type}, sort = #{sort}, description = #{description} " +
            "WHERE id = #{id}")
    void update(UserPermissionEntity permission);

    @Delete("DELETE FROM user_permission WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
