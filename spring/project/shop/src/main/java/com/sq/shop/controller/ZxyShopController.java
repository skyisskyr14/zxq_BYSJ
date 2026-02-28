package com.sq.shop.controller;

import com.sq.shop.dto.ZxyShopRegisterDto;
import com.sq.shop.dto.ZxyShopSaveDto;
import com.sq.shop.entity.ZxyShopEntity;
import com.sq.shop.model.ZxyShopModel;
import com.sq.shop.model.ZxyShopStoreModel;
import com.sq.shop.model.ZxyShopCageModel;
import com.sq.shop.model.ZxyShopServiceModel;
import com.sq.shop.dto.ZxyShopStoreSaveDto;
import com.sq.shop.dto.ZxyShopCageSaveDto;
import com.sq.shop.dto.ZxyShopServiceSaveDto;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@RestController("ZxyShopController")
@RequestMapping("/fd/shop")
@Tag(name = "SS--商户系统信息扩展接口")
public class ZxyShopController {

    @Resource
    private ZxyShopModel shopModel;

    @Resource
    private UserIpAccessService userIpAccessService;

    @Resource
    private ZxyShopStoreModel shopStoreModel;

    @Resource
    private ZxyShopCageModel shopCageModel;

    @Resource
    private ZxyShopServiceModel shopServiceModel;
    @Autowired
    private ZxyShopRepository zxyShopRepository;

    @Value("${app.shop.avatar.base-dir}")
    private String shopAvatarBaseDir;

    @Value("${app.shop.avatar.public-host}")
    private String shopAvatarPublicHost;

    @Value("${app.shop.store-image.base-dir}")
    private String shopStoreImageBaseDir;

    @Value("${app.shop.store-image.public-host}")
    private String shopStoreImagePublicHost;

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





    @GetMapping("/store/detail")
    @UserLog(action = "商户获取门店配置", module = "shop")
    @Operation(summary = "商户获取门店配置")
    public ResponseResult<?> storeDetail() {
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        Object detail = shopStoreModel.detail(zxyShopEntity.getId());
        if (detail == null) {
            return ResponseResult.fail("门店配置不存在");
        }
        return ResponseResult.success(detail);
    }

    @PostMapping("/store/create")
    @UserLog(action = "商户新增门店配置", module = "shop")
    @Operation(summary = "商户新增门店配置")
    public ResponseResult<?> storeCreate(@RequestBody ZxyShopStoreSaveDto dto) {
        String msg = validateStoreDto(dto, true);
        if (msg != null) return ResponseResult.fail(msg);
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        if (shopStoreModel.create(zxyShopEntity.getId(), dto)) {
            return ResponseResult.success("创建成功");
        }
        return ResponseResult.fail("门店配置已存在或创建失败");
    }

    @PostMapping("/store/update")
    @UserLog(action = "商户修改门店配置", module = "shop")
    @Operation(summary = "商户修改门店配置")
    public ResponseResult<?> storeUpdate(@RequestBody ZxyShopStoreSaveDto dto) {
        String msg = validateStoreDto(dto, false);
        if (msg != null) return ResponseResult.fail(msg);
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        if (shopStoreModel.update(zxyShopEntity.getId(), dto)) {
            return ResponseResult.success("修改成功");
        }
        return ResponseResult.fail("没有必要修改，因为一样~");
    }

    @PostMapping("/store/delete")
    @UserLog(action = "商户删除门店配置", module = "shop")
    @Operation(summary = "商户删除门店配置")
    public ResponseResult<?> storeDelete() {
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        if (shopStoreModel.delete(zxyShopEntity.getId())) {
            return ResponseResult.success("删除成功");
        }
        return ResponseResult.fail("门店配置不存在或删除失败");
    }


    @GetMapping("/cage/list")
    @UserLog(action = "商户查询笼位列表", module = "shop")
    @Operation(summary = "商户查询笼位列表")
    public ResponseResult<?> cageList() {
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        return ResponseResult.success(shopCageModel.list(zxyShopEntity.getId()));
    }

