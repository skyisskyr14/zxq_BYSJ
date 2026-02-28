package com.sq.system.ws.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.system.ws.entity.KfNormalQEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface KfNormalQRepository extends BaseMapper<KfNormalQEntity> {
    @Select("SELECT * FROM kf_normal_q WHERE id = #{id}")
    KfNormalQEntity selectById(Long id);

    @Select("SELECT * FROM kf_normal_q WHERE user_id = #{id} && type =0")
    KfNormalQEntity selectType0ByUserId(Long id);
}
