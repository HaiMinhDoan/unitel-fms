package com.unitel.fms.backend.contexts;

import com.unitel.fms.backend.dtos.AuthInfo;

import java.util.UUID;

public class SecurityContextHolder {
    private static final ThreadLocal<String> path = new ThreadLocal<>();
    private static final ThreadLocal<String> lang = new ThreadLocal<>();
    private static final ThreadLocal<UUID> orgId = new ThreadLocal<>();
    private static final ThreadLocal<AuthInfo> authInfo = new ThreadLocal<>();

    public static void setPath(String path) {
        SecurityContextHolder.path.set(path);
    }

    public static void setLang(String lang) {
        SecurityContextHolder.lang.set(lang);
    }

    public static void setOrgId(UUID orgId) {
        SecurityContextHolder.orgId.set(orgId);
    }

    public static void setAuthInfo(AuthInfo authInfo) {
        SecurityContextHolder.authInfo.set(authInfo);
    }

    public static String getPath() {
        return path.get();
    }

    public static String getLang() {
        return lang.get();
    }

    public static UUID getOrgId() {
        return orgId.get();
    }

    public static AuthInfo getAuthInfo() {
        return authInfo.get();
    }

    public static void clear() {
        path.remove();
        lang.remove();
        orgId.remove();
        authInfo.remove();
    }
}
