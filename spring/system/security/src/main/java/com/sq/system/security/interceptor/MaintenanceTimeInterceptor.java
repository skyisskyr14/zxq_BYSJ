package com.sq.system.security.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.system.common.result.ResponseResult;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

@Component
public class MaintenanceTimeInterceptor implements HandlerInterceptor {

    // 要限制的 URI 前缀，可以自己改
    private static final Set<String> BLOCK_PREFIXES = Set.of(
            "/ss/income_dl",
            "/ss/user-task/start",
            "/ss/user-task/pause",
            "/ss/user-task/get",
            "/ss/task/info"
    );

    // 判断是否在 1:00 ~ 4:00（北京时间）
    private boolean isInMaintenanceTime() {
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Shanghai"));
        LocalTime start = LocalTime.of(0, 0);
        LocalTime end   = LocalTime.of(5, 0);
        return !now.isBefore(start) && now.isBefore(end); // [00:00, 05:00)
    }

    private boolean needBlock(String uri) {
        // 只拦截你指定的那几类接口
        for (String prefix : BLOCK_PREFIXES) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI()
                .replaceFirst(request.getContextPath(), ""); // 去掉 /api 这种 context-path

        if (isInMaintenanceTime() && needBlock(uri)) {
            // 统一返回你的 ResponseResult 结构
            ResponseResult<?> result = ResponseResult.fail("系统维护中，暂不受理该操作（每天 1:00~5:00）");

            response.setStatus(200);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(result);

            try (PrintWriter out = response.getWriter()) {
                out.write(json);
                out.flush();
            }
            return false; // 拦截，不再继续后面的 Controller
        }

        return true;
    }
}
