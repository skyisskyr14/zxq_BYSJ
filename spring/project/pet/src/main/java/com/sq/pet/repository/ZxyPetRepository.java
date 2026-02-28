package com.sq.pet.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.pet.entity.ZxyPetEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("zxq")
public interface ZxyPetRepository extends BaseMapper<ZxyPetEntity> {

    @Select("SELECT * FROM pet WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<ZxyPetEntity> listByUserId(Long userId);

    @Select("SELECT * FROM pet WHERE id = #{id} AND user_id = #{userId} LIMIT 1")
    ZxyPetEntity getByIdAndUserId(Long id, Long userId);
}
