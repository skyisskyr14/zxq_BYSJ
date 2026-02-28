package com.sq.system.authapp.controller.usercore;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sq.system.common.annotation.UserLog;
import com.sq.system.security.context.UserTokenContextHolder;
import com.sq.system.usercore.dto.UserLocationDto;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.repository.UserRepository;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.security.helper.SqlHelper;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.common.annotation.PermissionLimit;
import com.sq.system.usercore.repository.UserToProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "用户管理接口")
@RestController("UserCoreController")
@RequestMapping("/user-core")
public class UserCoreController {

    @Resource
    private UserRepository userRepository;
    @Resource
    private UserToProjectRepository userToProjectRepository;

    @PostConstruct
    public void init() {
        System.out.println("Cont======[2/2 system/authapp/user] UserCoreController 初始化完成 ======");
    }

    @PermissionLimit("user:list")
    @GetMapping("/list")
    @AdminLog(module = "用户核心", action = "用户列表")
    public ResponseResult<List<UserEntity>> list() {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        SqlHelper.appendProjectCondition(wrapper);
        List<UserEntity> list = userRepository.selectList(wrapper);
        return ResponseResult.success(list);
    }

    @PermissionLimit("user:create")
    @PostMapping("/create")
    @AdminLog(module = "用户核心", action = "用户创建")
    public ResponseResult<String> create(@RequestBody UserEntity user) {
//        user.setProjectId(userToProjectRepository.selectOne(
//                Wrappers.lambdaQuery(UserToProjectEntity.class)
//                        .eq(UserToProjectEntity::getUserId,ProjectContextHolder.get())
//        ).getProjectId());
//        userRepository.insert(user);
        return ResponseResult.success("添加成功");
    }

    @PermissionLimit("user:update")
    @PutMapping("/update")
    @AdminLog(module = "用户核心", action = "用户更新")
    public ResponseResult<String> update(@RequestBody UserEntity user) {
        userRepository.updateById(user);
        return ResponseResult.success("更新成功");
    }

    @PermissionLimit("user:delete")
    @DeleteMapping("/delete/{id}")
    @AdminLog(module = "用户核心", action = "用户删除")
    public ResponseResult<String> delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseResult.success("删除成功");
    }


    @UserLog(module = "用户核心",action = "用户绑定提现体制")
    @PostMapping("/location")
    @Operation(summary = "用户绑定提现体制")
    public ResponseResult<?> location(@RequestBody UserLocationDto dto) {
        UserEntity user = UserTokenContextHolder.get();
        if(user == null) {
            return ResponseResult.fail("用户不存在");
        }else{
            if(dto.getName() == null && dto.getUsdt() == null ) {
                return ResponseResult.fail("没有绑定信息");
            }else{
                if(user.getNickname() == null) {
                    if(dto.getName() != null) {
                        user.setNickname(dto.getName());
                        user.setUpdateTime(LocalDateTime.now());
                        userRepository.updateById(user);
                    }
                }
                if(user.getUsdt() == null){
                    if(dto.getUsdt() != null) {
                        user.setUsdt(dto.getUsdt());
                        user.setUpdateTime(LocalDateTime.now());
                        userRepository.updateById(user);
                    }
                }
            }
        }
        return ResponseResult.success("绑定成功");
    }
}
