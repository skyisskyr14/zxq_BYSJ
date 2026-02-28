package com.sq.system.admincore.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.admincore.entity.ProjectEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
@DS("system")
public interface ProjectRepository extends BaseMapper<ProjectEntity> {

    @Select("SELECT * FROM system_project ORDER BY id ASC")
    List<ProjectEntity> selectAll();

    @Insert("INSERT INTO system_project (project_code, project_name, description) VALUES (#{projectCode}, #{projectName}, #{description})")
    int insert(ProjectEntity project);

    @Delete("DELETE FROM system_project WHERE id = #{id}")
    void deleteById(@Param("id") Long id);

    @Select({
            "<script>",
            "SELECT project_code FROM system_project",
            "WHERE id IN",
            "<foreach collection='id' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<String> findProjectCodeById(@Param("id") List<Long> id);
}
