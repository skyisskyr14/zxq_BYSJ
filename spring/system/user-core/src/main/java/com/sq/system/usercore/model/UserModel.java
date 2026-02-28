package com.sq.system.usercore.model;

import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component("UserCoreModel")
public class UserModel {
    @Autowired
    private final UserRepository userCoreRepository;

    public UserModel(UserRepository userCoreRepository) {
        this.userCoreRepository = userCoreRepository;
    }

    public Map<String,String> register(UserEntity user) {
        Map<String,String> map = new HashMap<>();
        if (userCoreRepository.findByUsername(user.getUsername()) != null) {
            map.put("msg","用户名已存在");
            return map;
        }
        if(userCoreRepository.findByUsername(user.getPhone()) != null){
            map.put("msg","手机号已存在");
            return map;
        }

        user.setCreateTime(LocalDateTime.now());

        userCoreRepository.insert(user);

        map.put("id", String.valueOf(userCoreRepository.findByUsername(user.getUsername()).getId()));
        map.put("msg","注册成功");

        return map;
    }
}
