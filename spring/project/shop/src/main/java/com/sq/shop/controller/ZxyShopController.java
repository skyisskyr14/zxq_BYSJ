package com.sq.shop.controller;

import com.sq.shop.dto.ZxyShopRegisterDto;
import com.sq.shop.entity.ZxyShopEntity;
import com.sq.shop.model.ZxyShopModel;
import com.sq.shop.repository.ZxyShopRepository;
import com.sq.system.common.annotation.UserLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.common.utils.IpUtil;
import com.sq.system.framework.redis.UserIpAccessService;
import com.sq.system.security.context.UserTokenContextHolder;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.entity.UserOperationLogEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@RestController("ZxyShopController")
@RequestMapping("/fd/shop")
@Tag(name = "SS--商户系统信息扩展接口")
public class ZxyShopController {

    @Resource
    private ZxyShopModel shopModel;

    @Resource
    private UserIpAccessService userIpAccessService;
    @Autowired
    private ZxyShopRepository zxyShopRepository;

    @PostConstruct
    public void init() {
        System.out.println("Cont======[1/1 shop] ZxyShopController 初始化完成 ======");
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ResponseResult<String> create(@RequestBody ZxyShopRegisterDto entity, HttpServletRequest request) {
        String ip = IpUtil.getIp(request);
        String uri = request.getRequestURI();

        UserOperationLogEntity log = new UserOperationLogEntity();
        log.setIpAddress(ip);
        log.setRequestUri(uri);
        log.setRequestMethod(request.getMethod());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setActionModule("shop,system/user-core");
        log.setActionType("注册用户账号");
        log.setRequestParam(entity.getPhone());
        log.setCreateTime(LocalDateTime.now());

        Map<String,Object> result = shopModel.create(entity,1L,ip);
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
    @UserLog(action = "商户修改个人基础信息", module = "shop")
    @Operation(summary = "商户修改个人基础信息")
    public ResponseResult<?> update(@RequestParam(required = false) String avatar,
                                    @RequestParam(required = false) int gender,
                                    @RequestParam(required = false) int age
                                            ) {
        UserEntity userEntity = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(userEntity.getId());


        if(avatar == null || avatar.isEmpty() || Objects.equals(zxyShopEntity.getAvatar(), avatar)
                || gender == -1 || zxyShopEntity.getGender() == gender
                || age == -1 || zxyShopEntity.getAge() == age) {
            return ResponseResult.fail("没有必要修改，因为一样~");
        }

        if(shopModel.update(zxyShopEntity,avatar,gender)){
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
    @UserLog(action = "商户获取基础信息", module = "shop")
    @Operation(summary = "商户获取基础信息")
    public ResponseResult<?> baseInfo() {
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());

        return ResponseResult.success(shopModel.getBaseInfo(user.getPhone(),zxyShopEntity));
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