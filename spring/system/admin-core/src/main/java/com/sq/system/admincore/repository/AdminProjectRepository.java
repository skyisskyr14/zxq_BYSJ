package com.sq.system.admincore.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.admincore.entity.AdminProjectEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
@DS("system")
public interface AdminProjectRepository extends BaseMapper<AdminProjectEntity> {

    @Select("SELECT project_id FROM admin_to_project WHERE admin_id = #{adminId}")
    List<Long> selectProjectIdsByAdminId(@Param("adminId") Long adminId);

    @Delete("DELETE FROM admin_to_project WHERE admin_id = #{adminId}")
    void deleteByAdminId(@Param("adminId") Long adminId);

    @Insert({
            "<script>",
            "INSERT INTO admin_to_project (admin_id, project_id) VALUES",
            "<foreach collection='projectIds' item='pid' separator=','>",
            "(#{adminId}, #{pid})",
            "</foreach>",
            "</script>"
    })
    void insertBatch(@Param("adminId") Long adminId, @Param("projectIds") List<Long> projectIds);
}