    @PostMapping("/cage/create")
    @UserLog(action = "商户新增笼位", module = "shop")
    @Operation(summary = "商户新增笼位")
    public ResponseResult<?> cageCreate(@RequestBody ZxyShopCageSaveDto dto) {
        String msg = validateCageDto(dto, true);
        if (msg != null) return ResponseResult.fail(msg);
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        if (shopCageModel.create(zxyShopEntity.getId(), dto)) {
            return ResponseResult.success("创建成功");
        }
        return ResponseResult.fail("笼位编号重复或创建失败");
    }

    @PostMapping("/cage/update")
    @UserLog(action = "商户修改笼位", module = "shop")
    @Operation(summary = "商户修改笼位")
    public ResponseResult<?> cageUpdate(@RequestBody ZxyShopCageSaveDto dto) {
        String msg = validateCageDto(dto, false);
        if (msg != null) return ResponseResult.fail(msg);
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        if (shopCageModel.update(zxyShopEntity.getId(), dto)) {
            return ResponseResult.success("修改成功");
        }
        return ResponseResult.fail("笼位不存在、编号重复或无需修改");
    }

    @PostMapping("/cage/delete")
    @UserLog(action = "商户删除笼位", module = "shop")
    @Operation(summary = "商户删除笼位")
    public ResponseResult<?> cageDelete(@RequestParam Long id) {
        if (id == null || id <= 0) {
            return ResponseResult.fail("笼位ID错误");
        }
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        if (shopCageModel.delete(id, zxyShopEntity.getId())) {
            return ResponseResult.success("删除成功");
        }
        return ResponseResult.fail("笼位不存在或删除失败");
    }



    @GetMapping("/service/list")
    @UserLog(action = "商户查询服务列表", module = "shop")
    @Operation(summary = "商户查询服务列表")
    public ResponseResult<?> serviceList() {
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        return ResponseResult.success(shopServiceModel.list(zxyShopEntity.getId()));
    }

    @PostMapping("/service/create")
    @UserLog(action = "商户新增服务", module = "shop")
    @Operation(summary = "商户新增服务")
    public ResponseResult<?> serviceCreate(@RequestBody ZxyShopServiceSaveDto dto) {
        String msg = validateServiceDto(dto, true);
        if (msg != null) return ResponseResult.fail(msg);
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        if (shopServiceModel.create(zxyShopEntity.getId(), dto)) {
            return ResponseResult.success("创建成功");
        }
        return ResponseResult.fail("创建失败");
    }

    @PostMapping("/service/update")
    @UserLog(action = "商户修改服务", module = "shop")
    @Operation(summary = "商户修改服务")
    public ResponseResult<?> serviceUpdate(@RequestBody ZxyShopServiceSaveDto dto) {
        String msg = validateServiceDto(dto, false);
        if (msg != null) return ResponseResult.fail(msg);
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        if (shopServiceModel.update(zxyShopEntity.getId(), dto)) {
            return ResponseResult.success("修改成功");
        }
        return ResponseResult.fail("服务不存在或无需修改");
    }

