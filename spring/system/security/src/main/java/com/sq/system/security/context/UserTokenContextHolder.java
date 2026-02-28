package com.sq.system.security.context;

import com.sq.system.usercore.entity.UserEntity;

public class UserTokenContextHolder {

    private static final ThreadLocal<UserEntity> context = new ThreadLocal<>();

    public static void set(UserEntity user) {
        context.set(user);
    }

    public static UserEntity get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}
