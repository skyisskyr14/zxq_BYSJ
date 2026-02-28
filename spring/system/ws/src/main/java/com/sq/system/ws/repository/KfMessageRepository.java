package com.sq.system.ws.repository;


import com.sq.system.ws.entity.KfMessage;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface KfMessageRepository extends JpaRepository<KfMessage, Long> {

    // 最新 N 条（进入会话首屏）
    List<KfMessage> findTop50ByConvIdOrderByIdDesc(Long convId);

    // 继续上拉：锚点 beforeId 之上的历史
    List<KfMessage> findTop50ByConvIdAndIdLessThanOrderByIdDesc(Long convId, Long beforeId);

    List<KfMessage> findKfMessageByConvId(Long convId);

    @Query("select coalesce(max(m.serverMsgId), -1) from KfMessage m where m.convId = :convId")
    Long maxServerMsgId(@Param("convId") Long convId);

    Optional<KfMessage> findByClientMsgId(String clientMsgId);

    @Query("""
           select m from KfMessage m
           where m.convId = :convId
             and (:before is null or m.serverMsgId < :before)
           order by m.serverMsgId desc
           """)
    List<KfMessage> fetchHistory(
            @Param("convId") Long convId,
            @Param("before") Long before,
            Pageable pageable
    );

}
