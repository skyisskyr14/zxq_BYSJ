package com.sq.system.ws.control.RD;

import com.sq.system.ws.server.KfConvService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kf/conv")
public class KfConvController {

    private final KfConvService service;

    public KfConvController(KfConvService service) {
        this.service = service;
    }

    // --- 请求/响应包裹，保持你之前的 {code,message,data} 形状 ---
    public static final class ApiResponse<T> {
        private int code;
        private String message;
        private T data;

        public ApiResponse() {}
        public ApiResponse(int code, String message, T data) {
            this.code = code; this.message = message; this.data = data;
        }
        public static <T> ApiResponse<T> ok(T data) {
            return new ApiResponse<>(200, "OK", data);
        }
        public static <T> ApiResponse<T> bad(String msg) {
            return new ApiResponse<>(400, msg, null);
        }
        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }

    // --- 入参 DTO（只支持 deptId/agentId/userId；按你的要求暂不接手机号） ---
    public static final class CreateConvReq {
        private Long deptId;
        private Long agentId;
        private Long userId;

        public Long getDeptId() { return deptId; }
        public void setDeptId(Long deptId) { this.deptId = deptId; }
        public Long getAgentId() { return agentId; }
        public void setAgentId(Long agentId) { this.agentId = agentId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }

    /**
     * 前端已对接：POST /api/kf/conv/create
     * 逻辑：
     *  - 存在窗口：返回 { existed: true,  conv: {id, deptId, agentId, userId} }
     *  - 不存在：   插入一条，返回 { existed: false, conv: {id, deptId, agentId, userId} }
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<KfConvService.CreateConvResult>> create(@RequestBody CreateConvReq req) {
        if (req == null || ObjectUtils.isEmpty(req.getDeptId())
                || ObjectUtils.isEmpty(req.getAgentId())
                || ObjectUtils.isEmpty(req.getUserId())) {
            return ResponseEntity.badRequest().body(ApiResponse.bad("deptId/agentId/userId 不能为空"));
        }
        var result = service.getOrCreate(req.getDeptId(), req.getAgentId(), req.getUserId());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