    @PostMapping("/service/delete")
    @UserLog(action = "商户删除服务", module = "shop")
    @Operation(summary = "商户删除服务")
    public ResponseResult<?> serviceDelete(@RequestParam Long id) {
        if (id == null || id <= 0) {
            return ResponseResult.fail("服务ID错误");
        }
        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }
        if (shopServiceModel.delete(id, zxyShopEntity.getId())) {
            return ResponseResult.success("删除成功");
        }
        return ResponseResult.fail("服务不存在或删除失败");
    }

    @GetMapping("/store/public/list")
    @UserLog(action = "用户端查询门店列表", module = "shop")
    @Operation(summary = "用户端查询门店列表")
    public ResponseResult<?> storePublicList() {
        return ResponseResult.success(shopStoreModel.listAll());
    }

    @GetMapping("/store/public/detail/{id}")
    @UserLog(action = "用户端查询门店详情", module = "shop")
    @Operation(summary = "用户端查询门店详情")
    public ResponseResult<?> storePublicDetail(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseResult.fail("门店ID错误");
        }
        Object detail = shopStoreModel.detailById(id);
        if (detail == null) {
            return ResponseResult.fail("门店不存在");
        }
        return ResponseResult.success(detail);
    }

    @PostMapping(value = "/store/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @UserLog(action = "商户上传门店图片", module = "shop")
    @Operation(summary = "商户上传门店图片")
    public ResponseResult<?> storeImageUpload(@RequestParam("files") MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return ResponseResult.fail("请至少上传一张图片");
        }

        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path dir = Paths.get(shopStoreImageBaseDir, String.valueOf(user.getId()), datePath);
        Files.createDirectories(dir);

        java.util.List<String> urls = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String contentType = file.getContentType();
            if (!StringUtils.hasText(contentType) || !contentType.startsWith("image/")) {
                return ResponseResult.fail("仅支持图片文件");
            }

            String ext = "";
            String originalName = file.getOriginalFilename();
            if (StringUtils.hasText(originalName) && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf('.'));
            }

            String filename = "store_" + UUID.randomUUID().toString().replace("-", "") + ext;
            Path dest = dir.resolve(filename);
            file.transferTo(dest);
            String relative = String.join("/", String.valueOf(user.getId()), datePath, filename).replace("\\", "/");
            urls.add(shopStoreImagePublicHost.replaceAll("/$", "") + "/" + relative);
        }

        return ResponseResult.success(Map.of("images", urls));
    }

    @PostMapping(value = "/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @UserLog(action = "商户上传头像", module = "shop")
    @Operation(summary = "商户上传头像")
    public ResponseResult<?> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseResult.fail("请选择头像文件");
        }

        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !contentType.startsWith("image/")) {
            return ResponseResult.fail("仅支持图片文件");
        }

        UserEntity user = UserTokenContextHolder.get();
        ZxyShopEntity zxyShopEntity = zxyShopRepository.getShopBySysId(user.getId());
        if (zxyShopEntity == null) {
            return ResponseResult.fail("商家信息不存在");
        }

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path dir = Paths.get(shopAvatarBaseDir, String.valueOf(user.getId()), datePath);
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
        String avatarUrl = shopAvatarPublicHost.replaceAll("/$", "") + "/" + relative;

        zxyShopEntity.setAvatar(avatarUrl);
        zxyShopEntity.setUpdateTime(LocalDateTime.now());
        zxyShopRepository.updateById(zxyShopEntity);

        return ResponseResult.success(Map.of("avatar", avatarUrl));
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


    private String validateCageDto(ZxyShopCageSaveDto dto, boolean forCreate) {
        if (!forCreate && (dto.getId() == null || dto.getId() <= 0)) {
            return "笼位ID不能为空";
        }
        if (!StringUtils.hasText(dto.getCode())) {
            return "笼位编号不能为空";
        }
        if (!StringUtils.hasText(dto.getSize())) {
            return "笼位规格不能为空";
        }
        if (!("S".equals(dto.getSize()) || "M".equals(dto.getSize()) || "L".equals(dto.getSize()))) {
            return "笼位规格仅支持 S/M/L";
        }
        if (dto.getPrice() == null || dto.getPrice().doubleValue() < 0) {
            return "笼位价格不能小于0";
        }
        if (StringUtils.hasText(dto.getStatus()) && !(("free".equals(dto.getStatus())) || ("occupied".equals(dto.getStatus())))) {
            return "笼位状态仅支持 free/occupied";
        }
        return null;
    }


    private String validateServiceDto(ZxyShopServiceSaveDto dto, boolean forCreate) {
        if (!forCreate && (dto.getId() == null || dto.getId() <= 0)) {
            return "服务ID不能为空";
        }
        if (!StringUtils.hasText(dto.getName())) {
            return "服务名称不能为空";
        }
        if (dto.getPrice() == null || dto.getPrice().doubleValue() < 0) {
            return "服务价格不能小于0";
        }
        return null;
    }

    private String validateStoreDto(ZxyShopStoreSaveDto dto, boolean requireName) {
        if (requireName && !StringUtils.hasText(dto.getName())) {
            return "门店名称不能为空";
        }
        if (dto.getScore() != null && (dto.getScore() < 1 || dto.getScore() > 5)) {
            return "评分范围为1-5";
        }
        return null;
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
