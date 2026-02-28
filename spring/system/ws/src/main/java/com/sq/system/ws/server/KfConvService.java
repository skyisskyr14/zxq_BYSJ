package com.sq.system.ws.server;

import com.sq.system.ws.entity.KfConversation;
import com.sq.system.ws.repository.KfConversationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class KfConvService {

    private final KfConversationRepository convRepo;

    public KfConvService(KfConversationRepository convRepo) {
        this.convRepo = convRepo;
    }

    public static final class ConvDto {
        private Long id;
        private Long deptId;
        private Long agentId;
        private Long userId;

        public ConvDto() {}
        public ConvDto(Long id, Long deptId, Long agentId, Long userId) {
            this.id = id; this.deptId = deptId; this.agentId = agentId; this.userId = userId;
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getDeptId() { return deptId; }
        public void setDeptId(Long deptId) { this.deptId = deptId; }
        public Long getAgentId() { return agentId; }
        public void setAgentId(Long agentId) { this.agentId = agentId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }

    public static final class CreateConvResult {
        private ConvDto conv;
        private boolean existed;

        public CreateConvResult() {}
        public CreateConvResult(ConvDto conv, boolean existed) {
            this.conv = conv; this.existed = existed;
        }
        public ConvDto getConv() { return conv; }
        public void setConv(ConvDto conv) { this.conv = conv; }
        public boolean isExisted() { return existed; }
        public void setExisted(boolean existed) { this.existed = existed; }
    }

    private ConvDto toDto(KfConversation c) {
        return new ConvDto(c.getId(), c.getDeptId(), c.getAgentId(), c.getUserId());
    }

    /**
     * 先查（deptId, agentId, userId）是否存在，存在则直接返回 existed=true；
     * 不存在则创建一条并返回 existed=false。
     */
    @Transactional
    public CreateConvResult getOrCreate(Long deptId, Long agentId, Long userId) {
        var hit = convRepo.findByDeptIdAndAgentIdAndUserId(deptId, agentId, userId);
        if (hit.isPresent()) {
            return new CreateConvResult(toDto(hit.get()), true);
        }
        // 不存在就新建
        var entity = new KfConversation();
        entity.setDeptId(deptId);
        entity.setAgentId(agentId);
        entity.setUserId(userId);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setIsOpen(true);
        entity.setUnreadForAgent(0);
        entity.setUnreadForUser(0);
        // 如果实体里还有 createTime/updateTime 之类的，交给 @PrePersist 或 DB 默认值即可

        var saved = convRepo.save(entity);
        return new CreateConvResult(toDto(saved), false);
    }
}
