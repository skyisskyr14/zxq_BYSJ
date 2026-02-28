package com.sq.user.controller;

import com.sq.system.common.result.ResponseResult;
import com.sq.user.entity.ZxyUserProfileEntity;
import com.sq.user.model.ZxyUserProfileModel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-profile")
@RequiredArgsConstructor
public class ZxyUserProfileController {

    private final ZxyUserProfileModel userProfileModel;

    @PostConstruct
    public void init() {
        System.out.println("Cont======[3/3 user] ZxyUserProfileController 初始化完成 ======");
    }

    @GetMapping("/detail-by-user")
    public ResponseResult<ZxyUserProfileEntity> detailByUser(@RequestParam Long userId) {
        return ResponseResult.success(userProfileModel.getByUserId(userId));
    }

    @PostMapping("/save")
    public ResponseResult<Void> save(@RequestBody ZxyUserProfileEntity entity) {
        userProfileModel.saveOrUpdateByUserId(entity);
        return ResponseResult.success();
    }

    @PostMapping("/delete")
    public ResponseResult<Void> delete(@RequestParam Long id) {
        userProfileModel.delete(id);
        return ResponseResult.success();
    }
}