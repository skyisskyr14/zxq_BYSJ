package com.sq.system.admincore.model;

import com.sq.system.admincore.repository.AdminRoleRepository;
import com.sq.system.admincore.repository.RolePermissionRepository;
import com.sq.system.admincore.repository.PermissionRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 管理员权限逻辑处理模型（无 Service 层设计）
 */
@Component
public class AdminPermissionModel {

    private final AdminRoleRepository adminRoleRepo;
    private final RolePermissionRepository rolePermRepo;
    private final PermissionRepository permissionRepo;

    public AdminPermissionModel(AdminRoleRepository adminRoleRepo,
                                RolePermissionRepository rolePermRepo,
                                PermissionRepository permissionRepo) {
        this.adminRoleRepo = adminRoleRepo;
        this.rolePermRepo = rolePermRepo;
        this.permissionRepo = permissionRepo;
    }

    /**
     * 查询某管理员所拥有的权限码集合
     */
    public Set<String> getPermissionCodes(Long adminId) {
        List<Long> roleIds = adminRoleRepo.selectRoleIdsByAdminId(adminId);
        if (roleIds == null || roleIds.isEmpty()) return Set.of();

        List<Long> permIds = rolePermRepo.selectPermIdsByRoleIds(roleIds);
        if (permIds == null || permIds.isEmpty()) return Set.of();

        return new HashSet<>(permissionRepo.selectPermCodesByIds(permIds));
    }
}
