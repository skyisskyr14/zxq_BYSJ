package com.sq.user.controller;

import com.sq.system.common.annotation.UserLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.common.utils.IpUtil;
import com.sq.system.framework.redis.UserIpAccessService;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.entity.UserOperationLogEntity;
import com.sq.user.dto.ZxyUserRegisterDto;
import com.sq.user.entity.ZxyUserEntity;
import com.sq.user.model.ZxyUserModel;
import com.sq.user.repository.ZxyUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import com.sq.system.security.context.UserTokenContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@RestController("ZxyUserController")
@RequestMapping("/fd/user")
@Tag(name = "SS--用户系统信息扩展接口")
public class ZxyUserController {

    @Resource
    private ZxyUserModel userModel;

    @Resource
    private UserIpAccessService userIpAccessService;
    @Autowired
    private ZxyUserRepository zxyUserRepository;

    @PostConstruct
    public void init() {
        System.out.println("Cont======[2/3 user] ZxyUserController 初始化完成 ======");
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ResponseResult<String> create(@RequestBody ZxyUserRegisterDto entity, HttpServletRequest request) {
        String ip = IpUtil.getIp(request);
        String uri = request.getRequestURI();

        UserOperationLogEntity log = new UserOperationLogEntity();
        log.setIpAddress(ip);
        log.setRequestUri(uri);
        log.setRequestMethod(request.getMethod());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setActionModule("user,system/user-core");
        log.setActionType("注册用户账号");
        log.setRequestParam(entity.getPhone());
        log.setCreateTime(LocalDateTime.now());

        Map<String,Object> result = userModel.create(entity,1L,ip);
        if((boolean) result.get("code")){
            log.setUserId((Long) result.get("mes"));
            log.setUserAccount(entity.getPhone());
            log.setResultStatus("成功");
            log.setResultMessage("注册成功");

            userIpAccessService.recordUserIpAccess(log, entity.getPhone());
            return ResponseResult.success("创建成功");
        }else{
            log.setUserId(null);
            log.setUserAccount(entity.getPhone());
            log.setResultStatus("失败");
            log.setResultMessage("注册失败" + result.get("mes"));

            userIpAccessService.recordUserIpAccess(log, entity.getPhone());
            return ResponseResult.fail((String) result.get("mes"));
        }

    }

    @PostMapping("/update")
    @UserLog(action = "用户修改个人基础信息", module = "user")
    @Operation(summary = "用户修改个人基础信息")
    public ResponseResult<?> update(@RequestParam(required = false) String nickname,
                                       @RequestParam(required = false) String avatar,
                                       @RequestParam(required = false) int gender) {
        UserEntity userEntity = UserTokenContextHolder.get();
        ZxyUserEntity zxyUserEntity = zxyUserRepository.getUserBySysId(userEntity.getId());


        if(nickname == null || nickname.isEmpty() || Objects.equals(userEntity.getNickname(), nickname)
        || avatar == null || avatar.isEmpty() || Objects.equals(zxyUserEntity.getAvatar(), avatar)
        || gender == -1 || zxyUserEntity.getGender() == gender) {
            return ResponseResult.fail("没有必要修改，因为一样~");
        }

        if(userModel.update(zxyUserEntity,nickname,avatar,gender)){
            return ResponseResult.success("修改成功");
        }else{
            return ResponseResult.success("修改失败");
        }
    }

//    @PostMapping("/delete")
//    public ResponseResult<Void> delete(@RequestParam Long id) {
//        userModel.delete(id);
//        return ResponseResult.success();
//    }

    @GetMapping("/baseInfo")
    @UserLog(action = "用户获取基础信息", module = "user")
    @Operation(summary = "用户获取基础信息")
    public ResponseResult<?> baseInfo() {
        UserEntity user = UserTokenContextHolder.get();
        ZxyUserEntity zxyUserEntity = zxyUserRepository.getUserBySysId(user.getId());

        return ResponseResult.success(userModel.getBaseInfo(user.getPhone(),zxyUserEntity));
    }

//    @GetMapping("/page")
//    public ResponseResult<Page<ZxyUserEntity>> page(@RequestParam Integer pageNum,
//                                                    @RequestParam Integer pageSize,
//                                                    @RequestParam(required = false) String phone,
//                                                    @RequestParam(required = false) String username,
//                                                    @RequestParam(required = false) Integer status) {
//        return ResponseResult.success(userModel.page(pageNum, pageSize, phone, username, status));
//    }
}