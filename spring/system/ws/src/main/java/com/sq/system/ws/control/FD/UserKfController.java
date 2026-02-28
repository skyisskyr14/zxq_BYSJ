package com.sq.system.ws.control.FD;


import com.sq.system.common.result.ResponseResult;
import com.sq.system.ws.entity.KfAgent;
import com.sq.system.ws.entity.KfConversation;
import com.sq.system.ws.entity.KfDept;
import com.sq.system.ws.entity.KfNormalQEntity;
import com.sq.system.ws.repository.KfAgentRepository;
import com.sq.system.ws.repository.KfConversationRepository;
import com.sq.system.ws.repository.KfDeptRepository;
import com.sq.system.ws.repository.KfNormalQRepository;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kf/FD")
public class UserKfController {

    @Resource
    private KfDeptRepository kfDeptRepository;
    @Resource
    private KfAgentRepository kfAgentRepository;
    @Resource
    private KfNormalQRepository kfNormalQRepository;
    @Resource
    private KfConversationRepository kfConversationRepository;

    @GetMapping("/info")
    public ResponseResult<?> buyCode(@RequestParam(value = "id", required = false) Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 部门
        List<KfDept> list = kfDeptRepository.findAll();
        result.put("type", list);

        // 客服（按你前端期望结构映射）
        List<KfAgent> list1 = kfAgentRepository.findAll();
        List<Map<String,Object>> info = new ArrayList<>();
        for (KfAgent kfAgent : list1) {
            Map<String,Object> map1 = new HashMap<>();
            map1.put("id",   kfAgent.getId());
            map1.put("name", kfAgent.getNickname());
            map1.put("type", kfAgent.getDeptId());
            map1.put("head", kfAgent.getHead());
            info.add(map1);
        }
        result.put("info", info);

        // FAQ
        List<KfNormalQEntity> list2 = kfNormalQRepository.selectList(null);
        result.put("normal", list2);

        // ===== 新增：该客服的会话 + 未读 =====
        // 只有当前端带 id（客服ID）时才查询
        if (userId != null) {
            // A) 未读聚合
            List<KfConversation> unreadRows = kfConversationRepository.findUnreadByUserId(userId);
            // key: dept:agent:user
            List<Map<String,Object>> unread = new ArrayList<>();
            for (KfConversation r : unreadRows) {
                Map<String, Object> unreadMap = new HashMap<>();
                Long deptId = r.getDeptId();
                Long agentId = r.getAgentId();
                Integer unreadNum = r.getUnreadForUser();
                unreadMap.put("deptId", deptId);
                unreadMap.put("agentId", agentId);
                unreadMap.put("unreadNum",unreadNum);
                unread.add(unreadMap);
            }
            result.put("unread", unread);
        }

        return ResponseResult.success(result);
    }


    @Transactional
    @PostMapping("/conv/markRead")
    public ResponseResult<?> markRead(@RequestParam Long deptId,
                                      @RequestParam Long agentId,
                                      @RequestParam Long userId) {
        return kfConversationRepository.findByDeptIdAndAgentIdAndUserId(deptId, agentId, userId)
                .map(c -> {
                    kfConversationRepository.clearUserUnread(c.getId());
                    return ResponseResult.success("ok");
                })
                .orElseGet(() -> ResponseResult.success("ok")); // 没有会话也算成功
    }


}
