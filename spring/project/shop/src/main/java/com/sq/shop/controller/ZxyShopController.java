package com.sq.shop.controller;

import com.sq.shop.dto.ZxyShopRegisterDto;
import com.sq.shop.dto.ZxyShopSaveDto;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

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

    @GetMapping("/baseInfo")
    @UserLog(action = "商户获取基础信息", module = "shop")
    @Operation(summary = "商户获取基础信息")
    public ResponseResult<?> baseInfo() {
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        return ResponseResult.success(shopModel.getBaseInfo(user.getUsername(),zxyShopEntity));
    }

    @PostMapping("/create")
    @UserLog(action = "商户新增基础信息", module = "shop")
    @Operation(summary = "商户新增基础信息")
    public ResponseResult<?> createBaseInfo(@RequestBody ZxyShopSaveDto dto) {
        String msg = validateDto(dto, true);
        if (msg != null) return ResponseResult.fail(msg);
        UserEntity user = UserTokenContextHolder.get();
        if (shopModel.createShopInfo(user.getId(), dto)) {
            return ResponseResult.success("创建成功");
        }
        return ResponseResult.fail("商家信息已存在或创建失败");
    }

    @PostMapping("/update")
    @UserLog(action = "商户修改个人基础信息", module = "shop")
    @Operation(summary = "商户修改个人基础信息")
    public ResponseResult<?> update(@RequestBody ZxyShopSaveDto dto) {
        String msg = validateDto(dto, false);
        if (msg != null) return ResponseResult.fail(msg);
        UserEntity userEntity = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(userEntity.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }

        if(shopModel.updateShopInfo(zxyShopEntity, dto)){
            return ResponseResult.success("修改成功");
        }else{
            return ResponseResult.fail("没有必要修改，因为一样~");
        }
    }

    @PostMapping("/delete")
    @UserLog(action = "商户删除基础信息", module = "shop")
    @Operation(summary = "商户删除基础信息")
    public ResponseResult<?> delete() {
        UserEntity userEntity = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(userEntity.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        if (shopModel.deleteShopInfo(zxyShopEntity)) {
            return ResponseResult.success("删除成功");
        }
        return ResponseResult.fail("删除失败");
    }

    @GetMapping("/list")
    @UserLog(action = "商户列表查询", module = "shop")
    @Operation(summary = "商户列表")
    public ResponseResult<?> list() {
        return ResponseResult.success(shopModel.listShops());
    }

    private String validateDto(ZxyShopSaveDto dto, boolean requireRealname) {
        if (requireRealname && !StringUtils.hasText(dto.getRealname())) {
            return "商家名称不能为空";
        }
        if (dto.getGender() != null && dto.getGender() < 0) {
            return "性别参数错误";
        }
        if (dto.getAge() != null && dto.getAge() < 0) {
            return "年龄参数错误";
        }
        return null;
    }
}
