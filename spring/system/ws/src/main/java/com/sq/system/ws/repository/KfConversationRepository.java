package com.sq.system.ws.repository;

import com.sq.system.ws.entity.KfConversation;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface KfConversationRepository extends JpaRepository<KfConversation, Long> {

    Optional<KfConversation> findByDeptIdAndAgentIdAndUserId(Long deptId, Long agentId, Long userId);

    List<KfConversation> findByDeptIdAndAgentIdOrderByLastMsgAtDesc(Long deptId, Long agentId);

    List<KfConversation> findByDeptIdAndAgentIdAndUnreadForAgentGreaterThanOrderByLastMsgAtDesc(
            Long deptId, Long agentId, int minUnread);

    // ★ 新增：按多个 agentId 拉会话列表（用于左侧会话总列表）
    List<KfConversation> findByAgentIdInOrderByLastMsgAtDesc(List<Long> agentIds);



    @Modifying
    @Query("update KfConversation c set c.unreadForAgent = c.unreadForAgent + :delta, c.lastMsgAt = :lastAt, c.lastPreview = :preview where c.id = :id")
    int bumpUnreadAndTouch(@Param("id") Long convId,
                           @Param("delta") int delta,
                           @Param("lastAt") LocalDateTime lastAt,
                           @Param("preview") String preview);

    @Modifying
    @Query("update KfConversation c set c.unreadForAgent = 0 where c.id = :id")
    int clearAgentUnread(@Param("id") Long convId);

    @Modifying
    @Query("update KfConversation c set c.unreadForUser = 0 where c.id = :id")
    int clearUserUnread(@Param("id") Long convId);

    @Query("select c from KfConversation c where c.unreadForUser > 0 and c.userId = :id")
    List<KfConversation> findUnreadByUserId(@Param("id") Long id);
}
