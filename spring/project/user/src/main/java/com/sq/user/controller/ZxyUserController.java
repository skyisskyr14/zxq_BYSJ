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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

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

    @Value("${app.user.avatar.base-dir}")
    private String userAvatarBaseDir;

    @Value("${app.user.avatar.public-host}")
    private String userAvatarPublicHost;

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
                                       @RequestParam(required = false) Integer gender) {
        UserEntity userEntity = UserTokenContextHolder.get();
        ZxyUserEntity zxyUserEntity = zxyUserRepository.getUserBySysId(userEntity.getId());

        if (zxyUserEntity == null) {
            return ResponseResult.fail("用户不存在");
        }

        if (userModel.updateBaseInfo(zxyUserEntity, nickname, avatar, gender)) {
            return ResponseResult.success("修改成功");
        }
        return ResponseResult.fail("没有必要修改，因为一样~");
    }

    @PostMapping(value = "/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @UserLog(action = "用户上传头像", module = "user")
    @Operation(summary = "用户上传头像")
    public ResponseResult<?> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseResult.fail("请选择头像文件");
        }

        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !contentType.startsWith("image/")) {
            return ResponseResult.fail("仅支持图片文件");
        }

        UserEntity user = UserTokenContextHolder.get();
        ZxyUserEntity zxyUserEntity = zxyUserRepository.getUserBySysId(user.getId());
        if (zxyUserEntity == null) {
            return ResponseResult.fail("用户不存在");
        }

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path dir = Paths.get(userAvatarBaseDir, String.valueOf(user.getId()), datePath);
        Files.createDirectories(dir);

        String ext = "";
        String originalName = file.getOriginalFilename();
        if (StringUtils.hasText(originalName) && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf('.'));
        }

        String filename = "avatar_" + UUID.randomUUID().toString().replace("-", "") + ext;
        Path dest = dir.resolve(filename);
        file.transferTo(dest);

        String relative = String.join("/", String.valueOf(user.getId()), datePath, filename).replace("\\", "/");
        String avatarUrl = userAvatarPublicHost.replaceAll("/$", "") + "/" + relative;

        zxyUserEntity.setAvatar(avatarUrl);
        zxyUserEntity.setUpdateTime(LocalDateTime.now());
        zxyUserRepository.updateById(zxyUserEntity);

        return ResponseResult.success(Map.of("avatar", avatarUrl));
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