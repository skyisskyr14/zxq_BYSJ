package com.sq.system.ws.repository;


import com.sq.system.ws.entity.KfAgent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KfAgentRepository extends JpaRepository<KfAgent, Long> {
    List<KfAgent> findByDeptId(Long deptId);
    List<KfAgent> findByUserId(Long userId);
    boolean existsByDeptIdAndNickname(Long deptId, String nickname);
}
