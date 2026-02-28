package com.sq.system.authapp.controller.usercore;

import com.sq.system.captcha.support.CaptchaDispatcher;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.annotation.UserLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.common.utils.CookieUtil;
import com.sq.system.common.utils.IpUtil;
import com.sq.system.framework.redis.UserIpAccessService;
import com.sq.system.framework.redis.UserTokenService;
import com.sq.system.security.context.UserTokenContextHolder;
import com.sq.system.usercore.dto.UserChangeDto;
import com.sq.system.usercore.dto.UserLoginDTO;
import com.sq.system.usercore.dto.UserRegisterDTO;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.entity.UserOperationLogEntity;
import com.sq.system.usercore.model.UserAuthModel;
import com.sq.system.usercore.repository.UserRepository;
import com.sq.system.usercore.repository.UserToProjectRepository;
import com.sq.system.usercore.repository.UserToRoleRepository;
import com.sq.system.usercore.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/user")
@Tag(name = "用户认证接口")
public class UserAuthController {

    private final UserAuthModel model;

    @Resource
    private UserRepository userRepository;
    @Resource
    private CaptchaDispatcher captchaDispatcher;
    @Resource
    private UserIpAccessService userIpAccessService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private UserToRoleRepository userToRoleRepository;

//    @Resource
//    private RedisSubAccountTimerService redisSubAccountTimerService;

    public UserAuthController(UserRepository userRepository,
                              CaptchaDispatcher captchaDispatcher,
                              UserToProjectRepository userToProjectRepository,
                              UserToRoleRepository userToRoleRepository) {
        this.model = new UserAuthModel(userRepository, captchaDispatcher, userToProjectRepository, userToRoleRepository);
    }

    @PostConstruct
    public void init() {
        System.out.println("Cont======[1/2 system/authapp/user] UserAuthController 初始化完成 ======");
    }

    @GetMapping("/auth-token")
    @Operation(summary = "用户自动登录验证")
    public ResponseResult<String> autnToken(HttpServletResponse response, HttpServletRequest request) {
        String ip = IpUtil.getIp(request);
        String uri = request.getRequestURI();

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("token".equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }
        }

