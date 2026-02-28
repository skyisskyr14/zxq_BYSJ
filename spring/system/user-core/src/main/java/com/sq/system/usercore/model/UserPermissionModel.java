package com.sq.system.usercore.model;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sq.system.usercore.entity.UserToRoleEntity;
import com.sq.system.usercore.repository.*;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserPermissionModel {

    private final UserRoleRepository roleRepo;
    private final UserRolePermissionRepository rolePermRepo;
    private final UserPermissionRepository permissionRepo;
    private final UserRepository userRepo;
    private final UserToRoleRepository userToRoleRepo;

    public UserPermissionModel(UserRoleRepository roleRepo,
                               UserRolePermissionRepository rolePermRepo,
                               UserPermissionRepository permissionRepo, UserRepository userRepo, UserToRoleRepository userToRoleRepo) {
        this.roleRepo = roleRepo;
        this.rolePermRepo = rolePermRepo;
        this.permissionRepo = permissionRepo;
        this.userRepo = userRepo;
        this.userToRoleRepo = userToRoleRepo;
    }

    /**
     * 查询某管理员所拥有的权限码集合
     */
    public Set<String> getPermissionCodes(Long userId) {

        int roleId = Math.toIntExact(userToRoleRepo.selectOne(
                Wrappers.lambdaQuery(UserToRoleEntity.class)
                        .eq(UserToRoleEntity::getUserId, userRepo.findIdById(userId))
        ).getRoleId());

        List<Long> permIds = rolePermRepo.selectPermIdByRoleId(roleId);
//        if (permIds == null || permIds.isEmpty()) return Set.of();

        return new HashSet<>(permissionRepo.selectPermCodesByIds(permIds));
    }
}
