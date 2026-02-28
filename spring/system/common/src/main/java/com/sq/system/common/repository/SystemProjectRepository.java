package com.sq.system.common.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.common.entity.SystemProjectEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

@Mapper
@DS("system")
public interface SystemProjectRepository extends BaseMapper<SystemProjectEntity> {
    @Select("SELECT id FROM system_project WHERE project_code = #{projectCode}")
    Long selectIdByProjectCode(@Param("projectCode") String projectCode);
}
