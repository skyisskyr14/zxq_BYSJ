// com.skyissky.ws.api.ChatHistoryController
package com.sq.system.ws.control;

import com.sq.system.ws.repository.KfConversationRepository;
import com.sq.system.ws.server.ChatQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kf/history")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatQueryService query;
    private final KfConversationRepository convRepo;

    /** 方式A：三元定位会话（部门/客服/用户） */
    @GetMapping("/find")
    public R<?> historyByTriple(@RequestParam Long deptId,
                                @RequestParam Long agentId,
                                @RequestParam Long userId,
                                @RequestParam(required = false) Long before,
                                @RequestParam(defaultValue = "20") Integer size) {
        int pageSize = Math.min(Math.max(size, 1), 100);
        var data = query.getHistoryByTriple(deptId, agentId, userId, before, pageSize);
        return R.ok(data);
    }

    /** 方式B：convId 直接查 */
    @GetMapping("/find/{convId}")
    public R<?> historyByConv(@PathVariable Long convId,
                              @RequestParam(required = false) Long before,
                              @RequestParam(defaultValue = "20") Integer size) {
        var conv = convRepo.findById(convId).orElse(null);
        if (conv == null) return R.ok(ChatQueryService.HistoryResult.emptyFor(null, null, null));
        int pageSize = Math.min(Math.max(size, 1), 100);
        var data = query.getHistoryByConvId(conv, before, pageSize);
        return R.ok(data);
    }

    /* 简单统一返回结构 */
    @lombok.Data @lombok.AllArgsConstructor
    static class R<T> {
        private int code; private String message; private T data;
        static <T> R<T> ok(T d) { return new R<>(200, "OK", d); }
    }
}
