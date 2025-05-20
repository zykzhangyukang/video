package com.coderman.video.handler;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 11:52
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;


/**
 * 登录失败处理器
 */
@Slf4j
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String errorMsg;

        if (exception instanceof LockedException) {
            errorMsg = "账号已被锁定，请联系管理员";
        } else if (exception instanceof DisabledException) {
            errorMsg = "账号已被禁用";
        } else if (exception instanceof AccountExpiredException) {
            errorMsg = "账号已过期";
        } else if (exception instanceof CredentialsExpiredException) {
            errorMsg = "密码已过期，请修改密码";
        } else if (exception instanceof BadCredentialsException) {
            errorMsg = "用户名或密码错误";
        } else {
            errorMsg = "登录失败：" + exception.getMessage();
        }

        // 重定向带上错误信息参数
        response.sendRedirect(request.getContextPath() + "/login?error=" + URLEncoder.encode(errorMsg, "UTF-8"));
    }

}

