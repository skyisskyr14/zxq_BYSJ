package com.sq.pet.controller;

import com.sq.pet.dto.ZxyPetSaveDto;
import com.sq.pet.model.ZxyPetModel;
import com.sq.pet.vo.ZxyPetVo;
import com.sq.system.common.annotation.UserLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.security.context.UserTokenContextHolder;
import com.sq.system.usercore.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ZxyPetController")
@RequestMapping("/fd/pet")
@Tag(name = "SS--宠物信息管理接口")
public class ZxyPetController {

    @Resource
    private ZxyPetModel petModel;

    @GetMapping("/list")
    @UserLog(action = "用户查看宠物列表", module = "pet")
    @Operation(summary = "宠物列表")
    public ResponseResult<?> list() {
        Long userId = currentBizUserId();
        if (userId == null) {
            return ResponseResult.fail("用户不存在");
        }
        List<ZxyPetVo> data = petModel.list(userId);
        return ResponseResult.success(data);
    }

    @GetMapping("/detail")
    @UserLog(action = "用户查看宠物详情", module = "pet")
    @Operation(summary = "宠物详情")
    public ResponseResult<?> detail(@RequestParam Long id) {
        Long userId = currentBizUserId();
        if (userId == null) {
            return ResponseResult.fail("用户不存在");
        }
        ZxyPetVo vo = petModel.detail(userId, id);
        if (vo == null) {
            return ResponseResult.fail("宠物不存在");
        }
        return ResponseResult.success(vo);
    }

    @PostMapping("/create")
    @UserLog(action = "用户新增宠物", module = "pet")
    @Operation(summary = "新增宠物")
    public ResponseResult<?> create(@RequestBody ZxyPetSaveDto dto) {
        String validMessage = validatePetDto(dto, false);
        if (validMessage != null) {
            return ResponseResult.fail(validMessage);
        }
        Long userId = currentBizUserId();
        if (userId == null) {
            return ResponseResult.fail("用户不存在");
        }
        if (petModel.create(userId, dto)) {
            return ResponseResult.success("创建成功");
        }
        return ResponseResult.fail("创建失败");
    }

    @PostMapping("/update")
    @UserLog(action = "用户编辑宠物", module = "pet")
    @Operation(summary = "编辑宠物")
    public ResponseResult<?> update(@RequestBody ZxyPetSaveDto dto) {
        String validMessage = validatePetDto(dto, true);
        if (validMessage != null) {
            return ResponseResult.fail(validMessage);
        }
        Long userId = currentBizUserId();
        if (userId == null) {
            return ResponseResult.fail("用户不存在");
        }
        if (petModel.update(userId, dto)) {
            return ResponseResult.success("修改成功");
        }
        return ResponseResult.fail("宠物不存在或无变更");
    }

    @PostMapping("/delete")
    @UserLog(action = "用户删除宠物", module = "pet")
    @Operation(summary = "删除宠物")
    public ResponseResult<?> delete(@RequestParam Long id) {
        Long userId = currentBizUserId();
        if (userId == null) {
            return ResponseResult.fail("用户不存在");
        }
        if (petModel.delete(userId, id)) {
            return ResponseResult.success("删除成功");
        }
        return ResponseResult.fail("宠物不存在");
    }

    private Long currentBizUserId() {
        UserEntity user = UserTokenContextHolder.get();
        return petModel.getBizUserIdBySysUser(user);
    }

    private String validatePetDto(ZxyPetSaveDto dto, boolean requireId) {
        if (requireId && dto.getId() == null) {
            return "缺少宠物ID";
        }
        if (!StringUtils.hasText(dto.getName())) {
            return "宠物名称不能为空";
        }
        if (dto.getType() == null || (dto.getType() != 1 && dto.getType() != 2)) {
            return "宠物类型错误";
        }
        if (dto.getAge() == null || dto.getAge() <= 0) {
            return "宠物年龄必须大于0";
        }
        if (dto.getWeight() == null || dto.getWeight() <= 0) {
            return "宠物体重必须大于0";
        }
        return null;
    }
}
