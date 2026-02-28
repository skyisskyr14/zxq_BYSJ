package com.sq.system.security.context;

import com.sq.system.admincore.entity.AdminUserEntity;

public class TokenContextHolder {

    private static final ThreadLocal<AdminUserEntity> context = new ThreadLocal<>();

    public static void set(AdminUserEntity user) {
        context.set(user);
    }

    public static AdminUserEntity get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}
