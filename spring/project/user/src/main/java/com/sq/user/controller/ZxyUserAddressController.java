package com.sq.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sq.system.common.result.ResponseResult;
import com.sq.user.entity.ZxyUserAddressEntity;
import com.sq.user.model.ZxyUserAddressModel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-address")
@RequiredArgsConstructor
public class ZxyUserAddressController {

    @Resource
    private ZxyUserAddressModel userAddressModel;

    @PostConstruct
    public void init() {
        System.out.println("Cont======[1/3 user] ZxyUserAddressController 初始化完成 ======");
    }

    @PostMapping("/create")
    public ResponseResult<Long> create(@RequestBody ZxyUserAddressEntity entity) {
        return ResponseResult.success(userAddressModel.create(entity));
    }

    @PostMapping("/update")
    public ResponseResult<Void> update(@RequestBody ZxyUserAddressEntity entity) {
        userAddressModel.update(entity);
        return ResponseResult.success();
    }

    @PostMapping("/delete")
    public ResponseResult<Void> delete(@RequestParam Long id) {
        userAddressModel.delete(id);
        return ResponseResult.success();
    }

    @GetMapping("/detail")
    public ResponseResult<ZxyUserAddressEntity> detail(@RequestParam Long id) {
        return ResponseResult.success(userAddressModel.getById(id));
    }

    @GetMapping("/list-by-user")
    public ResponseResult<List<ZxyUserAddressEntity>> listByUser(@RequestParam Long userId) {
        return ResponseResult.success(userAddressModel.listByUserId(userId));
    }

    @GetMapping("/page")
    public ResponseResult<Page<ZxyUserAddressEntity>> page(@RequestParam Integer pageNum,
                                                           @RequestParam Integer pageSize,
                                                           @RequestParam(required = false) Long userId) {
        return ResponseResult.success(userAddressModel.page(pageNum, pageSize, userId));
    }

    @PostMapping("/set-default")
    public ResponseResult<Void> setDefault(@RequestParam Long userId, @RequestParam Long addressId) {
        userAddressModel.setDefault(userId, addressId);
        return ResponseResult.success();
    }
}