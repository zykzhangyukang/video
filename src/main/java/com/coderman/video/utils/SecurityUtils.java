package com.coderman.video.utils;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 14:19
 */

import com.coderman.video.handler.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 安全工具类，统一封装 Spring Security 的获取当前用户方法
 */
public class SecurityUtils {

    /**
     * 获取当前用户的用户名
     */
    public static String getUsername() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }

    /**
     * 获取当前用户的用户ID（假设 LoginUser 有 userId 字段）
     */
    public static Long getUserId() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof LoginUser) {
            return ((LoginUser) principal).getId();
        }
        return null;
    }

    /**
     * 获取当前用户对象（自定义 LoginUser 类型）
     */
    public static LoginUser getLoginUser() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        }
        return null;
    }

    /**
     * 获取 Authentication 对象
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}