        boolean isToken = userTokenService.exists(token);
        if (isToken) {
            UserOperationLogEntity log = new UserOperationLogEntity();
            log.setIpAddress(ip);
            log.setRequestUri(uri);
            log.setRequestMethod(request.getMethod());
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setActionModule("用户核心");
            log.setActionType("登录");
            log.setRequestParam(null);
            log.setCreateTime(LocalDateTime.now());
            UserEntity user = userTokenService.getUserByToken(token);
            log.setUserId(user.getId());
            log.setUserAccount(user.getUsername());
            log.setResultStatus("成功");
            log.setResultMessage("yes");

            userIpAccessService.recordUserIpAccess(log, token);
            CookieUtil.setLoginCookies(response, token, user.getId());
            return ResponseResult.success("yes");
        } else {
            return ResponseResult.success("no");
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ResponseResult<String> register(@RequestBody UserRegisterDTO dto) {
        String status = model.register(dto, 1);
        if ("注册成功".equals(status)) {
            return ResponseResult.success("注册成功");
        } else {
            return ResponseResult.fail(status);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseResult<?> login(@RequestBody UserLoginDTO dto, HttpServletResponse response, HttpServletRequest request) {
        String ip = IpUtil.getIp(request);
        String uri = request.getRequestURI();

        UserLoginVO vo = model.login(dto,ip);
        UserEntity user = vo.getUser();

        UserOperationLogEntity log = new UserOperationLogEntity();
        log.setIpAddress(ip);
        log.setRequestUri(uri);
        log.setRequestMethod(request.getMethod());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setActionModule("system/auth-app");
        log.setActionType("登录");
        log.setRequestParam(null);
        log.setCreateTime(LocalDateTime.now());

        if (vo.getToken() == null) {
            log.setUserId(null);
            log.setUserAccount(null);
            log.setResultStatus("失败");
            log.setResultMessage(vo.getStatus());
            userIpAccessService.recordUserIpAccess(log, null);
            return ResponseResult.fail(vo.getStatus());
        } else {
            log.setUserId(user.getId());
            log.setUserAccount(user.getUsername());
            log.setResultStatus("成功");
            log.setResultMessage(vo.getToken());

            if(userTokenService.existsDelete(user.getId())) {

            }

            userIpAccessService.recordUserIpAccess(log, vo.getToken());
            userTokenService.saveToken(vo.getToken(), user);
            // ✅ 使用工具类设置 Cookie
            CookieUtil.setLoginCookies(response, vo.getToken(), user.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("message", "登录成功");
            result.put("token", vo.getToken());
            return ResponseResult.success(result);
        }
    }

    @GetMapping("/logout")
    @UserLog(module = "system/auth-app",action = "用户退出登录")
    @Operation(summary = "用户退出登录")
    public ResponseResult<String> logout(HttpServletResponse response, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("token".equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }
        }

        if (token == null) {
            return ResponseResult.fail("token不存在");
        } else {
            userTokenService.removeToken(token);
            // ✅ 使用工具类清除 Cookie
            CookieUtil.clearLoginCookies(response);
            return ResponseResult.success("退出登录成功");
        }
    }

    @PostMapping("/change")
    @UserLog(module = "用户核心",action = "用户修改密码和安全密码")
    @Operation(summary = "用户修改密码和安全密码")
    public ResponseResult<String> change(@RequestBody UserChangeDto dto) {
        UserEntity user = UserTokenContextHolder.get();
        if(user == null) {
            return ResponseResult.fail("用户不存在");
        }

        if(dto.getNewPassword() == null || dto.getNewPassword().isEmpty()) {
            return ResponseResult.fail("未知新密码");
        }

        if(dto.getOldPassword() == null || dto.getNewPassword().isEmpty()) {
            return ResponseResult.fail("未知旧密码");
        }

        if(dto.getType() == null) {
            return  ResponseResult.fail("未知修改类型");
        }

        if(dto.getType() == 1){
            if(!dto.getOldPassword().equals(user.getPassword())){
                return ResponseResult.fail("旧密码错误");
            }else{
                user.setPassword(dto.getNewPassword());
                userRepository.updateById(user);
            }
        }else{
            if(!dto.getOldPassword().equals(user.getSecurePassword())){
                return ResponseResult.fail("旧密码错误");
            }else{
                user.setSecurePassword(dto.getNewPassword());
                userRepository.updateById(user);
            }
        }

        return ResponseResult.success("修改密码成功");
    }

    @GetMapping("/seal")
    @AdminLog(module = "FANS_用户信息", action = "封禁用户")
    @Operation(summary = "封禁用户")
    public ResponseResult<?> sealSystemUser(@RequestParam Long id) {
        UserEntity user = userRepository.selectById(id);

        if(user == null) {
            return ResponseResult.fail("没有找到用户 ");
        }

        if(user.getStatus() == 1){
            user.setStatus(0);
            user.setUpdateTime(LocalDateTime.now());
            userRepository.updateById(user);

//            TaskLogWebSocket.sendToUserJson(fansUserRepository.selectByUserId(user.getId()).getId(), "LOGIN",
//                    "您的账号于" + LocalDateTime.now() + "因违规操作被封禁",
//                    "请及时联系您的上级或前往telegram联系客服咨询");

//            if(fansUser.getPlayStatue() == 1){
//                redisSubAccountTimerService.stop(fansUser.getId());
//            }

            return ResponseResult.success("封禁用户成功，强制下线并且停止其任务");
        }else{
            user.setStatus(1);
            user.setUpdateTime(LocalDateTime.now());
            userRepository.updateById(user);


            return ResponseResult.success("用户已经解除封禁");
        }

    }
}
