package com.sq.system.security.context;

public class ProjectContextHolder {

    private static final ThreadLocal<Long> PROJECT_ID_HOLDER = new ThreadLocal<>();

    public static void set(Long projectId) {
        PROJECT_ID_HOLDER.set(projectId);
    }

    public static Long get() {
        return PROJECT_ID_HOLDER.get();
    }

    public static void clear() {
        PROJECT_ID_HOLDER.remove();
    }
}
