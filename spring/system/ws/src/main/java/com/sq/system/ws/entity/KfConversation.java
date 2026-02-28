package com.sq.system.ws.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "kf_conversation", uniqueConstraints = {
        @UniqueConstraint(name="uk_conv", columnNames = {"dept_id","agent_id","user_id"})
}, indexes = {
        @Index(name="idx_conv_agent", columnList = "dept_id,agent_id,last_msg_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KfConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="dept_id", nullable = false)
    private Long deptId;

    @Column(name="agent_id", nullable = false)
    private Long agentId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="last_msg_at")
    private LocalDateTime lastMsgAt;

    @Column(name="last_preview", length = 256)
    private String lastPreview;

    @Column(name="unread_for_agent", nullable = false)
    private Integer unreadForAgent;

    @Column(name="unread_for_user", nullable = false)
    private Integer unreadForUser;

    @Column(name="is_open", nullable = false)
    private Boolean isOpen;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
