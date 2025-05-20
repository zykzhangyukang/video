package com.coderman.video.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 16:12
 */
public class RequestUtils {

    public static boolean isAjax(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String xRequestedWith = request.getHeader("X-Requested-With");
        String contentType = request.getContentType();

        // 判断是否为 Ajax 或 API 请求
        return "XMLHttpRequest".equalsIgnoreCase(xRequestedWith)
                || (accept != null && (accept.contains("application/json") || accept.contains("text/json")))
                || (contentType != null && contentType.contains("application/json"));
    }

}
