package com.sq.system.ws.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "kf_agent", indexes = {
        @Index(name = "idx_agent_user", columnList = "user_id"),
        @Index(name = "idx_agent_dept", columnList = "dept_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KfAgent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;            // 后台登录账号ID

    @Column(name="dept_id", nullable = false)
    private Long deptId;

    @Column(length = 64)
    private String nickname;

    @Column(nullable = false)
    private Integer status;

    private String head;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
